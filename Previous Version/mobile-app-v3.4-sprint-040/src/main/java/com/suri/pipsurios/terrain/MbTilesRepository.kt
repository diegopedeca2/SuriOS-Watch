package com.suri.pipsurios.terrain

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import java.util.LinkedHashMap
import java.util.Locale
import java.nio.file.Files
import java.nio.file.StandardCopyOption

data class TileKey(val zoom: Int, val x: Int, val xyzY: Int)

private const val MAX_QUERY_TILES = 80

/**
 * MBTiles index plus a bounded decoded-tile cache.
 *
 * NAVY7 is small enough that decoding every tile was harmless. Larger fields
 * must keep the SQLite source indexed and decode only the tiles visible in the
 * viewport, otherwise a complete HOME field can consume hundreds of MiB.
 */
class MbTilesData(
    val tileKeys: Set<TileKey>,
    val metadata: Map<String, String>,
    private val database: SQLiteDatabase,
    private val maxCachedTiles: Int = 96
) : AutoCloseable {
    private val cache = object : LinkedHashMap<TileKey, ImageBitmap>(maxCachedTiles, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<TileKey, ImageBitmap>?): Boolean =
            size > maxCachedTiles
    }

    /** Loads all missing visible tiles with one indexed SQLite query per small batch. */
    fun loadTiles(keys: Set<TileKey>): Map<TileKey, ImageBitmap> {
        if (keys.isEmpty()) return emptyMap()
        val result = LinkedHashMap<TileKey, ImageBitmap>()
        val missing = ArrayList<TileKey>()
        synchronized(cache) {
            keys.forEach { key ->
                val cached = cache[key]
                if (cached == null) missing += key else result[key] = cached
            }
        }
        if (missing.isEmpty()) return result

        synchronized(database) {
            if (!database.isOpen) return@synchronized
            missing.groupBy(TileKey::zoom).forEach { (zoom, zoomKeys) ->
                zoomKeys.chunked(MAX_QUERY_TILES).forEach { batch ->
                    val predicates = batch.joinToString(" OR ") { "(tile_column=? AND tile_row=?)" }
                    val args = ArrayList<String>(1 + batch.size * 2).apply {
                        add(zoom.toString())
                        batch.forEach { key ->
                            add(key.x.toString())
                            add(((1 shl zoom) - 1 - key.xyzY).toString())
                        }
                    }
                    database.rawQuery(
                        "SELECT tile_column,tile_row,tile_data FROM tiles WHERE zoom_level=? AND ($predicates)",
                        args.toTypedArray()
                    ).use { cursor ->
                        while (cursor.moveToNext()) {
                            val key = TileKey(
                                zoom = zoom,
                                x = cursor.getInt(0),
                                xyzY = (1 shl zoom) - 1 - cursor.getInt(1)
                            )
                            val bytes = cursor.getBlob(2)
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()?.let { image ->
                                result[key] = image
                            }
                        }
                    }
                }
            }
        }
        synchronized(cache) { result.forEach { (key, image) -> cache[key] = image } }
        return result
    }

    fun loadTile(key: TileKey): ImageBitmap? {
        synchronized(cache) { cache[key]?.let { return it } }
        val image = synchronized(database) {
            if (!database.isOpen) return@synchronized null
            val tmsY = (1 shl key.zoom) - 1 - key.xyzY
            database.rawQuery(
                "SELECT tile_data FROM tiles WHERE zoom_level=? AND tile_column=? AND tile_row=?",
                arrayOf(key.zoom.toString(), key.x.toString(), tmsY.toString())
            ).use { cursor ->
                if (!cursor.moveToFirst()) {
                    null
                } else {
                    val bytes = cursor.getBlob(0)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
                }
            }
        }
        if (image != null) synchronized(cache) { cache[key] = image }
        return image
    }

    override fun close() {
        synchronized(database) {
            if (database.isOpen) database.close()
        }
        synchronized(cache) { cache.clear() }
    }
}

