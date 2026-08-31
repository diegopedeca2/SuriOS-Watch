package com.suri.pipsurios.terrain

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos

data class GeoPoint(val latitude: Double, val longitude: Double)
data class MapBounds(val west: Double, val south: Double, val east: Double, val north: Double) {
    val center get() = GeoPoint((south + north) / 2.0, (west + east) / 2.0)
    fun contains(point: GeoPoint) = point.longitude in west..east && point.latitude in south..north
}

data class OfflineMapDefinition(
    val mapId: String,
    val name: String,
    val assetPath: String,
    val assetSha256: String,
    val bounds: MapBounds,
    val minZoom: Int,
    val maxNativeZoom: Int,
    val maxDisplayZoom: Int
)

/** UI-only choice used when no terrain field has been selected yet. */
object TerrainFieldSelection {
    const val CHOOSE_LOCATION_ID = "choose-location"
    const val CHOOSE_LOCATION_LABEL = "CHOOSE LOCATION"
}

object OfflineMapCatalog {
    val NAVY7 = OfflineMapDefinition(
        mapId = "navy7",
        name = "NAVY7",
        assetPath = "maps/navy_7_terrain.mbtiles",
        assetSha256 = "885290661BA89E3FC2A60D61C135E623751D6911E06E35597A08EB8075C4F394",
        // Sprint 023 target center: 40.352971232717216, -3.423711863510395.
        // Use the HOME geographic footprint: 5 km x 2.5 km, zoom 16..19.
        bounds = MapBounds(
            west = -3.453046863510395,
            south = 40.341471232717216,
            east = -3.394376863510395,
            north = 40.364471232717216
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val HOME = OfflineMapDefinition(
        mapId = "home",
        name = "HOME",
        assetPath = "maps/home_terrain.mbtiles",
        assetSha256 = "5BA9265B51C681075BA967E6DFB51DE546D51261DE51E915938F2D7A471CFFE5",
        bounds = MapBounds(-3.89984, 40.4364, -3.84117, 40.4594),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val OFFICE = OfflineMapDefinition(
        mapId = "office",
        name = "OFFICE",
        assetPath = "maps/office_terrain.mbtiles",
        assetSha256 = "112362A772C971BA34B31BF6DECF435E4F65053EE318F5F767CC389957BB91B9",
        // Sprint 027 target center: 40.43717182620207, -3.620425636696507.
        // Same HOME geographic footprint: 5 km x 2.5 km, zoom 16..19.
        bounds = MapBounds(
            west = -3.649760636696507,
            south = 40.42567182620207,
            east = -3.591090636696507,
            north = 40.44867182620207
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    /** Map files are listed alphabetically; CHOOSE LOCATION is a UI-only exception before them. */
    val maps = listOf(HOME, NAVY7, OFFICE).sortedBy { it.name }
}

object TerrainZoomTuning {
    const val MIN_ZOOM = 16
    const val MAX_NATIVE_ZOOM = 19
    const val MAX_DISPLAY_ZOOM = 20
}

data class WorldPixel(val x: Double, val y: Double)

/** One immutable viewport snapshot. Heading never changes its size, map center or zoom. */
data class TerrainViewportTransform(
    val center: GeoPoint,
    val zoom: Float,
    val width: Int,
    val height: Int,
    val headingDegrees: Float,
    val pivotX: Float = width / 2f,
    val pivotY: Float = height / 2f
) {
    private val referenceZoom = TerrainZoomTuning.MAX_NATIVE_ZOOM
    private val scale = 2.0.pow(zoom.toDouble() - referenceZoom)
    private val centerWorld = WebMercator.toWorldPixel(center, referenceZoom)

    fun geoToMapScreen(point: GeoPoint): Pair<Float, Float> {
        val target = WebMercator.toWorldPixel(point, referenceZoom)
        return (width / 2.0 + (target.x - centerWorld.x) * scale).toFloat() to
            (height / 2.0 + (target.y - centerWorld.y) * scale).toFloat()
    }

    fun geoToScreen(point: GeoPoint): Pair<Float, Float> =
        rotate(geoToMapScreen(point), -headingDegrees)

    fun screenToGeo(x: Float, y: Float): GeoPoint {
        val unrotated = rotate(x to y, headingDegrees)
        return WebMercator.fromWorldPixel(
            WorldPixel(
                centerWorld.x + (unrotated.first - width / 2.0) / scale,
                centerWorld.y + (unrotated.second - height / 2.0) / scale
            ),
            referenceZoom
        )
    }

    /** Converts a screen-space vector to MAX_NATIVE_ZOOM world pixels. */
    fun screenDeltaToWorldDelta(screenDx: Float, screenDy: Float): WorldPixel {
        val mapDelta = rotate(screenDx to screenDy, headingDegrees, 0f, 0f)
        return WorldPixel(mapDelta.first / scale, mapDelta.second / scale)
    }

    /** Exact inverse of [screenDeltaToWorldDelta]. */
    fun worldDeltaToScreenDelta(worldDx: Double, worldDy: Double): Pair<Float, Float> {
        val scaled = (worldDx * scale).toFloat() to (worldDy * scale).toFloat()
        return rotate(scaled, -headingDegrees, 0f, 0f)
    }

    /** Applies one pan/pinch delta while preserving the geographic point under the gesture centroid. */
    fun applyGesture(
        centroidX: Float,
        centroidY: Float,
        panX: Float,
        panY: Float,
        zoomChange: Float,
        minZoom: Float,
        maxZoom: Float
    ): TerrainViewportTransform {
        val centroidDelta = screenDeltaToWorldDelta(centroidX - width / 2f, centroidY - height / 2f)
        val anchorWorld = WorldPixel(centerWorld.x + centroidDelta.x, centerWorld.y + centroidDelta.y)
        val newZoom = (zoom + kotlin.math.log2(zoomChange)).coerceIn(minZoom, maxZoom)
        val newScale = 2.0.pow(newZoom.toDouble() - referenceZoom)
        val target = rotate(
            (centroidX + panX - width / 2f) to (centroidY + panY - height / 2f),
            headingDegrees,
            0f,
            0f
        )
        val newCenter = WebMercator.fromWorldPixel(
            WorldPixel(
                anchorWorld.x - target.first / newScale,
                anchorWorld.y - target.second / newScale
            ),
            referenceZoom
        )
        return copy(center = newCenter, zoom = newZoom)
    }

    private fun rotate(point: Pair<Float, Float>, degrees: Float, px: Float = pivotX, py: Float = pivotY): Pair<Float, Float> {
        val radians = Math.toRadians(degrees.toDouble())
        val cosine = cos(radians).toFloat()
        val sine = sin(radians).toFloat()
        val x = point.first - px
        val y = point.second - py
        return (px + x * cosine - y * sine) to (py + x * sine + y * cosine)
    }
}

object WebMercator {
    fun toWorldPixel(point: GeoPoint, zoom: Int): WorldPixel {
        val size = 256.0 * 2.0.pow(zoom)
        val latitude = point.latitude.coerceIn(-85.05112878, 85.05112878)
        val sinLatitude = sin(latitude * PI / 180.0)
        return WorldPixel(
            (point.longitude + 180.0) / 360.0 * size,
            (0.5 - ln((1.0 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * PI)) * size
        )
    }

    fun fromWorldPixel(pixel: WorldPixel, zoom: Int): GeoPoint {
        val size = 256.0 * 2.0.pow(zoom)
        return GeoPoint(
            latitude = 90.0 - 360.0 * atan(exp((pixel.y / size - 0.5) * 2.0 * PI)) / PI,
            longitude = pixel.x / size * 360.0 - 180.0
        )
    }
}

data class Respawn(val id: String, val point: GeoPoint)
data class RadZone(val id: String, val vertices: List<GeoPoint>) { init { require(vertices.size >= 3) } }
data class MapOverlays(val respawns: List<Respawn> = emptyList(), val radZones: List<RadZone> = emptyList())
