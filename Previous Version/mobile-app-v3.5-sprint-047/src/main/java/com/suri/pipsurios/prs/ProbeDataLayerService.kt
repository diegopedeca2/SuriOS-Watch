package com.suri.pipsurios.prs

import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.suri.probeprotocol.ProbeProtocol

/** Receives live PROBE telemetry; messages are intentionally not persisted or replayed. */
class ProbeDataLayerService : WearableListenerService() {
    override fun onMessageReceived(event: MessageEvent) {
        if (!event.path.startsWith(ProbeProtocol.TELEMETRY_PATH_PREFIX)) return
        val packet = ProbeProtocol.decode(event.data) ?: return
        val sessionId = when (packet) {
            is ProbeProtocol.Packet.Location -> packet.sample.sessionId
            is ProbeProtocol.Packet.Ble -> packet.sample.sessionId
            is ProbeProtocol.Packet.StatusUpdate -> packet.status.sessionId
            is ProbeProtocol.Packet.ControlMessage -> return
        }
        if (!ProbeTelemetryStore.acceptsProbe(event.sourceNodeId, sessionId)) return
        ProbeTelemetryStore.publish(packet)
    }
}
