package com.suri.pipsurios.sonar

object SonarTuning {
    const val RSSI_SMOOTHING_ALPHA = 0.28f

    const val VERY_CLOSE_MIN_RSSI = -55f
    const val CLOSE_MIN_RSSI = -67f
    const val MEDIUM_MIN_RSSI = -78f

    const val CALIBRATION_DURATION_MILLIS = 6_000L
    const val CONTACT_EXPIRY_MILLIS = 8_000L

    const val VERY_CLOSE_RADIUS_FRACTION = 0.22f
    const val CLOSE_RADIUS_FRACTION = 0.42f
    const val MEDIUM_RADIUS_FRACTION = 0.64f
    const val FAR_RADIUS_FRACTION = 0.84f

    const val SWEEP_DURATION_MILLIS = 2_400
}

object RssiFilter {
    fun smooth(previous: Float, current: Int, alpha: Float = SonarTuning.RSSI_SMOOTHING_ALPHA): Float =
        previous + alpha.coerceIn(0f, 1f) * (current - previous)
}

object ProximityClassifier {
    fun classify(smoothedRssi: Float): ProximityCategory = when {
        smoothedRssi >= SonarTuning.VERY_CLOSE_MIN_RSSI -> ProximityCategory.VERY_CLOSE
        smoothedRssi >= SonarTuning.CLOSE_MIN_RSSI -> ProximityCategory.CLOSE
        smoothedRssi >= SonarTuning.MEDIUM_MIN_RSSI -> ProximityCategory.MEDIUM
        else -> ProximityCategory.FAR
    }
}
