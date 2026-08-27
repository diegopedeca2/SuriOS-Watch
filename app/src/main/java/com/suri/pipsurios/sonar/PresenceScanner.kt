package com.suri.pipsurios.sonar

/**
 * A short, operator-controlled two-position BLE survey.
 *
 * The result is deliberately phrased as device signal evidence. BLE radio
 * observations cannot prove that a person is present or that a signal is on
 * the other side of a wall.
 */
enum class PresenceScanPhase {
    IDLE,
    REFERENCE,
    DOOR,
    COMPLETE
}

enum class PresenceAssessment {
    NOT_READY,
    NO_DEVICE_SIGNAL,
    POSSIBLE_DEVICE_SIGNAL,
    PROBABLE_DEVICE_SIGNAL
}

object PresenceTuning {
    /** The first pass is intentionally short-range: weaker readings are ignored. */
    const val CLOSE_DURATION_MILLIS = 6_000L
    /** The second pass listens more broadly and is compared with the close pass. */
    const val WIDE_DURATION_MILLIS = 10_000L
    const val CLOSE_MAX_RSSI = -65
    const val MIN_STABLE_OBSERVATIONS = 4
    const val STRONG_SIGNAL_RSSI = -72
}

/** A deliberately approximate point used by the simple 2D signal map. */
data class PresenceSignalPoint(
    val number: Int,
    val angleDegrees: Float,
    val distanceFraction: Float,
    val rssi: Int,
    val isNew: Boolean
)

data class PresenceScanSnapshot(
    val phase: PresenceScanPhase = PresenceScanPhase.IDLE,
    val phaseElapsedMillis: Long = 0L,
    val referenceComplete: Boolean = false,
    val referenceDeviceCount: Int = 0,
    val doorDeviceCount: Int = 0,
    val newDeviceCount: Int = 0,
    val stableNewDeviceCount: Int = 0,
    val strongestNewRssi: Int? = null,
    val signalIndex: Int = 0,
    val assessment: PresenceAssessment = PresenceAssessment.NOT_READY,
    val signalPoints: List<PresenceSignalPoint> = emptyList()
)

private data class DeviceStats(
    var observations: Int = 0,
    var firstSeen: Long = 0L,
    var lastSeen: Long = 0L,
    var strongestRssi: Int = Int.MIN_VALUE,
    var rssiSum: Long = 0L
) {
    fun observe(observation: BleObservation) {
        if (observations == 0) firstSeen = observation.observedAt
        observations++
        lastSeen = observation.observedAt
        strongestRssi = maxOf(strongestRssi, observation.rssi)
        rssiSum += observation.rssi
    }
}

/** Collects a reference position and a subsequent door position in one scan session. */
class PresenceScannerSession {
    private val reference = linkedMapOf<String, DeviceStats>()
    private val door = linkedMapOf<String, DeviceStats>()
    private var phase = PresenceScanPhase.IDLE
    private var referenceComplete = false
    private var phaseStartedAt = 0L
    private var phaseElapsedMillis = 0L

    @Synchronized
    fun reset() {
        reference.clear()
        door.clear()
        phase = PresenceScanPhase.IDLE
        referenceComplete = false
        phaseStartedAt = 0L
        phaseElapsedMillis = 0L
    }

    @Synchronized
    fun startReference(startedAt: Long) {
        reference.clear()
        door.clear()
        phase = PresenceScanPhase.REFERENCE
        referenceComplete = false
        phaseStartedAt = startedAt
        phaseElapsedMillis = 0L
    }

    @Synchronized
    fun finishReference(finishedAt: Long) {
        if (phase != PresenceScanPhase.REFERENCE) return
        phaseElapsedMillis = (finishedAt - phaseStartedAt).coerceAtLeast(0L)
        phase = PresenceScanPhase.IDLE
        referenceComplete = true
    }

    @Synchronized
    fun startDoor(startedAt: Long) {
        if (!referenceComplete) return
        door.clear()
        phase = PresenceScanPhase.DOOR
        phaseStartedAt = startedAt
        phaseElapsedMillis = 0L
    }

    @Synchronized
    fun finishDoor(finishedAt: Long) {
        if (phase != PresenceScanPhase.DOOR) return
        phaseElapsedMillis = (finishedAt - phaseStartedAt).coerceAtLeast(0L)
        phase = PresenceScanPhase.COMPLETE
    }

