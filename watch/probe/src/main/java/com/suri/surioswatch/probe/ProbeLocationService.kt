package com.suri.surioswatch.probe

import android.annotation.SuppressLint
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ServiceInfo
import android.location.Location
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import com.suri.probeprotocol.ProbeProtocol
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/** Headless PROBE node: location + BLE observations sent live to the controlling phone. */
class ProbeLocationService : Service() {
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var messageClient: MessageClient
    private val handler = Handler(Looper.getMainLooper())
    private val pendingBle = linkedMapOf<String, ScanResult>()
    private var phoneNodeId: String? = null
    private var sessionId: String? = null
    private var sequence = 0L
    private var running = false
    private val telemetryInFlight = AtomicInteger(0)
    private val transportFailed = AtomicBoolean(false)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let(::publishLocation)
        }
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            synchronized(pendingBle) {
                if (!pendingBle.containsKey(result.device.address) && pendingBle.size >= MAX_PENDING_BLE) {
                    pendingBle.remove(pendingBle.keys.first())
                }
                pendingBle[result.device.address] = result
            }
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            results.forEach { onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, it) }
        }

        override fun onScanFailed(errorCode: Int) {
            publishStatus("ERROR", "BLE_SCAN_$errorCode")
        }
    }

    private val flushRunnable = object : Runnable {
        override fun run() {
            flushBle()
            if (running) handler.postDelayed(this, BLE_SAMPLE_INTERVAL_MILLIS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = LocationServices.getFusedLocationProviderClient(this)
        messageClient = Wearable.getMessageClient(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return when (intent?.action) {
            ACTION_STOP -> {
                val requestedPhone = intent.getStringExtra(EXTRA_PHONE_NODE_ID)
                val requestedSession = intent.getStringExtra(EXTRA_SESSION_ID)
                if (running && (requestedPhone != phoneNodeId || requestedSession != sessionId)) {
                    return START_NOT_STICKY
                }
                stopTracking()
                stopSelf()
                START_NOT_STICKY
            }
            else -> {
                intent?.getStringExtra(EXTRA_PHONE_NODE_ID)?.let { phoneNodeId = it }
                intent?.getStringExtra(EXTRA_SESSION_ID)?.let { sessionId = it }
                startForegroundCompat()
                startTracking()
                START_NOT_STICKY
            }
        }
    }

    override fun onDestroy() {
        stopTracking()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?) = null

    @SuppressLint("MissingPermission")
    private fun startTracking() {
        if (running) stopTracking()
        if (phoneNodeId.isNullOrBlank() || sessionId.isNullOrBlank()) {
            ProbeRuntimeState.update("ERROR", "PHONE_LINK_REQUIRED")
            stopSelf()
            return
        }
        if (!hasLocationPermission() || !hasBluetoothPermission()) {
            publishStatus("ERROR", "PERMISSION_REQUIRED")
            stopSelf()
            return
        }
        transportFailed.set(false)
        sequence = 0L
        running = true
        publishStatus("ACTIVE", null)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, LOCATION_INTERVAL_MILLIS)
            .setMinUpdateIntervalMillis(LOCATION_MIN_INTERVAL_MILLIS)
            .setWaitForAccurateLocation(false)
            .build()
        runCatching {
            locationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
            val bleScanner = getBluetoothLeScanner() ?: error("BLE_SCANNER_UNAVAILABLE")
            bleScanner.startScan(
                emptyList(),
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build(),
                scanCallback
            )
            handler.post(flushRunnable)
        }.onFailure {
            running = false
            publishStatus("ERROR", it.javaClass.simpleName)
            stopSelf()
        }
    }

    @SuppressLint("MissingPermission")
    private fun stopTracking() {
        if (!running) return
        running = false
        handler.removeCallbacks(flushRunnable)
        if (::locationClient.isInitialized) locationClient.removeLocationUpdates(locationCallback)
        runCatching { getBluetoothLeScanner()?.stopScan(scanCallback) }
        publishStatus("STOPPED", null)
    }

    private fun publishLocation(location: Location) {
        if (!location.hasAccuracy() || !location.latitude.isFinite() || !location.longitude.isFinite()) return
        val sample = ProbeProtocol.LocationSample(
            probeId = PROBE_ID,
            sessionId = sessionId!!,
            sequence = nextSequence(),
            timestampEpochMillis = location.time.takeIf { it > 0L } ?: System.currentTimeMillis(),
            latitude = location.latitude,
            longitude = location.longitude,
            accuracyMeters = location.accuracy,
            provider = location.provider,
            batteryPercent = batteryPercent()
        )
        sendTelemetry(ProbeProtocol.LOCATION_PATH, ProbeProtocol.encodeLocation(sample))
    }

    @SuppressLint("MissingPermission")
    private fun flushBle() {
        val results = synchronized(pendingBle) {
            val current = pendingBle.values.toList()
            pendingBle.clear()
            current
        }
        results.forEach { result ->
            val sample = ProbeProtocol.BleSample(
                probeId = PROBE_ID,
                sessionId = sessionId!!,
                sequence = nextSequence(),
                timestampEpochMillis = System.currentTimeMillis(),
                temporaryId = "WATCH-${result.device.address}",
                deviceIdentifier = result.device.address.uppercase(Locale.US),
                deviceName = result.scanRecord?.deviceName,
                rssi = result.rssi,
                advertisingDataHex = result.scanRecord?.bytes?.toHexString(),
                deviceType = runCatching { result.device.type }.getOrNull()
            )
            sendTelemetry(ProbeProtocol.BLE_PATH, ProbeProtocol.encodeBle(sample))
        }
    }

    private fun publishStatus(state: String, message: String?) {
        ProbeRuntimeState.update(state, message)
        val status = ProbeProtocol.Status(
            probeId = PROBE_ID,
            sessionId = sessionId ?: return,
            state = state,
            timestampEpochMillis = System.currentTimeMillis(),
            batteryPercent = batteryPercent(),
            message = message
        )
        sendTelemetry(ProbeProtocol.STATUS_PATH, ProbeProtocol.encodeStatus(status))
    }

    private fun sendTelemetry(path: String, payload: ByteArray) {
        if (!::messageClient.isInitialized || transportFailed.get()) return
        val targetNodeId = phoneNodeId ?: return
        if (telemetryInFlight.incrementAndGet() > MAX_IN_FLIGHT_TELEMETRY) {
            telemetryInFlight.decrementAndGet()
            return
        }
        runCatching { messageClient.sendMessage(targetNodeId, path, payload) }
            .onSuccess { task ->
                task.addOnCompleteListener {
                    telemetryInFlight.decrementAndGet()
                    if (!it.isSuccessful) onTransportFailure()
                }
            }
            .onFailure {
                telemetryInFlight.decrementAndGet()
                onTransportFailure()
            }
    }

    private fun onTransportFailure() {
        if (!transportFailed.compareAndSet(false, true)) return
        if (running) {
            stopTracking()
            stopSelf()
        }
    }

    @Suppress("MissingPermission")
    private fun getBluetoothLeScanner() = getSystemService(android.bluetooth.BluetoothManager::class.java).adapter.bluetoothLeScanner

    private fun hasLocationPermission(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun hasBluetoothPermission(): Boolean = ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED

    private fun batteryPercent(): Int? = getSystemService(BatteryManager::class.java)
        ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        ?.takeIf { it in 0..100 }

    private fun nextSequence(): Long = ++sequence

    private fun startForegroundCompat() {
        val notification = Notification.Builder(this, NOTIFICATION_CHANNEL)
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("P.R.S. PROBE ACTIVE")
            .setContentText("WATCH 2 SENSOR NODE")
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, ProbeActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    private fun createNotificationChannel() {
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(NOTIFICATION_CHANNEL, "P.R.S. PROBE", NotificationManager.IMPORTANCE_LOW)
        )
    }

    private fun ByteArray.toHexString(): String = joinToString("") { byte -> "%02X".format(Locale.US, byte.toInt() and 0xFF) }

    companion object {
        const val ACTION_START = "com.suri.pipsurios.probe.START"
        const val ACTION_STOP = "com.suri.pipsurios.probe.STOP"
        const val EXTRA_PHONE_NODE_ID = "phone_node_id"
        const val PROBE_ID = "WATCH-2"
        const val EXTRA_SESSION_ID = "session_id"
        private const val LOCATION_INTERVAL_MILLIS = 10_000L
        private const val LOCATION_MIN_INTERVAL_MILLIS = 5_000L
        private const val BLE_SAMPLE_INTERVAL_MILLIS = 3_000L
        private const val MAX_PENDING_BLE = 256
        private const val MAX_IN_FLIGHT_TELEMETRY = 8
        private const val NOTIFICATION_CHANNEL = "prs_probe"
        private const val NOTIFICATION_ID = 1402
    }
}
