package com.suri.pipsurios.terrain

import android.content.Context
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Locale

object TerrainOverlayCodec {
    fun encode(value: MapOverlays): String = buildString {
        value.respawns.forEach { appendLine("R|${it.id}|${decimal(it.point.latitude)}|${decimal(it.point.longitude)}") }
        value.radZones.forEach { zone ->
            append("Z|${zone.id}")
            zone.vertices.forEach { append("|${decimal(it.latitude)},${decimal(it.longitude)}") }
            appendLine()
        }
    }

    fun decode(text: String): MapOverlays {
        val respawns = mutableListOf<Respawn>(); val zones = mutableListOf<RadZone>()
        text.lineSequence().filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split('|')
            runCatching {
                when (parts.firstOrNull()) {
                    "R" -> respawns += Respawn(parts[1], GeoPoint(parts[2].toDouble(), parts[3].toDouble()))
                    "Z" -> zones += RadZone(parts[1], parts.drop(2).map { point ->
                        point.split(',').let { GeoPoint(it[0].toDouble(), it[1].toDouble()) }
                    })
                }
            }
        }
        return MapOverlays(respawns, zones)
    }

    private fun decimal(value: Double) = String.format(Locale.US, "%.8f", value)
}

class TerrainOverlayRepository(private val root: File) {
    fun load(mapId: String): MapOverlays = File(root, "$mapId.overlays").takeIf { it.isFile }
        ?.let { runCatching { TerrainOverlayCodec.decode(it.readText(Charsets.UTF_8)) }.getOrNull() }
        ?: MapOverlays()

    fun save(mapId: String, overlays: MapOverlays) {
        require(mapId.matches(Regex("[a-z0-9_-]+")))
        root.mkdirs()
        val target = File(root, "$mapId.overlays")
        val temporary = File.createTempFile(".terrain-", ".tmp", root)
        temporary.writeText(TerrainOverlayCodec.encode(overlays), Charsets.UTF_8)
        Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    companion object { fun from(context: Context) = TerrainOverlayRepository(File(context.filesDir, "terrain/overlays")) }
}
