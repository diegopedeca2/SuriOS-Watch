package com.suri.pipsurios.prs

import java.util.ArrayDeque
import java.util.Locale
import kotlin.math.abs

/**
 * Keeps all BLE contacts alive while evaluating their signal at a controlled
 * cadence. A single scan callback never changes the inferred trend directly.
 */
class PrsContactTracker(
    private val tuning: PrsTuningConfig = PrsTuning.DEFAULT,
    private val densityEstimator: PrsDensityEstimator = DefaultPrsDensityEstimator()
) {
    private data class ContactState(
        val contactId: String,
        var latest: BleObservation,
        val firstSeenElapsedMillis: Long,
        var sampleCount: Int,
        var unknownLabel: String?,
        var smoothedRssi: Float? = null,
        var trend: PrsTrend = PrsTrend.INSUFFICIENT_DATA,
        var candidateTrend: PrsTrend? = null,
        var candidateCount: Int = 0,
        var lastEvaluatedObservationAt: Long = Long.MIN_VALUE,
        val history: ArrayDeque<RssiHistoryPoint> = ArrayDeque()
    )

    private val contacts = linkedMapOf<String, ContactState>()
    private var nextUnknownNumber = 1
    private var lastEvaluationAt = Long.MIN_VALUE

    @Synchronized
    fun observe(observation: BleObservation): String {
        val technicalId = observation.deviceIdentifier
            .trim()
            .takeIf { it.isNotEmpty() }
            ?.uppercase(Locale.US)
            ?: observation.temporaryId
        val contactId = if (observation.source == PrsObservationSource.A56) {
            technicalId
        } else {
            "${observation.source.key}:$technicalId"
        }

        val existing = contacts[contactId]
        if (existing == null) {
            contacts[contactId] = ContactState(
                contactId = contactId,
                latest = observation.copy(deviceIdentifier = technicalId),
                firstSeenElapsedMillis = observation.observedAt,
                sampleCount = 1,
                unknownLabel = if (usableName(observation.deviceName) == null) nextUnknownLabel() else null
            )
        } else {
            existing.latest = observation.copy(deviceIdentifier = technicalId)
            existing.sampleCount++
        }
        return contactId
    }

    @Synchronized
    fun evaluate(nowElapsedMillis: Long, force: Boolean = false): Boolean {
        if (!force && lastEvaluationAt != Long.MIN_VALUE &&
            nowElapsedMillis - lastEvaluationAt < tuning.evaluationIntervalMillis
        ) {
            expireLocked(nowElapsedMillis)
            return false
        }
        lastEvaluationAt = nowElapsedMillis
        expireLocked(nowElapsedMillis)

        contacts.values.forEach { state ->
            if (state.latest.observedAt <= state.lastEvaluatedObservationAt) return@forEach
            val raw = state.latest.rssi
            val smoothed = state.smoothedRssi?.let { previous ->
                previous + tuning.smoothingAlpha * (raw - previous)
            } ?: raw.toFloat()
            val previous = state.history.lastOrNull()
            val variation = previous?.let { smoothed - it.smoothedRssi }
            state.smoothedRssi = smoothed
            state.history.addLast(
                RssiHistoryPoint(
                    observedAtElapsedMillis = state.latest.observedAt,
                    observedAtEpochMillis = state.latest.observedAtEpochMillis,
                    rawRssi = raw,
                    smoothedRssi = smoothed,
                    variationFromPreviousDb = variation
                )
            )
            while (state.history.size > tuning.historyWindowSize) state.history.removeFirst()
            state.lastEvaluatedObservationAt = state.latest.observedAt

            val evidence = trendEvidence(state)
            state.trend = updateTrend(state, evidence)
        }
        return true
    }

    @Synchronized
    fun expire(nowElapsedMillis: Long): List<String> {
        val expired = contacts.values
            .filter { nowElapsedMillis - it.latest.observedAt > tuning.contactExpiryMillis }
            .map { it.contactId }
        expired.forEach(contacts::remove)
        return expired
    }

    @Synchronized
    fun snapshot(): PrsSnapshot = PrsSnapshot(
        contacts = contacts.values
            .sortedWith(compareBy(String.CASE_INSENSITIVE_ORDER) { displayName(it) })
            .map(::toSnapshot)
    )

    @Synchronized
    fun clear() {
        contacts.clear()
        nextUnknownNumber = 1
        lastEvaluationAt = Long.MIN_VALUE
    }

    private fun trendEvidence(state: ContactState): PrsTrend {
        val history = state.history.toList()
        if (history.size < tuning.minimumSamplesForTrend) return PrsTrend.INSUFFICIENT_DATA
        val oldest = history.first()
        val newest = history.last()
        if (newest.observedAtElapsedMillis - oldest.observedAtElapsedMillis < tuning.minimumTrendDurationMillis) {
            return PrsTrend.INSUFFICIENT_DATA
        }
        val delta = newest.smoothedRssi - oldest.smoothedRssi
        return when (state.trend) {
            PrsTrend.APPROACHING -> when {
                delta <= -(tuning.minimumSignificantVariationDb + tuning.hysteresisDb) -> PrsTrend.MOVING_AWAY
                delta <= tuning.stableVariationDb -> PrsTrend.STABLE
                else -> PrsTrend.APPROACHING
            }
            PrsTrend.MOVING_AWAY -> when {
                delta >= tuning.minimumSignificantVariationDb + tuning.hysteresisDb -> PrsTrend.APPROACHING
                delta >= -tuning.stableVariationDb -> PrsTrend.STABLE
                else -> PrsTrend.MOVING_AWAY
            }
            PrsTrend.STABLE,
            PrsTrend.INSUFFICIENT_DATA -> when {
                delta >= tuning.minimumSignificantVariationDb -> PrsTrend.APPROACHING
                delta <= -tuning.minimumSignificantVariationDb -> PrsTrend.MOVING_AWAY
                else -> PrsTrend.STABLE
            }
        }
    }

    private fun updateTrend(state: ContactState, evidence: PrsTrend): PrsTrend {
        if (evidence == PrsTrend.INSUFFICIENT_DATA) {
            state.candidateTrend = null
            state.candidateCount = 0
            return state.trend
        }
        if (evidence == state.trend) {
            state.candidateTrend = null
            state.candidateCount = 0
            return state.trend
        }
        if (state.candidateTrend != evidence) {
            state.candidateTrend = evidence
            state.candidateCount = 1
        } else {
            state.candidateCount++
        }
        val required = if (evidence == PrsTrend.STABLE) {
            tuning.stableConfirmationEvaluations
        } else {
            tuning.trendConfirmationEvaluations
        }
        if (state.candidateCount >= required) {
            state.candidateTrend = null
            state.candidateCount = 0
            return evidence
        }
        return state.trend
    }

    private fun toSnapshot(state: ContactState): PrsContactSnapshot {
        val history = state.history.toList()
        val smoothed = state.smoothedRssi ?: state.latest.rssi.toFloat()
        val mean = history.takeIf { it.isNotEmpty() }?.map { it.smoothedRssi }?.average()?.toFloat() ?: smoothed
        val variation = history.firstOrNull()?.let { smoothed - it.smoothedRssi } ?: 0f
        val proximity = when {
            smoothed >= tuning.nearMinimumRssi -> PrsProximityBand.NEAR
            smoothed >= tuning.mediumMinimumRssi -> PrsProximityBand.MEDIUM
            history.isNotEmpty() -> PrsProximityBand.FAR
            else -> PrsProximityBand.UNKNOWN
        }
        val confidence = (history.size.toFloat() / tuning.minimumSamplesForTrend).coerceIn(0f, 1f)
        val explanation = explanation(state, variation)
        return PrsContactSnapshot(
            contactId = state.contactId,
            displayName = displayName(state),
            source = state.latest.source,
            measured = state.latest,
            firstSeenElapsedMillis = state.firstSeenElapsedMillis,
            sampleCount = state.sampleCount,
            processed = PrsProcessedSignal(
                smoothedRssi = smoothed,
                meanRssi = mean,
                variationDb = variation,
                history = history
            ),
            inference = PrsInference(
                trend = state.trend,
                proximity = proximity,
                explanation = explanation,
                densityCloud = densityEstimator.estimate(proximity, confidence)
            )
        )
    }

    private fun explanation(state: ContactState, variation: Float): String = when (state.trend) {
        PrsTrend.APPROACHING -> "SMOOTHED RSSI INCREASED ${formatDb(abs(variation))} dB ACROSS WINDOW"
        PrsTrend.MOVING_AWAY -> "SMOOTHED RSSI DECREASED ${formatDb(abs(variation))} dB ACROSS WINDOW"
        PrsTrend.STABLE -> "WINDOW VARIATION REMAINS BELOW TREND THRESHOLD"
        PrsTrend.INSUFFICIENT_DATA -> "WAITING FOR TEMPORAL WINDOW"
    }

    private fun displayName(state: ContactState): String =
        usableName(state.latest.deviceName) ?: state.unknownLabel ?: "UNKNOWN"

    private fun nextUnknownLabel(): String = "UNKNOWN ${nextUnknownNumber++.toString().padStart(2, '0')}"

    private fun expireLocked(nowElapsedMillis: Long) {
        contacts.values.removeIf {
            nowElapsedMillis - it.latest.observedAt > tuning.contactExpiryMillis
        }
    }

    private fun usableName(name: String?): String? = name
        ?.filterNot(Char::isISOControl)
        ?.trim()
        ?.takeIf { it.isNotEmpty() }

    private fun formatDb(value: Float): String = "%.1f".format(Locale.US, value)
}
