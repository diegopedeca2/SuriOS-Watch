package com.suri.pipsurios

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.suri.pipsurios.terrain.MbTilesRepository
import com.suri.pipsurios.terrain.OfflineMapCatalog
import com.suri.pipsurios.terrain.OrganizationOverlayRepository
import com.suri.pipsurios.terrain.GeoPoint
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

    @Test fun airsoftOrganizationOverlayOpensOfflineWithGridAndPois() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val overlay = OrganizationOverlayRepository(context.assets).loadOrNull("airsoft_total")
        assertTrue(overlay != null)
        assertEquals("PROVISIONAL_QGIS_REVIEW", overlay?.sourceStatus)
        assertEquals(10, overlay?.grid?.rows?.size)
        assertEquals(9, overlay?.grid?.columns?.size)
        assertTrue(overlay?.pois?.any { it.name == "RESPAWN 2" } == true)
        assertEquals(40.816970, overlay?.pois?.first { it.id == "respawn_2" }?.latitude ?: 0.0, 1e-6)
        assertEquals(-4.273670, overlay?.pois?.first { it.id == "respawn_2" }?.longitude ?: 0.0, 1e-6)
        assertTrue(overlay?.pois?.all { OfflineMapCatalog.AIRSOFT_TOTAL.bounds.contains(it.point) } == true)
        assertEquals(GeoPoint(40.8179, -4.2786), overlay?.fieldBoundary?.first())
        assertTrue(overlay?.fieldBoundary?.all { OfflineMapCatalog.AIRSOFT_TOTAL.bounds.contains(it) } == true)
        assertTrue(overlay?.internalPaths?.flatMap { it.points }
            ?.all { OfflineMapCatalog.AIRSOFT_TOTAL.bounds.contains(it) } == true)
    }

    @Test fun airsoftTerrainUsesLightSuriOsOfflinePalette() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        MbTilesRepository(context).load(OfflineMapCatalog.AIRSOFT_TOTAL).use { data ->
            assertEquals("AIRSOFT TOTAL", data.metadata["name"])
            assertEquals("SURIOS_DAY_V1", data.metadata["style"])
            assertEquals("16", data.metadata["minzoom"])
            assertEquals("19", data.metadata["maxzoom"])
            assertEquals(
                "-4.293653996060,40.792677278878,-4.246246827323,40.828697017677",
                data.metadata["bounds"]
            )
            assertEquals(6727, data.tileKeys.size)
        }
    }

    @Test fun officeFieldTestPoisOpenOfflineAndStayInsideOfficeBounds() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val overlay = OrganizationOverlayRepository(context.assets).loadOrNull("office")
        assertTrue(overlay != null)
        assertEquals("USER_SUPPLIED_COORDINATES", overlay?.sourceStatus)
        assertEquals(listOf("RAST", "REPLICANT", "ELÍAS", "CHURROSTAR"), overlay?.pois?.map { it.name })
        assertTrue(overlay?.pois?.all { OfflineMapCatalog.OFFICE.bounds.contains(it.point) } == true)
    }

    @Test fun majadahondaAssetAndPoisOpenOffline() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        MbTilesRepository(context).load(OfflineMapCatalog.MAJADAHONDA).use { data ->
            assertEquals("majadahonda_terrain", data.metadata["name"])
            assertEquals("SURIOS_DAY_V1", data.metadata["style"])
            assertEquals("-3.889978066677,40.464338453037,-3.866393515684,40.482349377891", data.metadata["bounds"])
            assertEquals(1704, data.tileKeys.size)
        }
        val overlay = OrganizationOverlayRepository(context.assets).loadOrNull("majadahonda")
        assertTrue(overlay != null)
        assertEquals("USER_SUPPLIED_COORDINATES", overlay?.sourceStatus)
        assertEquals(listOf("Boothill", "Luis", "Rocka Rolla"), overlay?.pois?.map { it.name })
        assertTrue(overlay?.pois?.all { OfflineMapCatalog.MAJADAHONDA.bounds.contains(it.point) } == true)
    }
}
