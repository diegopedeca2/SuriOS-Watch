package com.suri.suriosradar.ble

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
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.os.Build
import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

enum class BleConnectionState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    ERROR
}

data class BleStatus(
    val state: BleConnectionState,
    val detail: String? = null
)

class RadarBleClient(
    context: Context,
    private val dataCharacteristicUuid: UUID = DATA_CHARACTERISTIC_UUID
) {
    private val appContext = context.applicationContext
    private val bluetoothAdapter: BluetoothAdapter? =
        appContext.getSystemService(android.bluetooth.BluetoothManager::class.java)?.adapter
    private val handler = Handler(Looper.getMainLooper())
    private val _status = MutableStateFlow(BleStatus(BleConnectionState.DISCONNECTED))
    private val _packets = MutableSharedFlow<ByteArray>(extraBufferCapacity = 32)

    val status = _status.asStateFlow()
    val packets = _packets.asSharedFlow()

    private var scanner: BluetoothLeScanner? = null
    private var gatt: BluetoothGatt? = null
    private var controlCharacteristic: BluetoothGattCharacteristic? = null
    private var notificationsConfigured = false
    private var running = false

    private val mtuFallback = Runnable {
        gatt?.let { enableNotificationsOnce(it) }
    }

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
                        setStatus(BleConnectionState.DISCONNECTED, "bluetooth_off")
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
        if (running) scheduleReconnect("radar_not_found")
    }

    private val reconnect = Runnable {
        if (running) startScan()
    }

    private val scanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val record = result.scanRecord
            val announcedName = record?.deviceName ?: result.device.name
            val serviceAdvertised = record?.serviceUuids?.any { it.uuid == SERVICE_UUID } == true
            val nameMatches = announcedName == RADAR_NAME || announcedName?.startsWith(RADAR_NAME) == true
            Log.d(
                TAG,
                "BLE scan address=${result.device.address} name=$announcedName " +
                    "service=$serviceAdvertised rssi=${result.rssi}"
            )
            if ((nameMatches || serviceAdvertised) && gatt == null) {
                stopScan()
                connect(result.device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            stopScan()
            setStatus(BleConnectionState.ERROR, "scan_$errorCode")
            if (running) scheduleReconnect("scan_failed")
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
                    notificationsConfigured = false
                    setStatus(BleConnectionState.CONNECTING, "discovering_services")
                    discoverServices(gatt)
                } else {
                    closeGatt(gatt)
                    setStatus(BleConnectionState.DISCONNECTED, "connection_lost_$status")
                    if (running) scheduleReconnect("connection_lost")
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            handler.post {
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    closeGatt(gatt)
                    setStatus(BleConnectionState.ERROR, "services_$status")
                    if (running) scheduleReconnect("services_failed")
                    return@post
                }
                // Radar v2 sends 29-byte frames. Request enough ATT payload
                // before subscribing; the fallback keeps v1 compatible with
                // stacks that reject MTU negotiation.
                handler.removeCallbacks(mtuFallback)
                val mtuRequested = runCatching { gatt.requestMtu(64) }.getOrDefault(false)
                if (!mtuRequested) {
                    enableNotificationsOnce(gatt)
                } else {
                    handler.postDelayed(mtuFallback, MTU_FALLBACK_MILLIS)
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            handler.post {
                handler.removeCallbacks(mtuFallback)
                enableNotificationsOnce(gatt)
            }
        }

        override fun onDescriptorWrite(
            gatt: BluetoothGatt,
            descriptor: BluetoothGattDescriptor,
            status: Int
        ) {
            handler.post {
                if (descriptor.uuid != CLIENT_CHARACTERISTIC_CONFIG_UUID) return@post
                Log.d(TAG, "BLE descriptor write uuid=${descriptor.uuid} status=$status")
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    setStatus(BleConnectionState.CONNECTED, "notifications_enabled")
                } else {
                    closeGatt(gatt)
                    setStatus(BleConnectionState.ERROR, "descriptor_$status")
                    if (running) scheduleReconnect("notifications_failed")
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

    @SuppressLint("MissingPermission")
    fun start() {
        if (running) return
        Log.i(TAG, "BLE client start")
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
        handler.removeCallbacks(mtuFallback)
        stopScan()
        closeGatt(gatt)
        gatt = null
        setStatus(BleConnectionState.DISCONNECTED)
    }

    fun close() {
        stop()
        runCatching { appContext.unregisterReceiver(adapterStateReceiver) }
    }

    @SuppressLint("MissingPermission")
    fun setEcoMode(enabled: Boolean): Boolean {
        return writeControlByte(if (enabled) CONTROL_ECO else CONTROL_NORMAL)
    }

    @SuppressLint("MissingPermission")
    fun writeControlByte(command: Byte): Boolean {
        val activeGatt = gatt ?: return false
        val control = controlCharacteristic ?: return false
        val value = byteArrayOf(command)
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
        Log.i(TAG, "BLE scan requested")
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            setStatus(BleConnectionState.ERROR, "bluetooth_unavailable")
            scheduleReconnect("bluetooth_unavailable")
            return
        }

        scanner = adapter.bluetoothLeScanner
        if (scanner == null) {
            setStatus(BleConnectionState.ERROR, "scanner_unavailable")
            scheduleReconnect("scanner_unavailable")
            return
        }

        setStatus(BleConnectionState.SCANNING)
        handler.removeCallbacks(scanTimeout)
        // ESPHome may put the local name in the scan response. Some Wear OS
        // stacks do not apply a device-name filter to that response, so use an
        // unfiltered scan and match name/service UUID in onScanResult().
        scanner?.startScan(
            null,
            ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
            scanCallback
        )
        Log.i(TAG, "BLE scan started")
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
        setStatus(BleConnectionState.CONNECTING, device.address)
        gatt = device.connectGatt(appContext, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    private fun discoverServices(gatt: BluetoothGatt) {
        if (!gatt.discoverServices()) {
            closeGatt(gatt)
            setStatus(BleConnectionState.ERROR, "discover_services_rejected")
            if (running) scheduleReconnect("discover_services_rejected")
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableNotificationsOnce(gatt: BluetoothGatt) {
        if (notificationsConfigured) return
        notificationsConfigured = true
        val service = gatt.getService(SERVICE_UUID)
        val characteristic = service?.getCharacteristic(dataCharacteristicUuid)
        controlCharacteristic = service?.getCharacteristic(CONTROL_CHARACTERISTIC_UUID)
        val descriptor = characteristic?.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
        if (characteristic == null || descriptor == null) {
            closeGatt(gatt)
            setStatus(BleConnectionState.ERROR, "radar_characteristic_missing")
            if (running) scheduleReconnect("characteristic_missing")
            return
        }

        if (!gatt.setCharacteristicNotification(characteristic, true)) {
            closeGatt(gatt)
            setStatus(BleConnectionState.ERROR, "notification_rejected")
            if (running) scheduleReconnect("notification_rejected")
            return
        }

        val writeResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            gatt.writeDescriptor(descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        } else {
            @Suppress("DEPRECATION")
            run {
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                if (gatt.writeDescriptor(descriptor)) {
                    BluetoothGatt.GATT_SUCCESS
                } else {
                    BluetoothGatt.GATT_FAILURE
                }
            }
        }
        Log.d(TAG, "BLE descriptor write requested result=$writeResult")
        if (writeResult != BluetoothGatt.GATT_SUCCESS) {
                closeGatt(gatt)
                setStatus(BleConnectionState.ERROR, "descriptor_write_rejected")
                if (running) scheduleReconnect("descriptor_write_rejected")
        }
    }

    private fun publishCharacteristic(uuid: UUID, value: ByteArray?) {
        if (uuid != dataCharacteristicUuid || value == null) return
        _packets.tryEmit(value.copyOf())
    }

    private fun scheduleReconnect(detail: String) {
        if (!running) return
        handler.removeCallbacks(reconnect)
        setStatus(BleConnectionState.DISCONNECTED, detail)
        handler.postDelayed(reconnect, RECONNECT_DELAY_MILLIS)
    }

    @SuppressLint("MissingPermission")
    private fun closeGatt(candidate: BluetoothGatt?) {
        candidate?.let {
            runCatching { it.disconnect() }
            runCatching { it.close() }
        }
        if (gatt === candidate) {
            gatt = null
            controlCharacteristic = null
            notificationsConfigured = false
        }
    }

    private fun setStatus(state: BleConnectionState, detail: String? = null) {
        _status.value = BleStatus(state, detail)
    }

    companion object {
        const val RADAR_NAME = "SuriOS Radar"
        val SERVICE_UUID: UUID = UUID.fromString("9e5d0001-7b3a-4c0f-91c2-245000000001")
        val DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0002-7b3a-4c0f-91c2-245000000001")
        val V2_DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0004-7b3a-4c0f-91c2-245000000001")
        val MINI_DATA_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0005-7b3a-4c0f-91c2-245000000001")
        val CONTROL_CHARACTERISTIC_UUID: UUID = UUID.fromString("9e5d0003-7b3a-4c0f-91c2-245000000001")
        private const val CONTROL_NORMAL: Byte = 0x00
        private const val CONTROL_ECO: Byte = 0x01
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID: UUID =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val SCAN_TIMEOUT_MILLIS = 10_000L
        private const val RECONNECT_DELAY_MILLIS = 1_500L
        private const val MTU_FALLBACK_MILLIS = 1_500L
        private const val TAG = "RadarBleClient"
    }
}
