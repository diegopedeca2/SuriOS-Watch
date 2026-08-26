package com.suri.pipsurios.terrain

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.File

data class TileKey(val zoom: Int, val x: Int, val xyzY: Int)
data class MbTilesData(val tiles: Map<TileKey, ImageBitmap>, val metadata: Map<String, String>)

class MbTilesRepository(private val context: Context) {
    fun load(definition: OfflineMapDefinition): MbTilesData {
        val file = materialize(definition)
        val database = SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY)
        return database.use { db ->
            val metadata = buildMap {
                db.rawQuery("SELECT name,value FROM metadata", null).use { cursor ->
                    while (cursor.moveToNext()) put(cursor.getString(0), cursor.getString(1))
                }
            }
            require(metadata["format"] == "png") { "Unsupported MBTiles format" }
            val tiles = buildMap {
                db.rawQuery("SELECT zoom_level,tile_column,tile_row,tile_data FROM tiles", null).use { cursor ->
                    while (cursor.moveToNext()) {
                        val zoom = cursor.getInt(0); val x = cursor.getInt(1); val tmsY = cursor.getInt(2)
                        val bytes = cursor.getBlob(3)
                        val bitmap = requireNotNull(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
                        put(TileKey(zoom, x, (1 shl zoom) - 1 - tmsY), bitmap.asImageBitmap())
                    }
                }
            }
            MbTilesData(tiles, metadata)
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
