package com.suri.pipsurios.prs

/** All values that are expected to change during physical A56 calibration. */
data class PrsTuningConfig(
    val evaluationIntervalMillis: Long,
    val smoothingAlpha: Float,
    val historyWindowSize: Int,
    val minimumSamplesForTrend: Int,
    val minimumTrendDurationMillis: Long,
    val minimumSignificantVariationDb: Float,
    val stableVariationDb: Float,
    val hysteresisDb: Float,
    val trendConfirmationEvaluations: Int,
    val stableConfirmationEvaluations: Int,
    val contactExpiryMillis: Long,
    val nearMinimumRssi: Float,
    val mediumMinimumRssi: Float
) {
    init {
        require(evaluationIntervalMillis > 0)
        require(smoothingAlpha in 0f..1f)
        require(historyWindowSize >= minimumSamplesForTrend)
        require(minimumSamplesForTrend >= 2)
        require(minimumTrendDurationMillis >= evaluationIntervalMillis)
        require(minimumSignificantVariationDb > stableVariationDb)
        require(hysteresisDb >= 0f)
        require(trendConfirmationEvaluations >= 1)
        require(stableConfirmationEvaluations >= 1)
        require(contactExpiryMillis > evaluationIntervalMillis)
        require(nearMinimumRssi > mediumMinimumRssi)
    }
}

object PrsTuning {
    /** Initial field-test defaults, not a distance calibration or a final dBm model. */
    val DEFAULT = PrsTuningConfig(
        evaluationIntervalMillis = 3_000L,
        smoothingAlpha = 0.35f,
        historyWindowSize = 8,
        minimumSamplesForTrend = 4,
        minimumTrendDurationMillis = 9_000L,
        minimumSignificantVariationDb = 4.5f,
        stableVariationDb = 2.0f,
        hysteresisDb = 1.5f,
        trendConfirmationEvaluations = 2,
        stableConfirmationEvaluations = 2,
        contactExpiryMillis = 15_000L,
        nearMinimumRssi = -76f,
        mediumMinimumRssi = -88f
    )
}

/** Display-only calibration for converting measured GPS deltas into the grid surface. */
object PrsProbeDisplayTuning {
    const val GRID_HALF_SPAN_METERS = 100.0
    const val MAX_LOCATION_AGE_MILLIS = 60_000L
}
