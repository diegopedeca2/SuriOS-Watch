package com.suri.pipsurios.pft

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import java.util.UUID
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

enum class PftS3ConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class PftS3Status(
    val state: PftS3ConnectionState,
    val detail: String? = null
)

/** BLE client for the PFT-SuriOS S3 service; independent from the radar client. */
class PftS3BleClient(context: Context) {
    private val appContext = context.applicationContext
    private val bluetoothAdapter: BluetoothAdapter? =
        appContext.getSystemService(android.bluetooth.BluetoothManager::class.java)?.adapter
    private val handler = Handler(Looper.getMainLooper())
    private val _status = MutableStateFlow(PftS3Status(PftS3ConnectionState.DISCONNECTED))
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 64)
    private val _packets = MutableSharedFlow<ByteArray>(extraBufferCapacity = 64)

    val status = _status.asStateFlow()
    val messages = _messages.asSharedFlow()
    val packets = _packets.asSharedFlow()

    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null
    private var eventCharacteristic: BluetoothGattCharacteristic? = null
    private var commandCharacteristic: BluetoothGattCharacteristic? = null
    private var running = false

    private val adapterStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
            handler.post {
                when (state) {
                    BluetoothAdapter.STATE_TURNING_OFF,
                    BluetoothAdapter.STATE_OFF -> {
                        stopScan()
                        closeGatt(gatt)
                        setStatus(PftS3ConnectionState.DISCONNECTED, "BLUETOOTH_OFF")
                    }

                    BluetoothAdapter.STATE_ON -> if (running) startScan()
                }
            }
        }
    }

    private val scanTimeout = Runnable {
        stopScan()
        if (running) scheduleReconnect("S3_NOT_FOUND")
    }

    private val reconnect = Runnable {
        if (running) startScan()
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val announcedName = result.scanRecord?.deviceName ?: result.device.name
            val announcesService = result.scanRecord?.serviceUuids?.any { parcelUuid ->
                parcelUuid.uuid == SERVICE_UUID
            } == true
            if ((announcedName == DEVICE_NAME || announcesService) && gatt == null) {
                stopScan()
                connect(result.device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            stopScan()
            setStatus(PftS3ConnectionState.ERROR, "SCAN_$errorCode")
            if (running) scheduleReconnect("SCAN_FAILED")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            handler.post {
                if (!running) {
                    closeGatt(gatt)
                    return@post
                }
                if (this@PftS3BleClient.gatt != null && this@PftS3BleClient.gatt !== gatt) {
                    closeGatt(gatt)
                    return@post
                }
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    this@PftS3BleClient.gatt = gatt
                    eventCharacteristic = null
                    commandCharacteristic = null
                    setStatus(PftS3ConnectionState.CONNECTING, "DISCOVERING_SERVICES")
                    discoverServices(gatt)
                } else {
                    closeGatt(gatt)
                    setStatus(PftS3ConnectionState.DISCONNECTED, "CONNECTION_LOST_$status")
                    if (running) scheduleReconnect("CONNECTION_LOST")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            handler.post {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    closeGatt(gatt)
                    setStatus(PftS3ConnectionState.ERROR, "SERVICES_$status")
                    if (running) scheduleReconnect("SERVICES_FAILED")
                    return@post
                }
                enableNotifications(gatt)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            handler.post {
                if (descriptor.uuid != CLIENT_CHARACTERISTIC_CONFIG_UUID) return@post
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    setStatus(PftS3ConnectionState.CONNECTED, "NOTIFICATIONS_ENABLED")
                } else {
                    closeGatt(gatt)
                    setStatus(PftS3ConnectionState.ERROR, "DESCRIPTOR_$status")
                    if (running) scheduleReconnect("NOTIFICATIONS_FAILED")
                }
            }
        }

        @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            publishCharacteristic(characteristic.uuid, characteristic.value)
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray
        ) {
            publishCharacteristic(characteristic.uuid, value)
        }
    }

    init {
        appContext.registerReceiver(
            adapterStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    fun hasRequiredPermissions(): Boolean = listOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION
    ).all { permission ->
        ContextCompat.checkSelfPermission(appContext, permission) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun start() {
        if (!hasRequiredPermissions()) {
            setStatus(PftS3ConnectionState.ERROR, "PERMISSIONS_REQUIRED")
            return
        }
        if (running) return
        running = true
        handler.removeCallbacks(reconnect)
        closeGatt(gatt)
        startScan()
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        running = false
        handler.removeCallbacks(scanTimeout)
        handler.removeCallbacks(reconnect)
        stopScan()
        closeGatt(gatt)
        gatt = null
        setStatus(PftS3ConnectionState.DISCONNECTED)
    }

    @SuppressLint("MissingPermission")
    fun sendCommand(command: String): Boolean {
        val activeGatt = gatt ?: return false
        val characteristic = commandCharacteristic ?: return false
        val value = (command.trim() + "\n").toByteArray(Charsets.UTF_8)
        val result = activeGatt.writeCharacteristic(
            characteristic,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        )
        return result == BluetoothGatt.GATT_SUCCESS
    }

    fun close() {
        stop()
        runCatching { appContext.unregisterReceiver(adapterStateReceiver) }
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        if (!running) return
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            setStatus(PftS3ConnectionState.ERROR, "BLUETOOTH_UNAVAILABLE")
            scheduleReconnect("BLUETOOTH_UNAVAILABLE")
            return
        }

        scanner = adapter.bluetoothLeScanner
        if (scanner == null) {
            setStatus(PftS3ConnectionState.ERROR, "SCANNER_UNAVAILABLE")
            scheduleReconnect("SCANNER_UNAVAILABLE")
            return
        }

        setStatus(PftS3ConnectionState.SCANNING)
        handler.removeCallbacks(scanTimeout)
        scanner?.startScan(
            null,
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            scanCallback
        )
        handler.postDelayed(scanTimeout, SCAN_TIMEOUT_MILLIS)
    }

    @SuppressLint("MissingPermission")
    private fun stopScan() {
        handler.removeCallbacks(scanTimeout)
        scanner?.stopScan(scanCallback)
        scanner = null
    }

    @SuppressLint("MissingPermission")
    @Suppress("DEPRECATION")
    private fun connect(device: BluetoothDevice) {
        if (!running) return
        setStatus(PftS3ConnectionState.CONNECTING, device.address)
        gatt = device.connectGatt(appContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices(gatt: BluetoothGatt) {
        if (!gatt.discoverServices()) {
            closeGatt(gatt)
            setStatus(PftS3ConnectionState.ERROR, "DISCOVER_SERVICES_REJECTED")
            if (running) scheduleReconnect("DISCOVER_SERVICES_REJECTED")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt) {
        val service = gatt.getService(SERVICE_UUID)
        val event = service?.getCharacteristic(EVENT_CHARACTERISTIC_UUID)
        commandCharacteristic = service?.getCharacteristic(COMMAND_CHARACTERISTIC_UUID)
        val descriptor = event?.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        if (event == null || commandCharacteristic == null || descriptor == null) {
            closeGatt(gatt)
            setStatus(PftS3ConnectionState.ERROR, "S3_CHARACTERISTIC_MISSING")
            if (running) scheduleReconnect("CHARACTERISTIC_MISSING")
            return
        }

        eventCharacteristic = event
        if (!gatt.setCharacteristicNotification(event, true)) {
            closeGatt(gatt)
            setStatus(PftS3ConnectionState.ERROR, "NOTIFICATION_REJECTED")
            if (running) scheduleReconnect("NOTIFICATION_REJECTED")
            return
        }

        @Suppress("DEPRECATION")
        run {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            if (!gatt.writeDescriptor(descriptor)) {
                closeGatt(gatt)
                setStatus(PftS3ConnectionState.ERROR, "DESCRIPTOR_WRITE_REJECTED")
                if (running) scheduleReconnect("DESCRIPTOR_WRITE_REJECTED")
            }
        }
    }

    private fun publishCharacteristic(uuid: UUID, value: ByteArray?) {
        if (uuid == EVENT_CHARACTERISTIC_UUID && value != null) {
            val copy = value.copyOf()
            if (copy.size == 17 && (copy[0].toInt() and 0xFF) == 1) {
                _packets.tryEmit(copy)
            } else {
                _messages.tryEmit(copy.toString(Charsets.UTF_8).trim())
            }
        }
    }

    private fun scheduleReconnect(detail: String) {
        if (!running) return
        handler.removeCallbacks(reconnect)
        setStatus(PftS3ConnectionState.DISCONNECTED, detail)
        handler.postDelayed(reconnect, RECONNECT_DELAY_MILLIS)
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt(candidate: BluetoothGatt?) {
        candidate?.let {
            runCatching { it.disconnect() }
            runCatching { it.close() }
        }
        if (gatt === candidate) gatt = null
        if (this.gatt == null) {
            eventCharacteristic = null
            commandCharacteristic = null
        }
    }

    private fun setStatus(state: PftS3ConnectionState, detail: String? = null) {
        _status.value = PftS3Status(state, detail)
    }

    companion object {
        const val DEVICE_NAME = "PFT-SuriOS S3"
        val SERVICE_UUID: UUID = UUID.fromString("9e5d1001-7b3a-4c0f-91c2-245000000001")
        val EVENT_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d1002-7b3a-4c0f-91c2-245000000001")
        val COMMAND_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d1003-7b3a-4c0f-91c2-245000000001")
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val SCAN_TIMEOUT_MILLIS = 10_000L
        private const val RECONNECT_DELAY_MILLIS = 1_500L
    }
}
