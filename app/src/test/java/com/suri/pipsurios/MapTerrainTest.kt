package com.suri.pipsurios

import com.suri.pipsurios.terrain.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MapTerrainTest {
    @Test fun panVectorsRemainIndependentOrthogonalAndReversibleAtEveryCardinalHeading() {
        val deltas = listOf(
            100f to 0f, 0f to 100f, -100f to 0f, 0f to -100f,
            100f to 100f, -100f to 100f
        )
        listOf(0f, 45f, 90f, 180f, 270f).forEach { heading ->
            val transform = TerrainViewportTransform(OfflineMapCatalog.NAVY7.bounds.center, 19.8f, 2340, 1080, heading)
            val world = deltas.map { transform.screenDeltaToWorldDelta(it.first, it.second) }
            deltas.zip(world).forEach { (screen, map) ->
                val restored = transform.worldDeltaToScreenDelta(map.x, map.y)
                assertEquals(screen.first, restored.first, 0.001f)
                assertEquals(screen.second, restored.second, 0.001f)
                val screenMagnitude = kotlin.math.hypot(screen.first, screen.second)
                val restoredMagnitude = kotlin.math.hypot(restored.first, restored.second)
                assertEquals(screenMagnitude, restoredMagnitude, 0.001f)
            }
            fun dot(a: WorldPixel, b: WorldPixel) = a.x * b.x + a.y * b.y
            fun cross(a: WorldPixel, b: WorldPixel) = a.x * b.y - a.y * b.x
            assertEquals(0.0, dot(world[0], world[1]), 1e-6)
            assertEquals(0.0, world[0].x + world[2].x, 1e-6)
            assertEquals(0.0, world[0].y + world[2].y, 1e-6)
            assertEquals(0.0, world[1].x + world[3].x, 1e-6)
            assertEquals(0.0, world[1].y + world[3].y, 1e-6)
            assertTrue(kotlin.math.abs(cross(world[0], world[1])) > 1e-6)
        }
    }

    @Test fun headingZeroKeepsPureScreenAxesPureInWorldCoordinates() {
        val transform = TerrainViewportTransform(OfflineMapCatalog.NAVY7.bounds.center, 19f, 2340, 1080, 0f)
        val horizontal = transform.screenDeltaToWorldDelta(100f, 0f)
        val vertical = transform.screenDeltaToWorldDelta(0f, 100f)
        assertEquals(0.0, horizontal.y, 1e-9)
        assertEquals(0.0, vertical.x, 1e-9)
    }

    @Test fun headingNinetyMapsScreenAxesToDistinctOrthogonalWorldAxes() {
        val transform = TerrainViewportTransform(OfflineMapCatalog.NAVY7.bounds.center, 19f, 2340, 1080, 90f)
        val horizontal = transform.screenDeltaToWorldDelta(100f, 0f)
        val vertical = transform.screenDeltaToWorldDelta(0f, 100f)
        assertEquals(0.0, horizontal.x, 1e-5)
        assertEquals(0.0, vertical.y, 1e-5)
        assertTrue(horizontal.y > 0.0)
        assertTrue(vertical.x < 0.0)
    }

    @Test fun coverageConstraintScalesBothPanAxesTogetherInsteadOfCreatingDiagonalDrift() {
        val coverage = TerrainTileCoverage(19, 1000.0, 3560.0, 2000.0, 3536.0)
        val startWorld = WorldPixel(2280.0, 2768.0)
        val start = WebMercator.fromWorldPixel(startWorld, 19)
        val requested = WebMercator.fromWorldPixel(WorldPixel(3000.0, 3100.0), 19)
        val constrained = coverage.constrainCenterMovement(start, requested, 20f, 1000, 600)
        val result = WebMercator.toWorldPixel(constrained, 19)
        val requestedDx = 720.0
        val requestedDy = 332.0
        val resultDx = result.x - startWorld.x
        val resultDy = result.y - startWorld.y
        assertEquals(0.0, requestedDx * resultDy - requestedDy * resultDx, 1e-4)
        assertTrue(resultDx >= -1e-5 && resultDx <= requestedDx + 1e-5)
        assertTrue(resultDy >= -1e-5 && resultDy <= requestedDy + 1e-5)
    }

    @Test fun headingSmoothingCrossesNorthByShortestPath() {
        val smoother = CircularHeadingSmoother(alpha=0.5f)
        assertEquals(359f, smoother.update(359f), 0.01f)
        val crossed = smoother.update(1f)
        assertTrue(crossed < 2f || crossed > 358f)
    }

    @Test fun nativeAndDisplayZoomExposeControlledOverzoom() {
        assertEquals(19, TerrainZoomTuning.MAX_NATIVE_ZOOM)
        assertEquals(20, TerrainZoomTuning.MAX_DISPLAY_ZOOM)
        assertEquals(20, OfflineMapCatalog.NAVY7.maxDisplayZoom)
    }

    @Test fun terrainCatalogKeepsChooseLocationFirstAndMapsAlphabetical() {
        assertEquals(listOf("HOME", "NAVY7", "OFFICE"), OfflineMapCatalog.maps.map { it.name })
        assertEquals("choose-location", TerrainFieldSelection.CHOOSE_LOCATION_ID)
        assertEquals("CHOOSE LOCATION", TerrainFieldSelection.CHOOSE_LOCATION_LABEL)
    }

    @Test fun officeCenterMatchesSprint027RequestedCoordinates() {
        val center = OfflineMapCatalog.OFFICE.bounds.center
        assertEquals(40.43717182620207, center.latitude, 1e-12)
        assertEquals(-3.620425636696507, center.longitude, 1e-12)
        assertTrue(OfflineMapCatalog.OFFICE.bounds.contains(center))
        assertEquals("maps/office_terrain.mbtiles", OfflineMapCatalog.OFFICE.assetPath)
        assertEquals(19, OfflineMapCatalog.OFFICE.maxNativeZoom)
    }

    @Test fun sprint30Navy7CenterMatchesRequestedCoordinates() {
        val center = OfflineMapCatalog.NAVY7.bounds.center
        assertEquals(40.352971232717216, center.latitude, 1e-12)
        assertEquals(-3.423711863510395, center.longitude, 1e-12)
        assertTrue(OfflineMapCatalog.NAVY7.bounds.contains(GeoPoint(40.352971232717216, -3.423711863510395)))
        assertEquals(-3.435483145327, OfflineMapCatalog.NAVY7.bounds.west, 1e-12)
        assertEquals(40.343965582217, OfflineMapCatalog.NAVY7.bounds.south, 1e-12)
        assertEquals(-3.411940581694, OfflineMapCatalog.NAVY7.bounds.east, 1e-12)
        assertEquals(40.361976883217, OfflineMapCatalog.NAVY7.bounds.north, 1e-12)
    }

    @Test fun headingRotatesAroundStableUserPivotWithoutChangingViewportOrCenter() {
        val center = OfflineMapCatalog.NAVY7.bounds.center
        val user = GeoPoint(center.latitude + 0.0002, center.longitude - 0.0002)
        val base = TerrainViewportTransform(center, 18f, 2340, 1080, 0f)
        val pivot = base.geoToMapScreen(user)
        val rotated = base.copy(headingDegrees = 90f, pivotX = pivot.first, pivotY = pivot.second)
        val userScreen = rotated.geoToScreen(user)
        assertEquals(pivot.first, userScreen.first, 0.01f)
        assertEquals(pivot.second, userScreen.second, 0.01f)
        assertEquals(2340, rotated.width)
        assertEquals(1080, rotated.height)
        assertEquals(center, rotated.center)
    }

    @Test fun rotatedScreenWorldTransformIsExactlyReversible() {
        val transform = TerrainViewportTransform(OfflineMapCatalog.NAVY7.bounds.center, 19.3f, 2340, 1080, 237f, 900f, 510f)
        val source = GeoPoint(40.3526, -3.4232)
        val screen = transform.geoToScreen(source)
        val restored = transform.screenToGeo(screen.first, screen.second)
        assertEquals(source.latitude, restored.latitude, 1e-7)
        assertEquals(source.longitude, restored.longitude, 1e-7)
    }

    @Test fun headingPreservesRadialDistanceAndNeverChangesCameraOrViewport() {
        val center = OfflineMapCatalog.NAVY7.bounds.center
        val point = GeoPoint(center.latitude + 0.0004, center.longitude + 0.0007)
        val base = TerrainViewportTransform(center, 18.4f, 2340, 1080, 0f)
        val original = base.geoToScreen(point)
        val radius = kotlin.math.hypot(original.first - 1170f, original.second - 540f)
        listOf(45f, 90f, 179f, 271f, 359f).forEach { heading ->
            val rotated = base.copy(headingDegrees = heading)
            val screen = rotated.geoToScreen(point)
            assertEquals(radius, kotlin.math.hypot(screen.first - 1170f, screen.second - 540f), 0.02f)
            assertEquals(center, rotated.center)
            assertEquals(2340, rotated.width)
            assertEquals(1080, rotated.height)
        }
    }

    @Test fun panUpdatesMapCenterOnceAndMovesWorldWithFinger() {
        val center = OfflineMapCatalog.NAVY7.bounds.center
        val base = TerrainViewportTransform(center, 18f, 2340, 1080, 37f)
        val moved = base.applyGesture(1170f, 540f, 100f, 0f, 1f, 16f, 20f)
        val oldCenterOnScreen = moved.geoToScreen(center)
        assertEquals(1270f, oldCenterOnScreen.first, 0.05f)
        assertEquals(540f, oldCenterOnScreen.second, 0.05f)
        assertEquals(base.zoom, moved.zoom)
        assertEquals(base.width, moved.width)
        assertEquals(base.height, moved.height)
    }

    @Test fun pinchKeepsGeographyUnderCentroidAndViewportSizeIsImmutable() {
        val base = TerrainViewportTransform(OfflineMapCatalog.NAVY7.bounds.center, 17.2f, 2340, 1080, 123f)
        val anchor = base.screenToGeo(760f, 420f)
        val zoomed = base.applyGesture(760f, 420f, 0f, 0f, 1.8f, 16f, 20f)
        val anchorAfter = zoomed.geoToScreen(anchor)
        assertEquals(760f, anchorAfter.first, 0.05f)
        assertEquals(420f, anchorAfter.second, 0.05f)
        assertTrue(zoomed.zoom > base.zoom)
        assertEquals(2340, zoomed.width)
        assertEquals(1080, zoomed.height)
    }

    @Test fun tileCoverageKeepsEveryRotatedViewportCornerInsideRaster() {
        val keys = buildSet {
            for (x in 257154..257163) for (y in 197809..197814) add(TileKey(19,x,y))
        }
        val coverage = TerrainTileCoverage.from(keys,19)!!
        val minimum = coverage.minimumDisplayZoom(2340,1080,16f,20f)
        assertTrue(minimum in 19.7f..20f)
        val clamped = coverage.clampCenterForFullRotation(OfflineMapCatalog.NAVY7.bounds.center,minimum,2340,1080)
        listOf(0f,45f,90f,137f,270f).forEach { heading ->
            val transform = TerrainViewportTransform(clamped,minimum,2340,1080,heading)
            listOf(0f to 0f,2340f to 0f,2340f to 1080f,0f to 1080f).forEach { corner ->
                val world = WebMercator.toWorldPixel(transform.screenToGeo(corner.first,corner.second),19)
                assertTrue(world.x in coverage.minPixelX..coverage.maxPixelX)
                assertTrue(world.y in coverage.minPixelY..coverage.maxPixelY)
            }
        }
    }
    private val zone = RadZone("z", listOf(
        GeoPoint(40.0, -3.0), GeoPoint(40.0, -2.999), GeoPoint(40.001, -2.999), GeoPoint(40.001, -3.0)
    ))

    @Test fun webMercatorRoundTripAndNavyBounds() {
        val point = GeoPoint(40.3527, -3.423)
        val result = WebMercator.fromWorldPixel(WebMercator.toWorldPixel(point, 19), 19)
        assertEquals(point.latitude, result.latitude, 1e-7)
        assertEquals(point.longitude, result.longitude, 1e-7)
        assertTrue(OfflineMapCatalog.NAVY7.bounds.contains(point))
    }

    @Test fun polygonInsideAndNearestBorderNotCenter() {
        val insideNearEdge = GeoPoint(40.0005, -2.99999)
        assertTrue(TerrainGeometry.isInside(insideNearEdge, zone.vertices))
        assertTrue(TerrainGeometry.distanceToBorderMeters(insideNearEdge, zone.vertices) < 2.0)
        assertFalse(TerrainGeometry.isInside(GeoPoint(40.002, -3.0), zone.vertices))
    }

    @Test fun geigerMappingUsesZoneGeometryEvenWithPoorGpsAccuracy() {
        val controller = TerrainRadiationController()
        assertEquals(0f, controller.update(20.0, false, 4f))
        val near = controller.update(2.0, false, 4f)
        assertTrue(near > 0f && near < 1f)
        assertTrue(controller.update(1.0, true, 4f) > near)
        val poorAccuracyInside = TerrainRadiationController().update(0.0, true, 80f)
        assertTrue(poorAccuracyInside > 0f)
    }

    @Test fun geigerMappingActivatesNearZoneWithPoorGpsAccuracy() {
        val near = TerrainRadiationController().update(2.0, false, 80f)
        assertTrue(near > 0f)
    }

    @Test fun geigerMappingStopsImmediatelyWhenZoneIsRemovedOrExited() {
        val controller = TerrainRadiationController()
        assertTrue(controller.update(0.0, true, 80f) > 0f)
        assertEquals(0f, controller.update(null, false, 80f))

        assertTrue(controller.update(0.0, true, 80f) > 0f)
        assertEquals(0f, controller.update(20.0, false, 80f))
    }

    @Test fun overlaySerializationPreservesMultipleMapsGeometry() {
        val source = MapOverlays(listOf(Respawn("r1", GeoPoint(40.1, -3.1)), Respawn("r2", GeoPoint(40.2, -3.2))), listOf(zone))
        assertEquals(source, TerrainOverlayCodec.decode(TerrainOverlayCodec.encode(source)))
    }

    @Test fun destructiveActionsAlwaysRequireConfirmationAndEmptyPreservesDefinition() {
        val initial = MapOverlays(listOf(Respawn("r", GeoPoint(40.0, -3.0))), listOf(zone))
        val actions = MapDestructiveActions(initial)
        actions.selectRespawn("r"); actions.confirm()
        assertEquals(initial, actions.overlays)
        actions.selectRespawn("r"); actions.requestDelete(); actions.cancel(); actions.confirm()
        assertEquals(initial, actions.overlays)
        actions.offerEmpty(); actions.requestEmpty(); actions.confirm()
        assertEquals(MapOverlays(), actions.overlays)
        assertEquals("navy7", OfflineMapCatalog.NAVY7.mapId)
    }
}
