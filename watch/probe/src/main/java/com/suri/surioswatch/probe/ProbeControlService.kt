package com.suri.surioswatch.probe

import android.content.Intent
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.suri.probeprotocol.ProbeProtocol

/** Receives only control; the sensor service owns the operational loop. */
class ProbeControlService : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (event.path != ProbeProtocol.CONTROL_PATH) return
        val packet = ProbeProtocol.decode(event.data) as? ProbeProtocol.Packet.ControlMessage ?: return
        val control = packet.control
        if (event.sourceNodeId.isBlank() || event.sourceNodeId != control.phoneNodeId) return
        Wearable.getNodeClient(this).connectedNodes
            .addOnSuccessListener { nodes ->
                if (nodes.none { it.id == event.sourceNodeId }) return@addOnSuccessListener
                when (control.command) {
                    ProbeProtocol.Command.START_RECON,
                    ProbeProtocol.Command.START_SENTRY -> startService(control.phoneNodeId, control.sessionId)
                    ProbeProtocol.Command.STOP -> runCatching {
                        startService(
                            Intent(this, ProbeLocationService::class.java)
                                .setAction(ProbeLocationService.ACTION_STOP)
                                .putExtra(ProbeLocationService.EXTRA_PHONE_NODE_ID, control.phoneNodeId)
                                .putExtra(ProbeLocationService.EXTRA_SESSION_ID, control.sessionId)
                        )
                    }
                    ProbeProtocol.Command.PING -> sendPong(control.phoneNodeId, control.sessionId)
                }
            }
    }

    private fun startService(phoneNodeId: String, sessionId: String) {
        val intent = Intent(this, ProbeLocationService::class.java)
            .setAction(ProbeLocationService.ACTION_START)
            .putExtra(ProbeLocationService.EXTRA_PHONE_NODE_ID, phoneNodeId)
            .putExtra(ProbeLocationService.EXTRA_SESSION_ID, sessionId)
        runCatching { startForegroundService(intent) }
    }

    private fun sendPong(phoneNodeId: String, sessionId: String) {
        val status = ProbeProtocol.Status(
            probeId = ProbeLocationService.PROBE_ID,
            sessionId = sessionId,
            state = "READY",
            timestampEpochMillis = System.currentTimeMillis(),
            batteryPercent = null,
            message = "PONG"
        )
        runCatching {
            Wearable.getMessageClient(this).sendMessage(
                phoneNodeId,
                ProbeProtocol.STATUS_PATH,
                ProbeProtocol.encodeStatus(status)
            )
        }
    }
}