    @Synchronized
    fun observe(observation: BleObservation) {
        // Android does not expose a true scan radius. A strong RSSI threshold
        // gives the first pass a useful, understandable "close" meaning.
        if (phase == PresenceScanPhase.REFERENCE && observation.rssi < PresenceTuning.CLOSE_MAX_RSSI) {
            return
        }
        val target = when (phase) {
            PresenceScanPhase.REFERENCE -> reference
            PresenceScanPhase.DOOR -> door
            PresenceScanPhase.IDLE,
            PresenceScanPhase.COMPLETE -> return
        }
        target.getOrPut(observation.temporaryId) { DeviceStats() }.observe(observation)
    }

    @Synchronized
    fun snapshot(now: Long = phaseStartedAt): PresenceScanSnapshot {
        val elapsed = when (phase) {
            PresenceScanPhase.REFERENCE,
            PresenceScanPhase.DOOR -> (now - phaseStartedAt).coerceAtLeast(0L)
            PresenceScanPhase.IDLE,
            PresenceScanPhase.COMPLETE -> phaseElapsedMillis
        }
        val newStats = door.filterKeys { it !in reference }.values
        val stableCount = newStats.count { it.observations >= PresenceTuning.MIN_STABLE_OBSERVATIONS }
        val strongest = newStats.maxOfOrNull { it.strongestRssi }
        val assessment = assess(newStats, phase)
        val signalIndex = signalIndex(newStats, stableCount, strongest, assessment)
        val visibleStats = when (phase) {
            PresenceScanPhase.REFERENCE -> reference
            PresenceScanPhase.DOOR,
            PresenceScanPhase.COMPLETE -> door
            PresenceScanPhase.IDLE -> if (referenceComplete && door.isNotEmpty()) door else reference
        }
        val newIds = if (referenceComplete) door.keys - reference.keys else emptySet()
        val signalPoints = visibleStats.entries
            .sortedBy { it.key }
            .mapIndexed { index, (id, stats) ->
                val averageRssi = if (stats.observations == 0) -100 else
                    (stats.rssiSum.toFloat() / stats.observations).toInt()
                val normalizedDistance = ((-averageRssi - 35f) / 65f).coerceIn(0f, 1f)
                PresenceSignalPoint(
                    number = index + 1,
                    angleDegrees = stableAngleFor(id),
                    distanceFraction = 0.10f + normalizedDistance * 0.82f,
                    rssi = averageRssi,
                    isNew = id in newIds
                )
            }

        return PresenceScanSnapshot(
            phase = phase,
            phaseElapsedMillis = elapsed,
            referenceComplete = referenceComplete,
            referenceDeviceCount = reference.size,
            doorDeviceCount = door.size,
            newDeviceCount = newStats.size,
            stableNewDeviceCount = stableCount,
            strongestNewRssi = strongest,
            signalIndex = signalIndex,
            assessment = assessment,
            signalPoints = signalPoints
        )
    }

    private fun assess(
        newStats: Collection<DeviceStats>,
        currentPhase: PresenceScanPhase
    ): PresenceAssessment {
        if (currentPhase != PresenceScanPhase.COMPLETE) return PresenceAssessment.NOT_READY
        if (newStats.isEmpty()) return PresenceAssessment.NO_DEVICE_SIGNAL

        val stableCount = newStats.count { it.observations >= PresenceTuning.MIN_STABLE_OBSERVATIONS }
        val strongest = newStats.maxOf { it.strongestRssi }
        return if (
            stableCount >= 2 ||
            (stableCount >= 1 && strongest >= PresenceTuning.STRONG_SIGNAL_RSSI)
        ) {
            PresenceAssessment.PROBABLE_DEVICE_SIGNAL
        } else {
            PresenceAssessment.POSSIBLE_DEVICE_SIGNAL
        }
    }

    private fun signalIndex(
        newStats: Collection<DeviceStats>,
        stableCount: Int,
        strongest: Int?,
        assessment: PresenceAssessment
    ): Int {
        if (assessment == PresenceAssessment.NOT_READY || newStats.isEmpty()) return 0
        val transientCount = (newStats.size - stableCount).coerceAtLeast(0)
        val stabilityScore = (stableCount * 35).coerceAtMost(70)
        val transientScore = (transientCount * 10).coerceAtMost(20)
        val signalScore = if (strongest != null && strongest >= PresenceTuning.STRONG_SIGNAL_RSSI) 10 else 0
        return (stabilityScore + transientScore + signalScore).coerceIn(0, 100)
    }

    private fun stableAngleFor(temporaryId: String): Float {
        val normalizedHash = temporaryId.hashCode().toLong() and 0x7fffffffL
        return (normalizedHash % 36000L) / 100f
    }
}
