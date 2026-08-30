package com.suri.surioswatch.probe

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.suri.probeprotocol.ProbeProtocol

/** Receives only control; the sensor service owns the operational loop. */
class ProbeControlService : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path != ProbeProtocol.CONTROL_PATH) return
        val packet = ProbeProtocol.decode(event.data) as? ProbeProtocol.Packet.ControlMessage ?: return
        when (packet.control.command) {
            ProbeProtocol.Command.START_RECON,
            ProbeProtocol.Command.START_SENTRY -> {
                startService(packet.control.phoneNodeId)
            }
            ProbeProtocol.Command.STOP -> stopService(Intent(this, ProbeLocationService::class.java))
            ProbeProtocol.Command.PING -> startService(packet.control.phoneNodeId)
        }
    }

    private fun startService(phoneNodeId: String) {
        val intent = Intent(this, ProbeLocationService::class.java)
            .setAction(ProbeLocationService.ACTION_START)
            .putExtra(ProbeLocationService.EXTRA_PHONE_NODE_ID, phoneNodeId)
        runCatching { startForegroundService(intent) }
    }
}
