package com.suri.pipsurios.detector

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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class RadarConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class RadarBleStatus(
    val state: RadarConnectionState,
    val detail: String? = null
)

class RadarBleClient(context: Context) {
    private val appContext = context.applicationContext
    private val bluetoothAdapter: BluetoothAdapter? =
        appContext.getSystemService(android.bluetooth.BluetoothManager::class.java)?.adapter
    private val handler = Handler(Looper.getMainLooper())
    private val _status = MutableStateFlow(RadarBleStatus(RadarConnectionState.DISCONNECTED))
    private val _packets = MutableSharedFlow<ByteArray>(extraBufferCapacity = 32)

    val status = _status.asStateFlow()
    val packets = _packets.asSharedFlow()

    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null
    private var controlCharacteristic: BluetoothGattCharacteristic? = null
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
                        setStatus(RadarConnectionState.DISCONNECTED, "BLUETOOTH_OFF")
                    }

                    BluetoothAdapter.STATE_ON -> if (running) startScan()
                }
            }
        }
    }

    init {
        appContext.registerReceiver(
            adapterStateReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    private val scanTimeout = Runnable {
        stopScan()
        if (running) scheduleReconnect("RADAR_NOT_FOUND")
    }

    private val reconnect = Runnable {
        if (running) startScan()
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val announcedName = result.scanRecord?.deviceName ?: result.device.name
            val announcesRadarService = result.scanRecord?.serviceUuids?.any { parcelUuid ->
                parcelUuid.uuid == SERVICE_UUID
            } == true
            if ((announcedName == RADAR_NAME || announcesRadarService) && gatt == null) {
                stopScan()
                connect(result.device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            stopScan()
            setStatus(RadarConnectionState.ERROR, "SCAN_$errorCode")
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
                if (this@RadarBleClient.gatt != null && this@RadarBleClient.gatt !== gatt) {
                    closeGatt(gatt)
                    return@post
                }
                if (status == BluetoothGatt.GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                    this@RadarBleClient.gatt = gatt
                    controlCharacteristic = null
                    setStatus(RadarConnectionState.CONNECTING, "DISCOVERING_SERVICES")
                    discoverServices(gatt)
                } else {
                    closeGatt(gatt)
                    setStatus(RadarConnectionState.DISCONNECTED, "CONNECTION_LOST_$status")
                    if (running) scheduleReconnect("CONNECTION_LOST")
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            handler.post {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    closeGatt(gatt)
                    setStatus(RadarConnectionState.ERROR, "SERVICES_$status")
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
                    setStatus(RadarConnectionState.CONNECTED, "NOTIFICATIONS_ENABLED")
                } else {
                    closeGatt(gatt)
                    setStatus(RadarConnectionState.ERROR, "DESCRIPTOR_$status")
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
            setStatus(RadarConnectionState.ERROR, "PERMISSIONS_REQUIRED")
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
        setStatus(RadarConnectionState.DISCONNECTED)
    }

    fun close() {
        stop()
        runCatching { appContext.unregisterReceiver(adapterStateReceiver) }
    }

    @SuppressLint("MissingPermission")
    fun setEcoMode(enabled: Boolean): Boolean {
        val activeGatt = gatt ?: return false
        val control = controlCharacteristic ?: return false
        val value = byteArrayOf(if (enabled) CONTROL_ECO else CONTROL_NORMAL)
        val result = activeGatt.writeCharacteristic(
            control,
            value,
            BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        )
        return result == BluetoothGatt.GATT_SUCCESS
    }

    @SuppressLint("MissingPermission")
    private fun startScan() {
        if (!running) return
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            setStatus(RadarConnectionState.ERROR, "BLUETOOTH_UNAVAILABLE")
            scheduleReconnect("BLUETOOTH_UNAVAILABLE")
            return
        }

        scanner = adapter.bluetoothLeScanner
        if (scanner == null) {
            setStatus(RadarConnectionState.ERROR, "SCANNER_UNAVAILABLE")
            scheduleReconnect("SCANNER_UNAVAILABLE")
            return
        }

        setStatus(RadarConnectionState.SCANNING)
        handler.removeCallbacks(scanTimeout)
        scanner?.startScan(
            // No filtramos solo por nombre: NimBLE puede publicar el nombre en
            // el scan response. Reconocemos el radar en onScanResult por nombre
            // o por su UUID de servicio.
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
        setStatus(RadarConnectionState.CONNECTING, device.address)
        gatt = device.connectGatt(appContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices(gatt: BluetoothGatt) {
        if (!gatt.discoverServices()) {
            closeGatt(gatt)
            setStatus(RadarConnectionState.ERROR, "DISCOVER_SERVICES_REJECTED")
            if (running) scheduleReconnect("DISCOVER_SERVICES_REJECTED")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotifications(gatt: BluetoothGatt) {
        val service = gatt.getService(SERVICE_UUID)
        val characteristic = service?.getCharacteristic(DATA_CHARACTERISTIC_UUID)
        controlCharacteristic = service?.getCharacteristic(CONTROL_CHARACTERISTIC_UUID)
        val descriptor = characteristic?.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        if (characteristic == null || descriptor == null) {
            closeGatt(gatt)
            setStatus(RadarConnectionState.ERROR, "RADAR_CHARACTERISTIC_MISSING")
            if (running) scheduleReconnect("CHARACTERISTIC_MISSING")
            return
        }

        if (!gatt.setCharacteristicNotification(characteristic, true)) {
            closeGatt(gatt)
            setStatus(RadarConnectionState.ERROR, "NOTIFICATION_REJECTED")
            if (running) scheduleReconnect("NOTIFICATION_REJECTED")
            return
        }

        @Suppress("DEPRECATION")
        run {
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            if (!gatt.writeDescriptor(descriptor)) {
                closeGatt(gatt)
                setStatus(RadarConnectionState.ERROR, "DESCRIPTOR_WRITE_REJECTED")
                if (running) scheduleReconnect("DESCRIPTOR_WRITE_REJECTED")
            }
        }
    }

    private fun publishCharacteristic(uuid: UUID, value: ByteArray?) {
        if (uuid == DATA_CHARACTERISTIC_UUID && value != null) {
            _packets.tryEmit(value.copyOf())
        }
    }

    private fun scheduleReconnect(detail: String) {
        if (!running) return
        handler.removeCallbacks(reconnect)
        setStatus(RadarConnectionState.DISCONNECTED, detail)
        handler.postDelayed(reconnect, RECONNECT_DELAY_MILLIS)
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt(candidate: BluetoothGatt?) {
        candidate?.let {
            runCatching { it.disconnect() }
            runCatching { it.close() }
        }
        if (gatt === candidate) gatt = null
        if (this.gatt == null) controlCharacteristic = null
    }

    private fun setStatus(state: RadarConnectionState, detail: String? = null) {
        _status.value = RadarBleStatus(state, detail)
    }

    companion object {
        const val RADAR_NAME = "SuriOS Radar"
        val SERVICE_UUID: UUID = UUID.fromString("9e5d0001-7b3a-4c0f-91c2-245000000001")
        val DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0002-7b3a-4c0f-91c2-245000000001")
        val CONTROL_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0003-7b3a-4c0f-91c2-245000000001")
        private const val CONTROL_NORMAL: Byte = 0x00
        private const val CONTROL_ECO: Byte = 0x01
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val SCAN_TIMEOUT_MILLIS = 10_000L
        private const val RECONNECT_DELAY_MILLIS = 1_500L
    }
}
