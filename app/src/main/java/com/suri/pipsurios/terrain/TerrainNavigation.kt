package com.suri.pipsurios.terrain

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

enum class DestinationSource {
    USER_WAYPOINT,
    ORGANIZATION_POI,
}

data class MapDestination(
    val id: String,
    val name: String,
    val point: GeoPoint,
    val source: DestinationSource,
)

data class NavigationReading(
    val distanceMeters: Double,
    val bearingDegrees: Double,
    val relativeBearingDegrees: Double?,
    val currentGrid: String?,
    val destinationGrid: String?,
)

object NavigationEngine {
    private const val EARTH_RADIUS_METERS = 6_371_000.0

    fun calculate(
        current: GeoPoint,
        headingDegrees: Float?,
        destination: MapDestination,
        grid: OrganizationGrid?,
    ): NavigationReading {
        val bearing = initialBearingDegrees(current, destination.point)
        return NavigationReading(
            distanceMeters = distanceMeters(current, destination.point),
            bearingDegrees = bearing,
            relativeBearingDegrees = headingDegrees?.let { relativeBearingDegrees(it.toDouble(), bearing) },
            currentGrid = grid?.cellFor(current),
            destinationGrid = grid?.cellFor(destination.point),
        )
    }

    fun distanceMeters(from: GeoPoint, to: GeoPoint): Double {
        val latitudeDelta = Math.toRadians(to.latitude - from.latitude)
        val longitudeDelta = Math.toRadians(to.longitude - from.longitude)
        val fromLatitude = Math.toRadians(from.latitude)
        val toLatitude = Math.toRadians(to.latitude)
        val haversine = sin(latitudeDelta / 2.0) * sin(latitudeDelta / 2.0) +
            cos(fromLatitude) * cos(toLatitude) *
            sin(longitudeDelta / 2.0) * sin(longitudeDelta / 2.0)
        return 2.0 * EARTH_RADIUS_METERS * atan2(sqrt(haversine), sqrt(1.0 - haversine))
    }

    fun initialBearingDegrees(from: GeoPoint, to: GeoPoint): Double {
        val latitudeFrom = Math.toRadians(from.latitude)
        val latitudeTo = Math.toRadians(to.latitude)
        val longitudeDelta = Math.toRadians(to.longitude - from.longitude)
        val y = sin(longitudeDelta) * cos(latitudeTo)
        val x = cos(latitudeFrom) * sin(latitudeTo) -
            sin(latitudeFrom) * cos(latitudeTo) * cos(longitudeDelta)
        return normalizeDegrees(Math.toDegrees(atan2(y, x)))
    }

    fun relativeBearingDegrees(headingDegrees: Double, bearingDegrees: Double): Double {
        return normalizeSignedDegrees(bearingDegrees - headingDegrees)
    }

    fun normalizeDegrees(value: Double): Double = ((value % 360.0) + 360.0) % 360.0

    private fun normalizeSignedDegrees(value: Double): Double {
        val normalized = normalizeDegrees(value)
        return if (normalized > 180.0) normalized - 360.0 else normalized
    }
}
