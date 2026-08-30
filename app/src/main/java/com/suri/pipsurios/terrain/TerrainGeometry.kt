package com.suri.pipsurios.terrain

import kotlin.math.*

object TerrainGeometry {
    fun isInside(point: GeoPoint, polygon: List<GeoPoint>): Boolean {
        var inside = false
        var previous = polygon.last()
        polygon.forEach { current ->
            if ((current.latitude > point.latitude) != (previous.latitude > point.latitude) &&
                point.longitude < (previous.longitude - current.longitude) *
                (point.latitude - current.latitude) / (previous.latitude - current.latitude) + current.longitude
            ) inside = !inside
            previous = current
        }
        return inside
    }

    fun distanceToBorderMeters(point: GeoPoint, polygon: List<GeoPoint>): Double {
        val referenceLatitude = point.latitude * PI / 180.0
        fun xy(value: GeoPoint): Pair<Double, Double> =
            Pair((value.longitude - point.longitude) * PI / 180.0 * EARTH_RADIUS * cos(referenceLatitude),
                (value.latitude - point.latitude) * PI / 180.0 * EARTH_RADIUS)
        return polygon.indices.minOf { index ->
            val (ax, ay) = xy(polygon[index])
            val (bx, by) = xy(polygon[(index + 1) % polygon.size])
            val dx = bx - ax; val dy = by - ay
            val t = if (dx == 0.0 && dy == 0.0) 0.0 else (-(ax * dx + ay * dy) / (dx * dx + dy * dy)).coerceIn(0.0, 1.0)
            hypot(ax + t * dx, ay + t * dy)
        }
    }

    fun nearestZoneDistanceMeters(point: GeoPoint, zones: List<RadZone>): Pair<Double, Boolean>? {
        if (zones.isEmpty()) return null
        val inside = zones.any { isInside(point, it.vertices) }
        return zones.minOf { distanceToBorderMeters(point, it.vertices) } to inside
    }

    private const val EARTH_RADIUS = 6_371_000.0
}

object TerrainRadiationTuning {
    const val RADIATION_TRIGGER_DISTANCE_METERS = 10.0
    const val LOCATION_MAX_ACCURACY_METERS = 35f
    const val LEVEL_SMOOTHING = 0.22f
    const val EXIT_HYSTERESIS_METERS = 2.0
}

class TerrainRadiationController {
    private var level = 0f
    private var active = false

    fun update(distanceMeters: Double?, inside: Boolean, accuracyMeters: Float): Float {
        // Accuracy is useful information for the caller, but it must not be a
        // hard gate: Wear GPS fixes commonly report >35 m even when the user
        // is already inside a user-drawn zone. The zone geometry remains the
        // source of truth for activation.
        if (distanceMeters == null || !distanceMeters.isFinite()) {
            reset()
            return 0f
        }
        val threshold = TerrainRadiationTuning.RADIATION_TRIGGER_DISTANCE_METERS +
            if (active) TerrainRadiationTuning.EXIT_HYSTERESIS_METERS else 0.0
        val target = when {
            inside -> 1f
            distanceMeters >= threshold -> 0f
            else -> ((threshold - distanceMeters) / TerrainRadiationTuning.RADIATION_TRIGGER_DISTANCE_METERS).toFloat().coerceIn(0f, 1f)
        }
        if (target <= 0f) {
            reset()
            return 0f
        }
        active = target > 0f
        return smoothToward(target)
    }

    fun reset() {
        level = 0f
        active = false
    }

    private fun smoothToward(target: Float): Float {
        level += TerrainRadiationTuning.LEVEL_SMOOTHING * (target - level)
        if (level < 0.005f && target == 0f) level = 0f
        return level
    }
}
