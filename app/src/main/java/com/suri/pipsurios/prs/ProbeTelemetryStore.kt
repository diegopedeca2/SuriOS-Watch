package com.suri.pipsurios.prs

import com.suri.probeprotocol.ProbeProtocol
import java.util.concurrent.CopyOnWriteArraySet

/** In-process bridge between the Data Layer listener and the P.R.S. screen. */
object ProbeTelemetryStore {
    private val snapshotListeners = CopyOnWriteArraySet<(PrsProbeNodeSnapshot) -> Unit>()
    private val observationListeners = CopyOnWriteArraySet<(ProbeProtocol.BleSample) -> Unit>()
    private var current = PrsProbeNodeSnapshot()
    private var expectedProbeNodeId: String? = null
    private var expectedSessionId: String? = null

    @Synchronized
    fun expectProbe(nodeId: String, sessionId: String) {
        expectedProbeNodeId = nodeId
        expectedSessionId = sessionId
    }

    @Synchronized
    fun acceptsProbe(nodeId: String, sessionId: String): Boolean =
        nodeId.isNotBlank() && nodeId == expectedProbeNodeId && sessionId == expectedSessionId

    @Synchronized
    fun clearExpectedProbe(nodeId: String, sessionId: String) {
        if (nodeId == expectedProbeNodeId && sessionId == expectedSessionId) {
            expectedProbeNodeId = null
            expectedSessionId = null
        }
    }

    @Synchronized
    fun snapshot(): PrsProbeNodeSnapshot = current

    fun observe(
        onSnapshot: (PrsProbeNodeSnapshot) -> Unit,
        onObservation: (ProbeProtocol.BleSample) -> Unit
    ): () -> Unit {
        snapshotListeners += onSnapshot
        observationListeners += onObservation
        onSnapshot(snapshot())
        return {
            snapshotListeners -= onSnapshot
            observationListeners -= onObservation
        }
    }

    @Synchronized
    fun publish(packet: ProbeProtocol.Packet) {
        when (packet) {
            is ProbeProtocol.Packet.Location -> {
                val sample = packet.sample
                current = current.copy(
                    probeId = sample.probeId,
                    state = "ACTIVE",
                    location = PrsProbeLocation.from(sample),
                    lastSeenEpochMillis = sample.timestampEpochMillis,
                    batteryPercent = sample.batteryPercent,
                    message = null
                )
            }
            is ProbeProtocol.Packet.Ble -> {
                val sample = packet.sample
                current = current.copy(
                    probeId = sample.probeId,
                    state = "ACTIVE",
                    lastSeenEpochMillis = sample.timestampEpochMillis,
                    observationCount = current.observationCount + 1,
                    message = null
                )
            }
            is ProbeProtocol.Packet.StatusUpdate -> {
                val status = packet.status
                current = current.copy(
                    probeId = status.probeId,
                    state = status.state,
                    lastSeenEpochMillis = status.timestampEpochMillis,
                    batteryPercent = status.batteryPercent,
                    message = status.message
                )
            }
            is ProbeProtocol.Packet.ControlMessage -> return
        }
        val updated = current
        snapshotListeners.forEach { it(updated) }
        if (packet is ProbeProtocol.Packet.Ble) {
            observationListeners.forEach { it(packet.sample) }
        }
    }

    @Synchronized
    fun clear() {
        current = PrsProbeNodeSnapshot()
        val updated = current
        snapshotListeners.forEach { it(updated) }
    }
}
