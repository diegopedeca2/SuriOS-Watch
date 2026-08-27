package com.suri.pipsurios.remoteprobe

enum class RemoteProbeNode {
    OPERATOR,
    PROBE
}

enum class RemoteProbeLink {
    STOPPED,
    STARTING,
    LISTENING,
    CONNECTED,
    ERROR
}

enum class RemoteProbeAssessment {
    NEAR_OPERATOR,
    BETWEEN,
    NEAR_PROBE,
    UNCERTAIN
}

data class RemoteProbeObservation(
    val node: RemoteProbeNode,
    val timestampEpochMillis: Long,
    val deviceIdentifier: String,
    val rssi: Int,
    val deviceName: String? = null,
    val advertisingDataHex: String? = null,
    val deviceType: Int? = null
)

data class RemoteProbeComparison(
    val deviceIdentifier: String,
    val operatorRssi: Int,
    val probeRssi: Int,
    val timestampDeltaMillis: Long,
    val assessment: RemoteProbeAssessment,
    val probeTimestampEpochMillis: Long = 0L
)

data class RemoteProbeSnapshot(
    val link: RemoteProbeLink = RemoteProbeLink.STOPPED,
    val sessionId: String? = null,
    val operatorContactCount: Int = 0,
    val probeContactCount: Int = 0,
    val combinedContactCount: Int = 0,
    val matchedContactCount: Int = 0,
    val lastProbeObservationAtEpochMillis: Long? = null,
    val comparisons: List<RemoteProbeComparison> = emptyList()
)
