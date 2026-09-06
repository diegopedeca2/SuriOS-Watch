package com.suri.pipsurios.prs

import com.suri.pipsurios.terrain.GeoPoint
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class PrsTargetSide(val label: String) {
    UNKNOWN("LADO PENDIENTE"),
    LEFT("IZQUIERDA"),
    RIGHT("DERECHA")
}

data class PrsTargetAreaEstimate(
    val center: GeoPoint? = null,
    val radiusMeters: Float = 0f,
    val confidence: Float = 0f,
    val side: PrsTargetSide = PrsTargetSide.UNKNOWN,
    val sideConfidence: Float = 0f,
    val sampleCount: Int = 0
)

/**
 * Builds one current target-area hypothesis from a static target's readings.
 * This is deliberately relative: BLE RSSI is not a calibrated rangefinder and
 * the phone antenna does not provide a true bearing.
 */
class PrsTargetAreaEstimator {
    private data class Sample(
        val point: GeoPoint,
        val headingDegrees: Float?,
        val smoothedRssi: Float,
        val proximity: PrsProximityBand,
        val gpsAccuracyMeters: Float,
        val observedAt: Long
    )

    private val samples = ArrayDeque<Sample>()
    private var targetId: String? = null
    private var lastSignalAt = Long.MIN_VALUE
    private var sideScore = 0.0
    private var estimate = PrsTargetAreaEstimate()

    fun update(
        contact: PrsContactSnapshot?,
        measurementPoint: GeoPoint?,
        headingDegrees: Float?,
        gpsAccuracyMeters: Float
    ): PrsTargetAreaEstimate {
        if (contact == null || measurementPoint == null) return estimate
        if (targetId != contact.contactId) clearFor(contact.contactId)

        val observedAt = contact.processed.history.lastOrNull()?.observedAtElapsedMillis
            ?: contact.measured.observedAt
        if (observedAt <= lastSignalAt) return estimate

        val currentRssi = contact.processed.smoothedRssi
        samples.lastOrNull()?.let { previous ->
            val turn = if (headingDegrees != null && previous.headingDegrees != null) {
                shortestHeadingDelta(headingDegrees - previous.headingDegrees)
            } else {
                null
            }
            val signalDelta = currentRssi - previous.smoothedRssi
            if (turn != null && abs(turn) >= MIN_HEADING_TURN_DEGREES &&
                abs(signalDelta) >= MIN_SIGNAL_CHANGE_DB
            ) {
                val evidence = (
                    (abs(turn) / FULL_TURN_DEGREES).coerceIn(0.0, 1.0) *
                        (abs(signalDelta) / SIGNIFICANT_SIGNAL_CHANGE_DB).coerceIn(0.0, 1.0)
                    )
                val vote = if (signalDelta * turn > 0f) 1.0 else -1.0
                sideScore = (sideScore * SIDE_MEMORY + vote * evidence).coerceIn(-1.0, 1.0)
            }
        }

        samples.addLast(
            Sample(
                point = measurementPoint,
                headingDegrees = headingDegrees,
                smoothedRssi = currentRssi,
                proximity = contact.inference.proximity,
                gpsAccuracyMeters = gpsAccuracyMeters.coerceAtLeast(0f),
                observedAt = observedAt
            )
        )
        while (samples.size > MAX_SAMPLES) samples.removeFirst()
        lastSignalAt = observedAt
        estimate = calculateEstimate()
        return estimate
    }

    fun clear() {
        targetId = null
        clearState()
    }

    private fun clearFor(newTargetId: String) {
        targetId = newTargetId
        clearState()
    }

    private fun clearState() {
        samples.clear()
        lastSignalAt = Long.MIN_VALUE
        sideScore = 0.0
        estimate = PrsTargetAreaEstimate()
    }

    private fun calculateEstimate(): PrsTargetAreaEstimate {
        val currentSamples = samples.toList()
        if (currentSamples.isEmpty()) return PrsTargetAreaEstimate()

        val side = when {
            sideScore >= SIDE_DECISION_THRESHOLD -> PrsTargetSide.RIGHT
            sideScore <= -SIDE_DECISION_THRESHOLD -> PrsTargetSide.LEFT
            else -> PrsTargetSide.UNKNOWN
        }
        val sideSign = when (side) {
            PrsTargetSide.RIGHT -> 1f
            PrsTargetSide.LEFT -> -1f
            PrsTargetSide.UNKNOWN -> 0f
        }
        val strongestRssi = currentSamples.maxOf { it.smoothedRssi }
        val candidates = currentSamples.map { sample ->
            val candidate = if (sideSign != 0f && sample.headingDegrees != null) {
                projectMeters(
                    point = sample.point,
                    distanceMeters = referenceDistanceMeters(sample.proximity),
                    bearingDegrees = sample.headingDegrees + sideSign * SIDE_OFFSET_DEGREES
                )
            } else {
                sample.point
            }
            val weight = 2.0.pow(((sample.smoothedRssi - strongestRssi) / RSSI_WEIGHT_STEP_DB).coerceAtLeast(-4.0))
            sample to WeightedPoint(candidate, weight)
        }

        val reference = candidates.first().second.point
        var totalWeight = 0.0
        var east = 0.0
        var north = 0.0
        candidates.forEach { (_, weighted) ->
            val local = toLocalMeters(reference, weighted.point)
            totalWeight += weighted.weight
            east += local.first * weighted.weight
            north += local.second * weighted.weight
        }
        val centre = fromLocalMeters(
            reference,
            east / totalWeight,
            north / totalWeight
        )

        val spreadSquared = candidates.sumOf { (_, weighted) ->
            val local = toLocalMeters(centre, weighted.point)
            weighted.weight * (local.first * local.first + local.second * local.second)
        } / totalWeight
        val spreadMeters = sqrt(spreadSquared)
        val latestGpsAccuracy = currentSamples.map { it.gpsAccuracyMeters }.average().toFloat()
        val sampleConfidence = (currentSamples.size / MAX_CONFIDENCE_SAMPLES.toFloat()).coerceIn(0f, 1f)
        val band = currentSamples.last().proximity
        val contractedBandRadius = referenceRadiusMeters(band) * (1f - RADIUS_CONTRACTION * sampleConfidence)
        val radius = maxOf(
            minimumRadiusMeters(band),
            contractedBandRadius + spreadMeters.toFloat() * SPREAD_FACTOR + latestGpsAccuracy * GPS_ACCURACY_FACTOR
        )

        return PrsTargetAreaEstimate(
            center = centre,
            radiusMeters = radius.coerceAtMost(MAX_RADIUS_METERS),
            confidence = (sampleConfidence * (0.70f + abs(sideScore).toFloat() * 0.30f)).coerceIn(0f, 1f),
            side = side,
            sideConfidence = abs(sideScore).toFloat().coerceIn(0f, 1f),
            sampleCount = currentSamples.size
        )
    }

