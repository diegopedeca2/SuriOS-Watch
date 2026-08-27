package com.suri.surioswatch.remoteprobe

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder

class RemoteProbeService : Service() {
    private lateinit var scanner: WatchBleScanner
    private lateinit var client: RemoteProbeClient
    private lateinit var localStore: RemoteProbeLocalStore
    private var started = false

    override fun onCreate() {
        super.onCreate()
        scanner = WatchBleScanner(this)
        client = RemoteProbeClient(this)
        localStore = RemoteProbeLocalStore(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> stopProbe()
            else -> startProbe()
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        stopProbe()
        super.onDestroy()
    }

    private fun startProbe() {
        if (started) return
        startForeground(NOTIFICATION_ID, buildNotification())
        started = true
        val scanStatus = scanner.start(
            onObservation = { observation ->
                RemoteProbeState.observe(observation.deviceIdentifier, observation.timestampEpochMillis)
                localStore.append(observation)
                client.enqueue(observation)
            },
            onStatus = { status ->
                RemoteProbeState.scanStatus = status
                if (status == WatchBleScanStatus.ERROR) RemoteProbeState.lastError = "BLE scan failed"
            }
        )
        RemoteProbeState.scanStatus = scanStatus
        if (scanStatus == WatchBleScanStatus.SCANNING) client.start()
    }

    private fun stopProbe() {
        if (!started) {
            RemoteProbeState.reset()
            return
        }
        scanner.stop()
        client.stop()
        started = false
        RemoteProbeState.reset()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, RemoteProbeActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("P.R.S. REMOTE PROBE")
            .setContentText("BLE scan active")
            .setSmallIcon(android.R.drawable.ic_menu_search)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        getSystemService(NotificationManager::class.java).createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                "P.R.S. Remote Probe",
                NotificationManager.IMPORTANCE_LOW
            )
        )
    }

    companion object {
        const val ACTION_START = "com.suri.surioswatch.remoteprobe.START"
        const val ACTION_STOP = "com.suri.surioswatch.remoteprobe.STOP"
        private const val CHANNEL_ID = "prs_remote_probe"
        private const val NOTIFICATION_ID = 1402
    }
}
