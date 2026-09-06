package com.suri.pipsurios.terrain

import com.suri.pipsurios.BuildConfig
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
    private val profile = BuildConfig.DISTRIBUTION_PROFILE

    val NAVY7 = OfflineMapDefinition(
        mapId = "navy7",
        name = "NAVY7",
        assetPath = "maps/navy_7_terrain.mbtiles",
        assetSha256 = "D21A2C69FA96B213521B7A67AFD40B72A037DAABC6C75BE30CE6F6DC8A84016A",
        // Target center preserved: 40.352971232717216, -3.423711863510395.
        // Sprint 030 footprint: 2 km x 2 km, zoom 16..19, with altitude lines.
        bounds = MapBounds(
            west = -3.435483145327,
            south = 40.343965582217,
            east = -3.411940581694,
            north = 40.361976883217
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val HOME = OfflineMapDefinition(
        mapId = "home",
        name = "HOME",
        assetPath = "maps/home_terrain.mbtiles",
        assetSha256 = "3F02B4C8B55A5D703033651DFFF5C50662879AD680C2E734CF29AC655377BBEA",
        // Target center preserved: 40.4479, -3.870505.
        // Sprint 030 footprint: 2 km x 2 km, cropped from the validated HOME map.
        bounds = MapBounds(
            west = -3.882292827336,
            south = 40.438894497808,
            east = -3.858717172664,
            north = 40.456905502192
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val OFFICE = OfflineMapDefinition(
        mapId = "office",
        name = "OFFICE",
        assetPath = "maps/office_terrain.mbtiles",
        assetSha256 = "F3508FB8C990C9CC54DE110F32554880546486DAB3BC872F65A9F2A8F0F5E20D",
        // Sprint 027 target center: 40.43717182620207, -3.620425636696507.
        // Sprint 029 footprint: 2 km x 2 km, zoom 16..19.
        bounds = MapBounds(
            west = -3.632211590216,
            south = 40.428166307246,
            east = -3.608639683177,
            north = 40.446177345158
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val BRICKTOWN = OfflineMapDefinition(
        mapId = "bricktown",
        name = "BRICKTOWN",
        assetPath = "maps/bricktown_terrain.mbtiles",
        assetSha256 = "E8CAEBE149C7A66FAEFE3E49407F056800F243C8974F5380D09955648120D6AD",
        // Target center: 40.43061267348348, -3.3518651804964206.
        // Sprint 031 footprint: 2 km x 2 km, zoom 16..19, with altitude lines.
        bounds = MapBounds(
            west = -3.363649988871,
            south = 40.421607144278,
            east = -3.340080372122,
            north = 40.439618202689
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )
    val AIRSOFT_TOTAL = OfflineMapDefinition(
        mapId = "airsoft_total",
        name = "AIRSOFT TOTAL",
        assetPath = "maps/airsoft_total_terrain.mbtiles",
        assetSha256 = "120834E597C7D56A76CA79A0B798EEB1451447FDE68B2A057D4A5A4F7772C13D",
        // Target center: 40.81068714827759, -4.269950411691506.
        // Sprint 032 footprint: 2 km x 2 km, zoom 16..19, with altitude lines.
        bounds = MapBounds(
            west = -4.281802203876,
            south = 40.801682213578,
            east = -4.258098619507,
            north = 40.819692082977
        ),
        minZoom = 16,
        maxNativeZoom = 19,
        maxDisplayZoom = 20
    )

    /** TESTING is a profile-specific field with an independently generated asset. */
    val TESTING = when (profile) {
        "FENRIR" -> OfflineMapDefinition(
            mapId = "testing",
            name = "TESTING",
            assetPath = "maps/testing_terrain.mbtiles",
            assetSha256 = "F2A4AE52E3AA7E13F143A3BAF8A38EFBC68E21576837F635A7C45BE65A9239CA",
            bounds = MapBounds(
                west = -3.049735951452,
                south = 43.320061790853,
                east = -3.025076392558,
                north = 43.338063733714
            ),
            minZoom = 16,
            maxNativeZoom = 19,
            maxDisplayZoom = 20
        )
        "ALTAMIRA" -> OfflineMapDefinition(
            mapId = "testing",
            name = "TESTING",
            assetPath = "maps/testing_terrain.mbtiles",
            assetSha256 = "809AF986908D443243615175B294101E7709F84ACD88444894DCCFE397350301",
            // Target center: 40.34897942140349, -3.818235386395919.
            bounds = MapBounds(
                west = -3.830005974191,
                south = 40.339973764668,
                east = -3.806464798600,
                north = 40.357985078138
            ),
            minZoom = 16,
            maxNativeZoom = 19,
            maxDisplayZoom = 20
        )
        "CHECHU" -> OfflineMapDefinition(
            mapId = "testing",
            name = "TESTING",
            assetPath = "maps/testing_terrain.mbtiles",
            assetSha256 = "5F236C8F7F43D5490C94C891373DF51FE3C0588B8AC0BF4B6297A87ECC4E4618",
            // Target center: 40.433753, -3.625904.
            bounds = MapBounds(
                west = -3.637689356588,
                south = 40.424747475701,
                east = -3.614118643412,
                north = 40.442758524299
            ),
            minZoom = 16,
            maxNativeZoom = 19,
            maxDisplayZoom = 20
        )
        else -> OfflineMapDefinition(
            mapId = "testing",
            name = "TESTING",
            assetPath = "maps/testing_terrain.mbtiles",
            assetSha256 = "AA6E8ACA50AA1396E15526E761E9FC0AAB944441700AD522AFD3A4334F53591B",
            bounds = MapBounds(
                west = -0.01,
                south = -0.01,
                east = 0.01,
                north = 0.01
            ),
            minZoom = 16,
            maxNativeZoom = 19,
            maxDisplayZoom = 20
        )
    }

    /** Map files are listed alphabetically; CHOOSE LOCATION is a UI-only exception before them. */
    val maps = when (profile) {
        "FENRIR", "CHECHU" -> listOf(NAVY7, TESTING)
        "ALTAMIRA" -> listOf(NAVY7, TESTING)
        else -> listOf(AIRSOFT_TOTAL, BRICKTOWN, HOME, NAVY7, OFFICE)
    }.sortedBy { it.name }
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
