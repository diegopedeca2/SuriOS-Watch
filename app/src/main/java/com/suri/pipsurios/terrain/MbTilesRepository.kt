package com.suri.pipsurios.terrain

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File
import java.util.LinkedHashMap

data class TileKey(val zoom: Int, val x: Int, val xyzY: Int)

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
            MbTilesData(tileKeys, metadata, database)
        } catch (error: Throwable) {
            database.close()
            throw error
        }
    }

    private fun materialize(definition: OfflineMapDefinition): File {
        val directory = File(context.filesDir, "terrain/maps").apply { mkdirs() }
        val target = File(directory, "${definition.mapId}.mbtiles")
        val expectedSize = context.assets.openFd(definition.assetPath).length
        if (!target.isFile || target.length() != expectedSize) {
            val temporary = File.createTempFile(".${definition.mapId}-", ".tmp", directory)
            context.assets.open(definition.assetPath).use { input -> temporary.outputStream().use(input::copyTo) }
            check(temporary.renameTo(target) || run { temporary.copyTo(target, overwrite = true); temporary.delete() })
        }
        return target
    }
}
