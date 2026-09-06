package com.suri.pipsurios.prs

/** Data measured directly by the Android BLE scanner. It must not be treated as a position. */
data class BleObservation(
    val temporaryId: String,
    val rssi: Int,
    val observedAt: Long,
    val deviceIdentifier: String = temporaryId,
    val deviceName: String? = null,
    val advertisingDataHex: String? = null,
    val deviceType: Int? = null,
    val bluetoothClass: Int? = null,
    val deviceAddressType: Int? = null,
    val observedAtEpochMillis: Long = System.currentTimeMillis(),
    val source: PrsObservationSource = PrsObservationSource.A56
) {
    val deviceCategory: PrsDeviceCategory?
        get() = PrsDeviceClassifier.classify(
            deviceName = deviceName,
            advertisingDataHex = advertisingDataHex,
            bluetoothClass = bluetoothClass
        )
}

enum class PrsObservationSource(val displayName: String, val key: String) {
    A56("A56", "A56"),
    PROBE_WATCH_2("WATCH 2 PROBE", "WATCH-2")
}

/** One point in the evaluated, time-sampled signal history. */
data class RssiHistoryPoint(
    val observedAtElapsedMillis: Long,
    val observedAtEpochMillis: Long,
    val rawRssi: Int,
    val smoothedRssi: Float,
    val variationFromPreviousDb: Float?
)

/** Processed values kept separate from the measured BLE observation. */
data class PrsProcessedSignal(
    val smoothedRssi: Float,
    val meanRssi: Float,
    val variationDb: Float,
    val history: List<RssiHistoryPoint>
)

enum class PrsTrend {
    INSUFFICIENT_DATA,
    APPROACHING,
    STABLE,
    MOVING_AWAY
}

enum class PrsProximityBand {
    UNKNOWN,
    NEAR,
    MEDIUM,
    FAR
}

/** Inferences made from the processed signal; no member represents a physical coordinate. */
data class PrsInference(
    val trend: PrsTrend,
    val proximity: PrsProximityBand,
    val explanation: String,
    val densityCloud: DensityCloud
)

/** A contact combines measured, processed and inferred layers without conflating them. */
data class PrsContactSnapshot(
    val contactId: String,
    val displayName: String,
    val source: PrsObservationSource,
    val measured: BleObservation,
    val firstSeenElapsedMillis: Long,
    val sampleCount: Int,
    val processed: PrsProcessedSignal,
    val inference: PrsInference
)

data class PrsSnapshot(
    val contacts: List<PrsContactSnapshot> = emptyList()
) {
    fun contact(contactId: String?): PrsContactSnapshot? =
        contacts.firstOrNull { it.contactId == contactId }
}

/**
 * Signal-derived uncertainty data kept for the general density grid. The
 * target-area estimator adds a separate, experimental heading/RSSI hypothesis
 * for TRACKER without claiming that BLE provides a direct bearing.
 */
data class DensityCloud(
    val radialCenterFraction: Float,
    val radialSpreadFraction: Float,
    val confidence: Float,
    val azimuthCoverage: Float = 1f
)

/** Optional motion/orientation input for future estimators. */
data class PrsMovementContext(
    val eastDisplacementMeters: Double = 0.0,
    val northDisplacementMeters: Double = 0.0,
    val headingDegrees: Float? = null,
    val speedMetersPerSecond: Float? = null
)
