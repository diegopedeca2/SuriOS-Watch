package com.suri.pipsurios.remoteprobe

import android.content.Context
import com.suri.pipsurios.sonar.BleObservation

/** Coordinates the existing A56 scanner, the local gateway and raw test storage. */
class RemoteProbeSession(context: Context) {
    private val repository = RemoteProbeRepository.from(context)
    private val observations = ArrayDeque<RemoteProbeObservation>()
    private val gateway = RemoteProbeGateway(context) { probeObservations ->
        probeObservations.forEach(::observeProbe)
    }
    private var sessionId: String? = null

    @Synchronized
    fun start() {
        if (sessionId != null) return
        sessionId = repository.createSession()
        gateway.start()
    }

    fun observeOperator(observation: BleObservation) {
        append(
            RemoteProbeObservation(
                node = RemoteProbeNode.OPERATOR,
                timestampEpochMillis = observation.observedAtEpochMillis,
                deviceIdentifier = observation.deviceIdentifier,
                rssi = observation.rssi,
                deviceName = observation.deviceName,
                advertisingDataHex = observation.advertisingDataHex,
                deviceType = observation.deviceType
            )
        )
    }

    fun stop() {
        gateway.stop()
        synchronized(this) {
            sessionId = null
            observations.clear()
        }
    }

    @Synchronized
    fun snapshot(nowEpochMillis: Long = System.currentTimeMillis()): RemoteProbeSnapshot {
        val recent = observations.filter {
            nowEpochMillis - it.timestampEpochMillis in 0..ACTIVE_CONTACT_WINDOW_MILLIS
        }
        val operatorIds = recent.filter { it.node == RemoteProbeNode.OPERATOR }
            .mapTo(mutableSetOf()) { it.deviceIdentifier }
        val probeIds = recent.filter { it.node == RemoteProbeNode.PROBE }
            .mapTo(mutableSetOf()) { it.deviceIdentifier }
        val combinedIds = operatorIds union probeIds
        val comparisons = RemoteProbeComparator.compare(observations.toList(), nowEpochMillis)
        return RemoteProbeSnapshot(
            link = gateway.status(nowEpochMillis),
            sessionId = sessionId,
            operatorContactCount = operatorIds.size,
            probeContactCount = probeIds.size,
            combinedContactCount = combinedIds.size,
            matchedContactCount = comparisons.size,
            lastProbeObservationAtEpochMillis = recent.filter { it.node == RemoteProbeNode.PROBE }
                .maxOfOrNull { it.timestampEpochMillis },
            comparisons = comparisons
        )
    }

    private fun observeProbe(observation: RemoteProbeObservation) {
        if (observation.node != RemoteProbeNode.PROBE) return
        append(observation)
    }

    @Synchronized
    private fun append(observation: RemoteProbeObservation) {
        if (sessionId == null) return
        observations.addLast(observation)
        while (observations.size > MAX_MEMORY_OBSERVATIONS) observations.removeFirst()
        repository.append(sessionId!!, observation)
    }

    companion object {
        private const val ACTIVE_CONTACT_WINDOW_MILLIS = 15_000L
        private const val MAX_MEMORY_OBSERVATIONS = 20_000
    }
}
