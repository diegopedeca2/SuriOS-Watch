package com.suri.pipsurios.remoteprobe

import kotlin.math.abs

/**
 * Deliberately conservative, experimental comparison. It compares observed RSSI only;
 * it does not infer distance, coordinates or triangulation.
 */
object RemoteProbeComparator {
    const val MATCH_WINDOW_MILLIS = 8_000L
    const val BETWEEN_MAX_DELTA_DB = 6
    const val MIN_SIDE_DELTA_DB = 10

    fun compare(
        observations: List<RemoteProbeObservation>,
        nowEpochMillis: Long = System.currentTimeMillis()
    ): List<RemoteProbeComparison> {
        val recent = observations.filter {
            nowEpochMillis - it.timestampEpochMillis in 0..MATCH_WINDOW_MILLIS
        }
        return recent.groupBy { it.deviceIdentifier }
            .mapNotNull { (identifier, deviceObservations) ->
                val operator = deviceObservations.filter { it.node == RemoteProbeNode.OPERATOR }
                val probe = deviceObservations.filter { it.node == RemoteProbeNode.PROBE }
                val operatorLast = operator.maxByOrNull { it.timestampEpochMillis } ?: return@mapNotNull null
                val probeLast = probe.maxByOrNull { it.timestampEpochMillis } ?: return@mapNotNull null
                val timestampDelta = abs(operatorLast.timestampEpochMillis - probeLast.timestampEpochMillis)
                if (timestampDelta > MATCH_WINDOW_MILLIS) return@mapNotNull null
                val operatorRssi = operator.map { it.rssi }.average().toInt()
                val probeRssi = probe.map { it.rssi }.average().toInt()
                RemoteProbeComparison(
                    deviceIdentifier = identifier,
                    operatorRssi = operatorRssi,
                    probeRssi = probeRssi,
                    timestampDeltaMillis = timestampDelta,
                    assessment = classify(operatorRssi, probeRssi),
                    probeTimestampEpochMillis = probeLast.timestampEpochMillis
                )
            }
            .sortedBy { it.deviceIdentifier }
    }

    fun classify(operatorRssi: Int, probeRssi: Int): RemoteProbeAssessment {
        val difference = probeRssi - operatorRssi
        return when {
            difference >= MIN_SIDE_DELTA_DB -> RemoteProbeAssessment.NEAR_PROBE
            difference <= -MIN_SIDE_DELTA_DB -> RemoteProbeAssessment.NEAR_OPERATOR
            abs(difference) <= BETWEEN_MAX_DELTA_DB -> RemoteProbeAssessment.BETWEEN
            else -> RemoteProbeAssessment.UNCERTAIN
        }
    }
}
