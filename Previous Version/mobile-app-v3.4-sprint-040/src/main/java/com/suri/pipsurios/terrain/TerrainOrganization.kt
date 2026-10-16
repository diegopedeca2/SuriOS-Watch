package com.suri.pipsurios.terrain

import android.content.res.AssetManager
import java.io.IOException

@JvmInline
value class OrganizationPoiType(val id: String) {
    companion object {
        val RESPAWN = OrganizationPoiType("RESPAWN")
        val BASE = OrganizationPoiType("BASE")
        val POI = OrganizationPoiType("POI")
        val AMMO = OrganizationPoiType("AMMO")
        val ENTRANCE = OrganizationPoiType("ENTRANCE")
        val SAFE_ZONE = OrganizationPoiType("SAFE_ZONE")
        val PARKING = OrganizationPoiType("PARKING")

        fun fromWire(value: String): OrganizationPoiType = OrganizationPoiType(
            value.trim().uppercase().replace('-', '_').replace(' ', '_')
        )
    }
}

data class OrganizationPoi(
    val id: String,
    val type: OrganizationPoiType,
    val name: String,
    val point: GeoPoint,
) {
    // Keep the wire contract explicit for exporters and future consumers while
    // retaining one shared geographic value for map rendering and navigation.
    val latitude: Double get() = point.latitude
    val longitude: Double get() = point.longitude
}

data class OrganizationPath(
    val id: String,
    val name: String,
    val points: List<GeoPoint>,
) {
    init {
        require(points.size >= 2) { "An organization path needs at least two points" }
    }
}

data class OrganizationGridCell(
    val id: String,
    val bounds: MapBounds,
)

data class OrganizationGrid(
    val bounds: MapBounds,
    val rows: List<String>,
    val columns: List<String>,
    val cells: List<OrganizationGridCell> = emptyList(),
) {
    init {
        require(rows.isNotEmpty()) { "Organization grid needs rows" }
        require(columns.isNotEmpty()) { "Organization grid needs columns" }
        require(cells.map(OrganizationGridCell::id).distinct().size == cells.size) {
            "Organization grid cell ids must be unique"
        }
    }

    fun cellFor(point: GeoPoint): String? {
        if (!bounds.contains(point)) return null
        cells.firstOrNull { it.bounds.contains(point) }?.let { return it.id }

        val longitudeSpan = bounds.east - bounds.west
        val latitudeSpan = bounds.north - bounds.south
        if (longitudeSpan <= 0.0 || latitudeSpan <= 0.0) return null

        val columnIndex = (((point.longitude - bounds.west) / longitudeSpan) * columns.size)
            .toInt()
            .coerceIn(0, columns.lastIndex)
        val rowIndex = (((bounds.north - point.latitude) / latitudeSpan) * rows.size)
            .toInt()
            .coerceIn(0, rows.lastIndex)
        return "${rows[rowIndex]}-${columns[columnIndex]}"
    }
}

data class OrganizationOverlay(
    val mapId: String,
    val sourceStatus: String,
    val grid: OrganizationGrid?,
    val fieldBoundary: List<GeoPoint>,
    val internalPaths: List<OrganizationPath>,
    val pois: List<OrganizationPoi>,
) {
    // Logical layer views keep the source POI collection extensible while
    // allowing the map renderer and future exports to address each layer
    // independently. Navigation still consumes MapDestination, not a POI
    // subtype.
    val respawns: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.RESPAWN }
    val bases: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.BASE }
    val pointsOfInterest: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.POI }
    val ammunition: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.AMMO }
    val entrances: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.ENTRANCE }
    val safeZones: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.SAFE_ZONE }
    val parking: List<OrganizationPoi>
        get() = pois.filter { it.type == OrganizationPoiType.PARKING }
}

/**
 * Small, dependency-free vector format for packaged organization data.
 * The Android app consumes vector records, never the original organization image.
 */
object OrganizationOverlayCodec {
    fun decode(text: String): OrganizationOverlay {
        var mapId: String? = null
        var sourceStatus = "UNKNOWN"
        var gridBounds: MapBounds? = null
        var gridRows = emptyList<String>()
        var gridColumns = emptyList<String>()
        val gridCells = mutableListOf<OrganizationGridCell>()
        var boundary = emptyList<GeoPoint>()
        val paths = mutableListOf<OrganizationPath>()
        val pois = mutableListOf<OrganizationPoi>()

        text.lineSequence()
            .map(String::trim)
            .filter { it.isNotEmpty() && !it.startsWith("#") }
            .forEach { line ->
                val parts = line.split('|')
                when (parts.firstOrNull()) {
                    "MAP" -> mapId = parts.requireField(1)
                    "META" -> if (parts.requireField(1) == "SOURCE_STATUS") {
                        sourceStatus = parts.requireField(2)
                    }
                    "GRID" -> {
                        val west = parts.requireField(1).toDouble()
                        val south = parts.requireField(2).toDouble()
                        val east = parts.requireField(3).toDouble()
                        val north = parts.requireField(4).toDouble()
                        gridBounds = MapBounds(west, south, east, north)
                        gridRows = parts.requireField(5).split(',').filter(String::isNotBlank)
                        gridColumns = parts.requireField(6).split(',').filter(String::isNotBlank)
                    }
                    "CELL" -> gridCells += OrganizationGridCell(
                        id = parts.requireField(1),
                        bounds = MapBounds(
                            west = parts.requireField(2).toDouble(),
                            south = parts.requireField(3).toDouble(),
                            east = parts.requireField(4).toDouble(),
                            north = parts.requireField(5).toDouble(),
                        ),
                    )
                    "BOUNDARY" -> boundary = parts.drop(2).map(::parsePoint)
                    "PATH" -> paths += OrganizationPath(
                        id = parts.requireField(1),
                        name = parts.requireField(2),
                        points = parts.drop(3).map(::parsePoint),
                    )
                    "POI" -> pois += OrganizationPoi(
                        id = parts.requireField(1),
                        type = OrganizationPoiType.fromWire(parts.requireField(2)),
                        name = parts.requireField(3),
                        point = GeoPoint(
                            latitude = parts.requireField(4).toDouble(),
                            longitude = parts.requireField(5).toDouble(),
                        ),
                    )
                }
            }

        return OrganizationOverlay(
            mapId = requireNotNull(mapId) { "Organization overlay has no MAP record" },
            sourceStatus = sourceStatus,
            grid = gridBounds?.let { bounds ->
                OrganizationGrid(bounds, gridRows, gridColumns, gridCells.toList())
            },
            fieldBoundary = boundary,
            internalPaths = paths,
            pois = pois,
        )
    }

    private fun parsePoint(value: String): GeoPoint {
        val parts = value.split(',')
        require(parts.size == 2) { "Invalid organization point: $value" }
        return GeoPoint(parts[0].toDouble(), parts[1].toDouble())
    }

    private fun List<String>.requireField(index: Int): String =
        getOrNull(index)?.takeIf(String::isNotBlank)
            ?: error("Organization overlay field $index is missing")
}

class OrganizationOverlayRepository(private val assets: AssetManager) {
    fun loadOrNull(mapId: String): OrganizationOverlay? = runCatching {
        assets.open("maps/${mapId}_organization.overlay")
            .bufferedReader(Charsets.UTF_8)
            .use { OrganizationOverlayCodec.decode(it.readText()) }
    }.getOrElse { error ->
        if (error is IOException) null else throw error
    }
}
