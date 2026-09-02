package com.suri.pipsurios

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.suri.pipsurios.terrain.MbTilesRepository
import com.suri.pipsurios.terrain.OfflineMapCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MbTilesInstrumentedTest {
    @Test fun navy7AssetOpensOfflineWithExpectedMetadataAndTiles() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val data = MbTilesRepository(context).load(OfflineMapCatalog.NAVY7)
        assertEquals("png", data.metadata["format"])
        assertEquals("navy_7_terrain", data.metadata["name"])
        assertEquals("16", data.metadata["minzoom"])
        assertEquals("19", data.metadata["maxzoom"])
        assertEquals(
            "-3.435483145327,40.343965582217,-3.411940581694,40.361976883217",
            data.metadata["bounds"]
        )
        assertEquals(1699, data.tileKeys.size)
        assertTrue(data.tileKeys.any { it.zoom == 19 })
    }

    @Test fun homeAndOfficeAssetsOpenOfflineWithExpectedBounds() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val repository = MbTilesRepository(context)
        repository.load(OfflineMapCatalog.HOME).use { data ->
            assertEquals("home_terrain", data.metadata["name"])
            assertEquals(
                "-3.882292827336,40.438894497808,-3.858717172664,40.456905502192",
                data.metadata["bounds"]
            )
            assertEquals(1699, data.tileKeys.size)
        }
        repository.load(OfflineMapCatalog.OFFICE).use { data ->
            assertEquals("office_terrain", data.metadata["name"])
            assertEquals(
                "-3.632211590216,40.428166307246,-3.608639683177,40.446177345158",
                data.metadata["bounds"]
            )
            assertEquals(1669, data.tileKeys.size)
        }
    }
}