class MbTilesRepository(private val context: Context) {
    fun load(definition: OfflineMapDefinition): MbTilesData {
        val file = materialize(definition)
        val database = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
        return try {
            val metadata = buildMap {
                database.rawQuery("SELECT name,value FROM metadata", null).use { cursor ->
                    while (cursor.moveToNext()) put(cursor.getString(0), cursor.getString(1))
                }
            }
            require(metadata["format"] == "png") { "Unsupported MBTiles format" }
            validateMetadata(metadata, definition)
            val tileKeys = buildSet {
                database.rawQuery("SELECT zoom_level,tile_column,tile_row FROM tiles", null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val zoom = cursor.getInt(0)
                        val x = cursor.getInt(1)
                        val tmsY = cursor.getInt(2)
                        add(TileKey(zoom, x, (1 shl zoom) - 1 - tmsY))
                    }
                }
            }
            require(tileKeys.isNotEmpty()) { "MBTiles contains no tiles" }
            val data = MbTilesData(tileKeys, metadata, database)
            require(tileKeys.take(SAMPLE_TILE_COUNT).all { data.loadTile(it) != null }) {
                "MBTiles sample tile decode failed"
            }
            data
        } catch (error: Throwable) {
            database.close()
            throw error
        }
    }

    private fun materialize(definition: OfflineMapDefinition): File {
        val directory = File(context.filesDir, "terrain/maps").apply { mkdirs() }
        val target = File(directory, "${definition.mapId}.mbtiles")
        val expectedHash = definition.assetSha256.uppercase(Locale.US)
        require(expectedHash.matches(SHA256_PATTERN)) { "Invalid asset SHA-256 for ${definition.mapId}" }
        if (target.isFile) {
            if (MbTilesVerificationCache.isVerified(target, expectedHash) || sha256(target) == expectedHash) {
                MbTilesVerificationCache.markVerified(target, expectedHash)
                return target
            }
        }

        val temporary = File.createTempFile(".${definition.mapId}-", ".tmp", directory)
        try {
            check(copyAssetAndHash(definition.assetPath, temporary) == expectedHash) {
                "Asset hash mismatch for ${definition.mapId}"
            }
            moveIntoPlace(temporary, target)
            MbTilesVerificationCache.markVerified(target, expectedHash)
        } finally {
            temporary.delete()
        }
        return target
    }

    private fun validateMetadata(metadata: Map<String, String>, definition: OfflineMapDefinition) {
        require(metadata["minzoom"]?.toIntOrNull() == definition.minZoom) { "MBTiles minzoom mismatch" }
        require(metadata["maxzoom"]?.toIntOrNull() == definition.maxNativeZoom) { "MBTiles maxzoom mismatch" }
        val bounds = metadata["bounds"]?.split(',')?.mapNotNull(String::toDoubleOrNull)
        require(bounds?.size == 4) { "MBTiles bounds missing" }
        val parsedBounds = bounds
        val expected = listOf(
            definition.bounds.west,
            definition.bounds.south,
            definition.bounds.east,
            definition.bounds.north
        )
        require(parsedBounds.zip(expected).all { (actual, wanted) -> kotlin.math.abs(actual - wanted) <= BOUNDS_TOLERANCE }) {
            "MBTiles bounds mismatch"
        }
    }

    private fun sha256(file: File): String = MessageDigest.getInstance("SHA-256").let { digest ->
        FileInputStream(file).use { input ->
            val buffer = ByteArray(HASH_BUFFER_SIZE)
            while (true) {
                val count = input.read(buffer)
                if (count < 0) break
                digest.update(buffer, 0, count)
            }
        }
        digest.digest().joinToString("") { "%02X".format(Locale.US, it.toInt() and 0xFF) }
    }

    private fun copyAssetAndHash(assetPath: String, target: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        context.assets.open(assetPath).use { input ->
            target.outputStream().use { output ->
                val buffer = ByteArray(HASH_BUFFER_SIZE)
                while (true) {
                    val count = input.read(buffer)
                    if (count < 0) break
                    output.write(buffer, 0, count)
                    digest.update(buffer, 0, count)
                }
            }
        }
        return digest.digest().joinToString("") { "%02X".format(Locale.US, it.toInt() and 0xFF) }
    }

    private fun moveIntoPlace(source: File, target: File) {
        try {
            Files.move(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.ATOMIC_MOVE,
                StandardCopyOption.REPLACE_EXISTING
            )
        } catch (_: java.nio.file.AtomicMoveNotSupportedException) {
            Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
    }

    private companion object {
        const val SAMPLE_TILE_COUNT = 3
        const val HASH_BUFFER_SIZE = 64 * 1024
        const val BOUNDS_TOLERANCE = 0.000000001
        val SHA256_PATTERN = Regex("[0-9A-F]{64}")
    }
}

private object MbTilesVerificationCache {
    private val verifiedFiles = mutableSetOf<String>()

    @Synchronized
    fun isVerified(file: File, expectedHash: String): Boolean = signature(file, expectedHash) in verifiedFiles

    @Synchronized
    fun markVerified(file: File, expectedHash: String) {
        verifiedFiles += signature(file, expectedHash)
    }

    private fun signature(file: File, expectedHash: String): String =
        "${file.absolutePath}|$expectedHash|${file.length()}|${file.lastModified()}"
}