    private data class WeightedPoint(val point: GeoPoint, val weight: Double)

    private fun referenceRadiusMeters(proximity: PrsProximityBand): Float = when (proximity) {
        PrsProximityBand.NEAR -> 40f
        PrsProximityBand.MEDIUM -> 90f
        PrsProximityBand.FAR -> 180f
        PrsProximityBand.UNKNOWN -> 180f
    }

    private fun minimumRadiusMeters(proximity: PrsProximityBand): Float = when (proximity) {
        PrsProximityBand.NEAR -> 15f
        PrsProximityBand.MEDIUM -> 28f
        PrsProximityBand.FAR -> 55f
        PrsProximityBand.UNKNOWN -> 65f
    }

    private fun referenceDistanceMeters(proximity: PrsProximityBand): Double = when (proximity) {
        PrsProximityBand.NEAR -> 25.0
        PrsProximityBand.MEDIUM -> 75.0
        PrsProximityBand.FAR -> 150.0
        PrsProximityBand.UNKNOWN -> 120.0
    }

    private fun projectMeters(point: GeoPoint, distanceMeters: Double, bearingDegrees: Float): GeoPoint {
        val bearing = bearingDegrees * PI / 180.0
        val northMeters = distanceMeters * cos(bearing)
        val eastMeters = distanceMeters * sin(bearing)
        val latitudeRadians = point.latitude * PI / 180.0
        return GeoPoint(
            latitude = point.latitude + northMeters / METERS_PER_DEGREE_LATITUDE,
            longitude = point.longitude + eastMeters / (METERS_PER_DEGREE_LONGITUDE * cos(latitudeRadians))
        )
    }

    private fun toLocalMeters(reference: GeoPoint, point: GeoPoint): Pair<Double, Double> {
        val latitudeRadians = reference.latitude * PI / 180.0
        return (
            (point.longitude - reference.longitude) * METERS_PER_DEGREE_LONGITUDE * cos(latitudeRadians)
            ) to ((point.latitude - reference.latitude) * METERS_PER_DEGREE_LATITUDE)
    }

    private fun fromLocalMeters(reference: GeoPoint, eastMeters: Double, northMeters: Double): GeoPoint {
        val latitudeRadians = reference.latitude * PI / 180.0
        return GeoPoint(
            latitude = reference.latitude + northMeters / METERS_PER_DEGREE_LATITUDE,
            longitude = reference.longitude + eastMeters / (METERS_PER_DEGREE_LONGITUDE * cos(latitudeRadians))
        )
    }

    companion object {
        private const val MAX_SAMPLES = 12
        private const val MAX_CONFIDENCE_SAMPLES = 8
        private const val MIN_HEADING_TURN_DEGREES = 10f
        private const val MIN_SIGNAL_CHANGE_DB = 1.5f
        private const val SIGNIFICANT_SIGNAL_CHANGE_DB = 6.0
        private const val FULL_TURN_DEGREES = 90.0
        private const val SIDE_OFFSET_DEGREES = 65f
        private const val SIDE_MEMORY = 0.70
        private const val SIDE_DECISION_THRESHOLD = 0.25
        private const val RSSI_WEIGHT_STEP_DB = 6.0
        private const val RADIUS_CONTRACTION = 0.60f
        private const val SPREAD_FACTOR = 0.35f
        private const val GPS_ACCURACY_FACTOR = 0.50f
        private const val MAX_RADIUS_METERS = 450f
        private const val METERS_PER_DEGREE_LATITUDE = 110_540.0
        private const val METERS_PER_DEGREE_LONGITUDE = 111_320.0

        internal fun shortestHeadingDelta(delta: Float): Float {
            var normalized = delta % 360f
            if (normalized > 180f) normalized -= 360f
            if (normalized < -180f) normalized += 360f
            return normalized
        }
    }
}
