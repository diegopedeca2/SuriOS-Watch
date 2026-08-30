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
            "-3.453046863510,40.341471232717,-3.394376863510,40.364471232717",
            data.metadata["bounds"]
        )
        assertEquals(5242, data.tileKeys.size)
        assertTrue(data.tileKeys.any { it.zoom == 19 })
    }
}
