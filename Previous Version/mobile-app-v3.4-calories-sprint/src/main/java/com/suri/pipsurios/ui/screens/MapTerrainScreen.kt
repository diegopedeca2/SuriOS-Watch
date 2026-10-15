package com.suri.pipsurios.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerInputChange
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.geiger.ClickScheduler
import com.suri.pipsurios.terrain.*
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenBright
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipNeutral
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.ui.theme.PipMapBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt
import java.util.UUID

private suspend fun AwaitPointerEventScope.waitForMapLongPress(
    down: PointerInputChange,
    pass: PointerEventPass
): Boolean {
    return try {
        withTimeout(viewConfiguration.longPressTimeoutMillis) {
            while (true) {
                val event = awaitPointerEvent(pass)
                val change = event.changes.firstOrNull { it.id == down.id }
                if (change == null || !change.pressed ||
                    (change.position - down.position).getDistance() > 24f
                ) {
                    return@withTimeout false
                }
                awaitPointerEvent(PointerEventPass.Final)
            }
            false
        }
    } catch (_: CancellationException) {
        true
    }
}

private enum class TerrainEditMode { NONE, ADD_RESPAWN, ADD_RAD_ZONE }
private enum class TerrainCompassMode { GYRO, NORTH_UP }
private enum class NavigationPrompt { NONE, CONFIRM_WAYPOINT, CONFIRM_CLEAR_WAYPOINT, CONFIRM_STOP }

// Sans-serif keeps the map readable in daylight while preserving the P.R.S.
// green/amber terminal palette used by the rest of the application.
private val MapTextFont = FontFamily.SansSerif

/**
 * Draws a map label only when it is near the current canvas.
 *
 * During a field change there can be one frame where the new map and the old
 * viewport do not yet agree. Compose uses the label position to calculate the
 * text area, so attempting to draw a label tens of kilometres off-screen can
 * create an invalid, enormous constraint and close the app.
 */
private fun DrawScope.drawMapLabel(
    textMeasurer: androidx.compose.ui.text.TextMeasurer,
    text: String,
    topLeft: Offset,
    style: TextStyle,
) {
    val offscreenMargin = 1024f
    if (text.isBlank() ||
        !topLeft.x.isFinite() ||
        !topLeft.y.isFinite() ||
        topLeft.x < -offscreenMargin ||
        topLeft.y < -offscreenMargin ||
        topLeft.x > size.width + offscreenMargin ||
        topLeft.y > size.height + offscreenMargin
    ) {
        return
    }
    drawText(textMeasurer, text, topLeft, style)
}

@Composable
fun MapTerrainScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val mapOptions = OfflineMapCatalog.maps
    var selectedMapId by remember { mutableStateOf(TerrainFieldSelection.CHOOSE_LOCATION_ID) }
    var fieldMenuExpanded by remember { mutableStateOf(false) }
    val selectedDefinition = mapOptions.firstOrNull { it.mapId == selectedMapId }
    val definition = selectedDefinition ?: OfflineMapCatalog.NAVY7
    val hasSelectedMap = selectedDefinition != null
    val overlayRepository = remember { TerrainOverlayRepository.from(context.applicationContext) }
    val organizationRepository = remember { OrganizationOverlayRepository(context.assets) }
    val locationSource = remember { TerrainLocation(context.applicationContext) }
    val headingSource = remember { TerrainHeading(context.applicationContext) }
    val clickScheduler = remember(context) { ClickScheduler(context.applicationContext) }
    var mapData by remember { mutableStateOf<MbTilesData?>(null) }
    var loadedTiles by remember { mutableStateOf<Map<TileKey, ImageBitmap>>(emptyMap()) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var overlays by remember { mutableStateOf(MapOverlays()) }
    var overlaysMapId by remember { mutableStateOf<String?>(null) }
    var organizationOverlay by remember { mutableStateOf<OrganizationOverlay?>(null) }
    var selection by remember { mutableStateOf<MapSelection>(MapSelection.None) }
    var activeDestination by remember { mutableStateOf<MapDestination?>(null) }
    var waypointDraft by remember { mutableStateOf<GeoPoint?>(null) }
    var navigationPrompt by remember { mutableStateOf(NavigationPrompt.NONE) }
    var editMode by remember { mutableStateOf(TerrainEditMode.NONE) }
    var draftZone by remember { mutableStateOf(emptyList<GeoPoint>()) }
    var center by remember { mutableStateOf(definition.bounds.center) }
    var zoom by remember { mutableFloatStateOf(17.5f) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var fix by remember { mutableStateOf<TerrainLocationFix?>(null) }
    var locationStatus by remember { mutableStateOf("WAITING GPS") }
    var geigerLevel by remember { mutableFloatStateOf(0f) }
    var heading by remember { mutableStateOf<Float?>(null) }
    var headingStatus by remember { mutableStateOf("HEADING WAIT") }
    var compassMode by remember { mutableStateOf(TerrainCompassMode.GYRO) }
    val radiation = remember { TerrainRadiationController() }
    val currentGeigerLevel by rememberUpdatedState(geigerLevel)
    val currentSelectedMapId by rememberUpdatedState(selectedMapId)
    val currentMapData by rememberUpdatedState(mapData)
    val effectiveHeading = if (compassMode == TerrainCompassMode.GYRO) heading ?: 0f else 0f
    val currentHeading by rememberUpdatedState(effectiveHeading)
    val currentCenter by rememberUpdatedState(center)
    val currentZoom by rememberUpdatedState(zoom)
    val tileRequestHeading = (effectiveHeading / 15f).roundToInt() * 15f
    val gridTextMeasurer = rememberTextMeasurer()
    val gridLabelStyle = TextStyle(
        color = PipAmber,
        fontSize = 13.75.sp,
        fontFamily = MapTextFont,
        fontWeight = FontWeight.Bold,
    )
    val organizationPoiLabelStyle = gridLabelStyle.copy(color = PipRed)

    LaunchedEffect(selectedMapId) {
        val requestedMapId = selectedMapId
        val requestedDefinition = selectedDefinition
        val previousMapData = mapData
        mapData = null
        previousMapData?.close()
        loadedTiles = emptyMap()
        loadError = null
        overlaysMapId = null
        overlays = MapOverlays()
        organizationOverlay = null
        selection = MapSelection.None
        activeDestination = null
        waypointDraft = null
        navigationPrompt = NavigationPrompt.NONE
        editMode = TerrainEditMode.NONE
        draftZone = emptyList()
        center = requestedDefinition?.bounds?.center ?: OfflineMapCatalog.NAVY7.bounds.center
        zoom = 17.5f
        if (requestedDefinition == null) {
            fix = null
            geigerLevel = 0f
            heading = null
            locationStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
            headingStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
            return@LaunchedEffect
        }
        locationStatus = "WAITING GPS"
        headingStatus = "HEADING WAIT"
        val loadedOverlays = withContext(Dispatchers.IO) {
            overlayRepository.load(requestedDefinition.mapId)
        }
        val loadedOrganizationOverlay = withContext(Dispatchers.IO) {
            organizationRepository.loadOrNull(requestedDefinition.mapId)
        }
        if (currentSelectedMapId != requestedMapId) return@LaunchedEffect
        overlays = loadedOverlays
        overlaysMapId = requestedDefinition.mapId
        organizationOverlay = loadedOrganizationOverlay
        try {
            val loaded = withContext(Dispatchers.IO) {
                MbTilesRepository(context.applicationContext).load(requestedDefinition)
            }
            if (currentSelectedMapId == requestedMapId) {
                mapData = loaded
            } else {
                // A fast second map change may finish this load after the
                // screen has requested another field. Do not publish stale
                // SQLite data into the new viewport.
                loaded.close()
            }
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (error: Throwable) {
            if (currentSelectedMapId == requestedMapId) {
                loadError = error.message ?: "MAP LOAD FAILED"
            }
        }
    }
    val tileCoverage = remember(mapData, definition.maxNativeZoom) {
        mapData?.let { TerrainTileCoverage.from(it.tileKeys, definition.maxNativeZoom) }
    }
    DisposableEffect(mapData) {
        val current = mapData
        onDispose { current?.close() }
    }
    LaunchedEffect(mapData, center, zoom, tileRequestHeading, canvasSize) {
        val data = mapData ?: return@LaunchedEffect
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@LaunchedEffect
        delay(80)
        val tileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
        val requested = visibleTileKeys(
            data,
            TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, tileRequestHeading),
            tileZoom,
            canvasSize
        )
        val missing = requested.filterNot(loadedTiles::containsKey)
        if (missing.isNotEmpty()) {
            val loaded = withContext(Dispatchers.IO) {
                data.loadTiles(missing.toSet())
            }
            if (currentMapData !== data) return@LaunchedEffect
            loadedTiles = loadedTiles.filterKeys { it in requested } + loaded
        } else {
            loadedTiles = loadedTiles.filterKeys { it in requested }
        }
    }
    val minimumCoverageZoom = tileCoverage?.minimumDisplayZoom(
        canvasSize.width, canvasSize.height, definition.minZoom.toFloat(), definition.maxDisplayZoom.toFloat()
    ) ?: definition.minZoom.toFloat()
    LaunchedEffect(tileCoverage, canvasSize) {
        val coverage = tileCoverage ?: return@LaunchedEffect
        zoom = maxOf(zoom, minimumCoverageZoom)
        center = coverage.clampCenterForFullRotation(center, zoom, canvasSize.width, canvasSize.height)
    }
    LaunchedEffect(overlaysMapId, overlays) {
        val mapId = overlaysMapId ?: return@LaunchedEffect
        withContext(Dispatchers.IO) { overlayRepository.save(mapId, overlays) }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (hasSelectedMap && locationSource.hasPermission()) {
            locationSource.start({ fix = it; locationStatus = "GPS ACTIVE" }, { locationStatus = "GPS UNAVAILABLE" })
        } else if (!hasSelectedMap) {
            locationStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
        } else {
            locationStatus = "LOCATION PERMISSION REQUIRED"
        }
    }
    DisposableEffect(locationSource, hasSelectedMap) {
        if (!hasSelectedMap) {
            locationSource.stop()
            fix = null
            locationStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
        } else if (locationSource.hasPermission()) {
            locationSource.start({ fix = it; locationStatus = "GPS ACTIVE" }, { locationStatus = "GPS UNAVAILABLE" })
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
        onDispose { locationSource.stop() }
    }
    DisposableEffect(clickScheduler) {
        onDispose { clickScheduler.release() }
    }
    DisposableEffect(headingSource, compassMode, hasSelectedMap) {
        if (!hasSelectedMap) {
            headingSource.stop()
            heading = null
            headingStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
        } else if (compassMode == TerrainCompassMode.GYRO) {
            headingSource.start(
                { heading = it; headingStatus = "HDG ${it.roundToInt()}" },
                { heading = null; headingStatus = "HEADING UNAVAILABLE" }
            )
        } else {
            headingStatus = "NORTH UP"
        }
        onDispose { headingSource.stop() }
    }

    LaunchedEffect(fix, overlays.radZones, hasSelectedMap) {
        if (!hasSelectedMap) {
            geigerLevel = 0f
            return@LaunchedEffect
        }
        val current = fix
        val distance = current?.let { TerrainGeometry.nearestZoneDistanceMeters(it.point, overlays.radZones) }
        if (distance == null) {
            radiation.reset()
            geigerLevel = 0f
        } else {
            geigerLevel = radiation.update(distance.first, distance.second, current.accuracyMeters)
        }
    }
    LaunchedEffect(geigerLevel > 0.005f, hasSelectedMap) {
        if (hasSelectedMap && geigerLevel > 0.005f) {
            clickScheduler.run { currentGeigerLevel }
        } else {
            clickScheduler.stop()
        }
    }

    fun clamp(point: GeoPoint, atZoom: Float = currentZoom): GeoPoint = tileCoverage
        ?.clampCenterForFullRotation(point, atZoom, canvasSize.width, canvasSize.height)
        ?: GeoPoint(
            point.latitude.coerceIn(definition.bounds.south, definition.bounds.north),
            point.longitude.coerceIn(definition.bounds.west, definition.bounds.east)
        )
    fun viewportTransform(): TerrainViewportTransform {
        return TerrainViewportTransform(currentCenter, currentZoom, canvasSize.width, canvasSize.height, currentHeading)
    }
    fun screenToGeo(offset: Offset): GeoPoint = viewportTransform().screenToGeo(offset.x, offset.y)
    fun geoToMapScreen(point: GeoPoint): Offset {
        val value = viewportTransform().geoToMapScreen(point)
        return Offset(value.first, value.second)
    }
    fun geoToScreen(point: GeoPoint): Offset {
        val value = viewportTransform().geoToScreen(point)
        return Offset(value.first, value.second)
    }
    val visibleOrganizationOverlay = organizationOverlay?.takeIf { overlaysMapId == selectedMapId }
    fun hitOrganizationPoi(offset: Offset) = visibleOrganizationOverlay?.pois
        ?.minByOrNull { (geoToScreen(it.point) - offset).getDistance() }
        ?.takeIf { (geoToScreen(it.point) - offset).getDistance() <= 34f }
    fun hitRespawn(offset: Offset) = overlays.respawns.minByOrNull { (geoToScreen(it.point) - offset).getDistance() }
        ?.takeIf { (geoToScreen(it.point) - offset).getDistance() <= 30f }
    fun hitZone(point: GeoPoint) = overlays.radZones.lastOrNull { TerrainGeometry.isInside(point, it.vertices) }
    val navigationReading = fix?.point?.let { current ->
        activeDestination?.let { destination ->
            NavigationEngine.calculate(current, heading, destination, visibleOrganizationOverlay?.grid)
        }
    }

    Row(Modifier.fillMaxSize().background(PipMapBackground)) {
        Box(
            Modifier
                .weight(0.75f)
                .fillMaxHeight()
                .border(1.dp, PipGreenDim)
        ) {
            val navigationModifier = if (hasSelectedMap && editMode == TerrainEditMode.NONE) Modifier.pointerInput(tileCoverage, canvasSize) {
                detectTransformGestures { centroid, pan, zoomChange, _ ->
                    val updated = viewportTransform().applyGesture(
                        centroid.x, centroid.y, pan.x, pan.y, zoomChange,
                        minimumCoverageZoom, definition.maxDisplayZoom.toFloat()
                    )
                    center = tileCoverage?.constrainCenterMovement(
                        currentCenter, updated.center, updated.zoom, canvasSize.width, canvasSize.height
                    ) ?: clamp(updated.center, updated.zoom)
                    zoom = updated.zoom
                    selection = MapSelection.None
                }
            } else Modifier
            Canvas(
                Modifier.matchParentSize()
                    .onSizeChanged { canvasSize = it }
                    .then(navigationModifier)
                    .pointerInput(editMode, overlays, visibleOrganizationOverlay, activeDestination, center, zoom, hasSelectedMap, fix, tileCoverage) {
                        detectTapGestures(
                            onTap = { offset ->
                                if (hasSelectedMap) {
                                    val userFix = fix
                                    val destination = activeDestination
                                    val waypointMarker = destination
                                        ?.takeIf { it.source == DestinationSource.USER_WAYPOINT }
                                        ?.let { geoToScreen(it.point) }
                                    if (waypointMarker != null && (waypointMarker - offset).getDistance() <= 38f) {
                                        selection = MapSelection.WaypointSelected
                                        return@detectTapGestures
                                    }
                                    val userMarker = userFix?.let { geoToScreen(it.point) }
                                    if (userMarker != null && (userMarker - offset).getDistance() <= 38f) {
                                        center = tileCoverage?.clampCenterForFullRotation(
                                            userFix.point,
                                            zoom,
                                            canvasSize.width,
                                            canvasSize.height
                                        ) ?: clamp(userFix.point, zoom)
                                        selection = MapSelection.None
                                    } else {
                                        val point = screenToGeo(offset)
                                        val organizationPoi = hitOrganizationPoi(offset)
                                        val respawn = if (organizationPoi == null) hitRespawn(offset) else null
                                        val zone = if (organizationPoi == null && respawn == null) hitZone(point) else null
                                        when {
                                            organizationPoi != null -> selection = MapSelection.OrganizationPoiSelected(organizationPoi.id)
                                            respawn != null -> selection = MapSelection.RespawnSelected(respawn.id)
                                            zone != null -> selection = MapSelection.ZoneSelected(zone.id)
                                            editMode == TerrainEditMode.ADD_RESPAWN -> {
                                                overlays = overlays.copy(respawns = overlays.respawns + Respawn(UUID.randomUUID().toString(), point)); editMode = TerrainEditMode.NONE
                                            }
                                            editMode == TerrainEditMode.ADD_RAD_ZONE -> draftZone = draftZone + point
                                            else -> selection = MapSelection.None
                                        }
                                    }
                                }
                            },
                            onDoubleTap = { offset ->
                                if (hasSelectedMap) {
                                    val point = screenToGeo(offset)
                                    if (hitRespawn(offset) == null && hitOrganizationPoi(offset) == null && hitZone(point) == null && editMode == TerrainEditMode.NONE) selection = MapSelection.EmptyOffered
                                }
                            }
                        )
                    }
                    .pointerInput(editMode, visibleOrganizationOverlay, overlays, activeDestination, center, zoom, hasSelectedMap, fix, tileCoverage) {
                        awaitEachGesture {
                            val down = awaitFirstDown(
                                requireUnconsumed = false,
                                pass = PointerEventPass.Initial
                            )
                            val longPress = waitForMapLongPress(down, PointerEventPass.Initial)
                            if (longPress && hasSelectedMap && editMode == TerrainEditMode.NONE) {
                                val offset = down.position
                                val point = screenToGeo(offset)
                                val organizationHit = hitOrganizationPoi(offset)
                                val respawnHit = hitRespawn(offset)
                                val zoneHit = hitZone(point)
                                val isFree = organizationHit == null && respawnHit == null && zoneHit == null
                                if (isFree) {
                                    waypointDraft = point
                                    navigationPrompt = NavigationPrompt.CONFIRM_WAYPOINT
                                    selection = MapSelection.None
                                }
                            }
                            while (true) {
                                val event = awaitPointerEvent(PointerEventPass.Initial)
                                if (event.changes.none { it.pressed }) break
                            }
                        }
                    }
            ) {
                drawRect(PipMapBackground)
                val transform = viewportTransform()
                clipRect {
                    rotate(-currentHeading, Offset(transform.pivotX, transform.pivotY)) {
                        val data = mapData
                        if (data != null) {
                            val desiredTileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
                            val renderTileZoom = loadedTiles.keys
                                .asSequence()
                                .map { it.zoom }
                                .distinct()
                                .minByOrNull { abs(it - desiredTileZoom) }
                                ?: desiredTileZoom
                            val scale = 2.0.pow(zoom.toDouble() - renderTileZoom).toFloat()
                            val centerPixel = WebMercator.toWorldPixel(center, renderTileZoom)
                            loadedTiles.filterKeys { it.zoom == renderTileZoom }.forEach { (key, image) ->
                                val x = (size.width / 2 + (key.x * 256.0 - centerPixel.x) * scale).roundToInt()
                                val y = (size.height / 2 + (key.xyzY * 256.0 - centerPixel.y) * scale).roundToInt()
                                drawImage(image, dstOffset = IntOffset(x, y), dstSize = IntSize(ceil(256 * scale).toInt(), ceil(256 * scale).toInt()))
                            }
                                }
                        visibleOrganizationOverlay?.let { overlay ->
                            val grid = overlay.grid
                            if (grid != null) {
                                // The organization grid is an orientation layer, not
                                // decorative terrain. Black and a wider stroke keep it
                                // readable over the light raster and contour lines.
                                val gridColor = PipBlack
                                val gridStrokeWidth = 3.5f
                                if (grid.cells.isNotEmpty()) {
                                    grid.cells.forEach { cell ->
                                        val northWest = geoToMapScreen(GeoPoint(cell.bounds.north, cell.bounds.west))
                                        val northEast = geoToMapScreen(GeoPoint(cell.bounds.north, cell.bounds.east))
                                        val southEast = geoToMapScreen(GeoPoint(cell.bounds.south, cell.bounds.east))
                                        val southWest = geoToMapScreen(GeoPoint(cell.bounds.south, cell.bounds.west))
                                        drawLine(gridColor, northWest, northEast, strokeWidth = gridStrokeWidth)
                                        drawLine(gridColor, northEast, southEast, strokeWidth = gridStrokeWidth)
                                        drawLine(gridColor, southEast, southWest, strokeWidth = gridStrokeWidth)
                                        drawLine(gridColor, southWest, northWest, strokeWidth = gridStrokeWidth)
                                        val labelPosition = geoToMapScreen(
                                            GeoPoint(
                                                (cell.bounds.north + cell.bounds.south) / 2.0,
                                                (cell.bounds.west + cell.bounds.east) / 2.0,
                                            )
                                        )
                                        drawMapLabel(gridTextMeasurer, cell.id, labelPosition, gridLabelStyle)
                                    }
                                } else {
                                val longitudeSpan = grid.bounds.east - grid.bounds.west
                                val latitudeSpan = grid.bounds.north - grid.bounds.south
                                for (index in 0..grid.columns.size) {
                                    val longitude = grid.bounds.west + longitudeSpan * index / grid.columns.size
                                    drawLine(
                                        gridColor,
                                        geoToMapScreen(GeoPoint(grid.bounds.north, longitude)),
                                        geoToMapScreen(GeoPoint(grid.bounds.south, longitude)),
                                        strokeWidth = gridStrokeWidth,
                                    )
                                }
                                for (index in 0..grid.rows.size) {
                                    val latitude = grid.bounds.north - latitudeSpan * index / grid.rows.size
                                    drawLine(
                                        gridColor,
                                        geoToMapScreen(GeoPoint(latitude, grid.bounds.west)),
                                        geoToMapScreen(GeoPoint(latitude, grid.bounds.east)),
                                        strokeWidth = gridStrokeWidth,
                                    )
                                }
                                grid.rows.forEachIndexed { index, row ->
                                    val latitude = grid.bounds.north - latitudeSpan * (index + 0.5) / grid.rows.size
                                    val labelPosition = geoToMapScreen(GeoPoint(latitude, grid.bounds.west))
                                    drawMapLabel(gridTextMeasurer, row, Offset(labelPosition.x + 3f, labelPosition.y), gridLabelStyle)
                                }
                                grid.columns.forEachIndexed { index, column ->
                                    val longitude = grid.bounds.west + longitudeSpan * (index + 0.5) / grid.columns.size
                                    val labelPosition = geoToMapScreen(GeoPoint(grid.bounds.north, longitude))
                                    drawMapLabel(gridTextMeasurer, column, Offset(labelPosition.x, labelPosition.y + 18f), gridLabelStyle)
                                }
                                }
                            }
                            if (overlay.fieldBoundary.size >= 2) {
                                val path = Path()
                                overlay.fieldBoundary.map(::geoToMapScreen).forEachIndexed { index, point ->
                                    if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                                }
                                // Dark halo plus bright line keeps the perimeter
                                // visible over contours and organization paths.
                                drawPath(path, PipBlack.copy(alpha = 0.92f), style = Stroke(8f))
                                drawPath(path, PipGreenBright, style = Stroke(5f))
                            }
                            overlay.internalPaths.forEach { internalPath ->
                                val path = Path()
                                internalPath.points.map(::geoToMapScreen).forEachIndexed { index, point ->
                                    if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
                                }
                                // Blue identifies organization paths and the dark halo
                                // prevents them from disappearing over contours.
                                drawPath(path, PipBlack.copy(alpha = 0.72f), style = Stroke(7f))
                                drawPath(path, PipBlue, style = Stroke(4.5f))
                            }
                            overlay.pois.forEach { poi ->
                                val point = geoToMapScreen(poi.point)
                                // Keep the official red, but isolate each POI from
                                // green contours and blue organization paths.
                                drawCircle(PipBlack.copy(alpha = 0.92f), 13.5f, point)
                                drawCircle(PipRed, 10f, point, style = Stroke(3.5f))
                                drawCircle(PipRed, 4f, point)
                                drawMapLabel(
                                    gridTextMeasurer,
                                    poi.name,
                                    Offset(point.x + 12f, point.y + 4f),
                                    organizationPoiLabelStyle,
                                )
                            }
                        }
                        overlays.radZones.forEach { zone ->
                            val path = Path(); zone.vertices.map(::geoToMapScreen).forEachIndexed { i, p -> if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y) }; path.close()
                            drawPath(path, PipRed.copy(alpha = 0.24f)); drawPath(path, PipRed, style = Stroke(3f))
                        }
                        if (draftZone.isNotEmpty()) {
                            val path = Path(); draftZone.map(::geoToMapScreen).forEachIndexed { i, p -> if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y) }
                            drawPath(path, PipAmber, style = Stroke(3f)); draftZone.forEach { drawCircle(PipAmber, 6f, geoToMapScreen(it)) }
                        }
                        overlays.respawns.forEach { respawn ->
                            val p = geoToMapScreen(respawn.point)
                            drawCircle(PipGreen, 11f, p, style = Stroke(3f))
                            drawLine(PipGreen, p - Offset(16f,0f), p + Offset(16f,0f), 2f)
                            drawLine(PipGreen, p - Offset(0f,16f), p + Offset(0f,16f), 2f)
                        }
                        waypointDraft?.let { point ->
                            val p = geoToMapScreen(point)
                            drawCircle(PipAmber, 14f, p, style = Stroke(3f))
                            drawLine(PipAmber, p - Offset(18f, 0f), p + Offset(18f, 0f), 2f)
                            drawLine(PipAmber, p - Offset(0f, 18f), p + Offset(0f, 18f), 2f)
                        }
                        activeDestination?.let { destination ->
                            val p = geoToMapScreen(destination.point)
                            val color = if (destination.source == DestinationSource.USER_WAYPOINT) PipBlue else PipAmber
                            drawCircle(color, 16f, p, style = Stroke(3f))
                            drawCircle(color, 4f, p)
                        }
                        fix?.let { drawUserLocationMarker(geoToMapScreen(it.point)) }
                    }
                }
            }
        }

        Column(
            Modifier
                .weight(0.25f)
                .fillMaxHeight()
                .background(PipBlack)
                .border(1.dp, PipGreenDim)
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            activeDestination?.let { destination ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, PipAmber)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("NAVIGATION", color = PipAmber, fontSize = 15.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    Text(destination.name, color = PipGreenBright, fontSize = 15.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    navigationReading?.let { reading ->
                        NavigationArrow(reading.relativeBearingDegrees)
                        Text(formatNavigationDistance(reading.distanceMeters), color = PipGreenBright, fontSize = 18.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                        Text("GRID NOW ${reading.currentGrid ?: "--"}", color = PipNeutral, fontSize = 12.sp, fontFamily = MapTextFont)
                        Text("GRID TO ${reading.destinationGrid ?: "--"}", color = PipNeutral, fontSize = 12.sp, fontFamily = MapTextFont)
                    } ?: Text("WAITING GPS", color = PipNeutral, fontSize = 12.sp, fontFamily = MapTextFont)
                }
            }
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("MAP - TERRAIN", color=PipGreenBright, fontSize=19.sp, fontFamily=MapTextFont, fontWeight = FontWeight.Bold)
                if (navigationPrompt == NavigationPrompt.CONFIRM_WAYPOINT && waypointDraft != null) {
                    Text("SET WAYPOINT?", color = PipAmber, fontSize = 13.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    TerrainAction("CONFIRM", modifier = Modifier.fillMaxWidth()) {
                        val point = waypointDraft
                        if (point != null) {
                            activeDestination = MapDestination("waypoint", "WAYPOINT", point, DestinationSource.USER_WAYPOINT)
                            waypointDraft = null
                            navigationPrompt = NavigationPrompt.NONE
                        }
                    }
                    TerrainAction("CANCEL", modifier = Modifier.fillMaxWidth()) {
                        waypointDraft = null
                        navigationPrompt = NavigationPrompt.NONE
                    }
                }
                Text("FIELD", color=PipGreenBright, fontSize=13.sp, fontFamily=MapTextFont, fontWeight = FontWeight.Bold)
                Box(
                    Modifier
                        .fillMaxWidth()
                        .border(1.dp, PipGreen)
                        .clickable { fieldMenuExpanded = !fieldMenuExpanded }
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                ) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            if (hasSelectedMap) definition.name else TerrainFieldSelection.CHOOSE_LOCATION_LABEL,
                            color = PipGreen,
                            fontSize = 14.sp,
                            fontFamily = MapTextFont,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(if (fieldMenuExpanded) "^" else "v", color=PipGreenBright, fontSize=16.sp, fontFamily=MapTextFont, fontWeight = FontWeight.Bold)
                    }
                }
                if (fieldMenuExpanded) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .border(1.dp, PipGreenDim)
                            .padding(vertical = 2.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        TerrainAction(
                            text = "> ${TerrainFieldSelection.CHOOSE_LOCATION_LABEL}",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                selectedMapId = TerrainFieldSelection.CHOOSE_LOCATION_ID
                                fieldMenuExpanded = false
                            }
                        )
                        mapOptions.forEach { option ->
                            TerrainAction(
                                text = "> ${option.name}",
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    selectedMapId = option.mapId
                                    fieldMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                TerrainAction(
                    text = "COMPASS: ${if (compassMode == TerrainCompassMode.GYRO) "GYRO" else "NORTH UP"}",
                    enabled = hasSelectedMap,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    compassMode = if (compassMode == TerrainCompassMode.GYRO) {
                        TerrainCompassMode.NORTH_UP
                    } else {
                        TerrainCompassMode.GYRO
                    }
                }
                TerrainAction("ADD RESPAWN", hasSelectedMap && editMode == TerrainEditMode.NONE, Modifier.fillMaxWidth()) { editMode = TerrainEditMode.ADD_RESPAWN; selection = MapSelection.None }
                TerrainAction("ADD RAD ZONE", hasSelectedMap && editMode == TerrainEditMode.NONE, Modifier.fillMaxWidth()) { editMode = TerrainEditMode.ADD_RAD_ZONE; draftZone = emptyList(); selection = MapSelection.None }
                if (editMode == TerrainEditMode.ADD_RAD_ZONE) TerrainAction("FINISH", draftZone.size >= 3, Modifier.fillMaxWidth()) { overlays = overlays.copy(radZones = overlays.radZones + RadZone(UUID.randomUUID().toString(), draftZone)); draftZone=emptyList(); editMode=TerrainEditMode.NONE }
                if (editMode != TerrainEditMode.NONE) TerrainAction("CANCEL", modifier = Modifier.fillMaxWidth()) { editMode=TerrainEditMode.NONE; draftZone=emptyList() }
                visibleOrganizationOverlay?.let { overlay ->
                    Text("ORG OVERLAY // ${overlay.sourceStatus}", color = PipAmber, fontSize = 11.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    if (overlay.pois.isNotEmpty()) {
                        Text("ORGANIZATION POI", color = PipGreenBright, fontSize = 12.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                        overlay.pois.forEach { poi ->
                            TerrainAction(
                                text = "> ${poi.name}",
                                modifier = Modifier.fillMaxWidth(),
                                textColor = PipRed,
                                onClick = { selection = MapSelection.OrganizationPoiSelected(poi.id) },
                            )
                        }
                    }
                }
                when (val current = selection) {
                    is MapSelection.RespawnSelected -> TerrainAction("DELETE", modifier = Modifier.fillMaxWidth()) { selection = MapSelection.DeleteRespawnConfirm(current.id) }
                    MapSelection.WaypointSelected -> {
                        Text("WAYPOINT", color = PipGreenBright, fontSize = 14.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                        TerrainAction("CLEAR WAYPOINT", modifier = Modifier.fillMaxWidth()) {
                            navigationPrompt = NavigationPrompt.CONFIRM_CLEAR_WAYPOINT
                        }
                    }
                    is MapSelection.ZoneSelected -> TerrainAction("CLEAR", modifier = Modifier.fillMaxWidth()) { selection = MapSelection.ClearZoneConfirm(current.id) }
                    MapSelection.EmptyOffered -> TerrainAction("EMPTY MAP", modifier = Modifier.fillMaxWidth()) { selection = MapSelection.EmptyConfirm }
                    else -> Unit
                }
                if (selection is MapSelection.DeleteRespawnConfirm || selection is MapSelection.ClearZoneConfirm || selection == MapSelection.EmptyConfirm) {
                    TerrainAction("CONFIRM", modifier=Modifier.fillMaxWidth()) {
                        overlays = when (val current=selection) {
                            is MapSelection.DeleteRespawnConfirm -> overlays.copy(respawns=overlays.respawns.filterNot { it.id==current.id })
                            is MapSelection.ClearZoneConfirm -> overlays.copy(radZones=overlays.radZones.filterNot { it.id==current.id })
                            MapSelection.EmptyConfirm -> MapOverlays()
                            else -> overlays
                        }
                        selection=MapSelection.None
                    }
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                visibleOrganizationOverlay
                    ?.pois
                    ?.firstOrNull { it.id == (selection as? MapSelection.OrganizationPoiSelected)?.id }
                    ?.takeIf { activeDestination?.id != it.id || activeDestination?.source != DestinationSource.ORGANIZATION_POI }
                    ?.let { poi ->
                        Text(poi.name, color = PipRed, fontSize = 17.5.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                        if (activeDestination?.id == poi.id && activeDestination?.source == DestinationSource.ORGANIZATION_POI) {
                            TerrainAction("STOP NAVIGATION", modifier = Modifier.fillMaxWidth()) {
                                navigationPrompt = NavigationPrompt.CONFIRM_STOP
                            }
                        } else {
                            TerrainAction("NAVIGATE", modifier = Modifier.fillMaxWidth()) {
                                activeDestination = MapDestination(
                                    id = poi.id,
                                    name = poi.name,
                                    point = poi.point,
                                    source = DestinationSource.ORGANIZATION_POI,
                                )
                                navigationPrompt = NavigationPrompt.NONE
                            }
                        }
                    }
                if (navigationPrompt == NavigationPrompt.CONFIRM_CLEAR_WAYPOINT) {
                    Text("CLEAR WAYPOINT?", color = PipAmber, fontSize = 13.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    TerrainAction("CONFIRM", modifier = Modifier.fillMaxWidth()) {
                        activeDestination = null
                        waypointDraft = null
                        navigationPrompt = NavigationPrompt.NONE
                        selection = MapSelection.None
                    }
                    TerrainAction("CANCEL", modifier = Modifier.fillMaxWidth()) { navigationPrompt = NavigationPrompt.NONE }
                }
                if (navigationPrompt == NavigationPrompt.CONFIRM_STOP) {
                    Text("STOP NAVIGATION?", color = PipAmber, fontSize = 13.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
                    TerrainAction("CONFIRM", modifier = Modifier.fillMaxWidth()) {
                        activeDestination = null
                        navigationPrompt = NavigationPrompt.NONE
                        selection = MapSelection.None
                    }
                    TerrainAction("CANCEL", modifier = Modifier.fillMaxWidth()) { navigationPrompt = NavigationPrompt.NONE }
                }
                Text(loadError ?: "$locationStatus  $headingStatus  Z${"%.1f".format(zoom)}", color=if(loadError==null) PipGreenDim else PipRed, fontSize=12.sp, fontFamily=MapTextFont)
                if (hasSelectedMap) Text("TAP BLUE POINT // RECENTER", color = PipBlue, fontSize = 11.sp, fontFamily = MapTextFont)
                if (hasSelectedMap) Text("LONG PRESS // SET WAYPOINT", color = PipGreenBright, fontSize = 11.sp, fontFamily = MapTextFont)
                TerrainAction(
                    "< BACK",
                    editMode == TerrainEditMode.NONE,
                    Modifier.fillMaxWidth(),
                    onClick = onBack,
                )
            }
        }
    }
}

@Composable
@Suppress("ModifierParameter")
private fun TerrainAction(
    text: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    textColor: Color? = null,
    onClick: () -> Unit,
) {
    Text(
        text,
        color = if (enabled) textColor ?: PipGreenBright else PipGreenDim,
        fontSize = 15.sp,
        fontFamily = MapTextFont,
        fontWeight = if (textColor != null) FontWeight.Bold else FontWeight.Medium,
        modifier=if(enabled) modifier.background(PipBlack.copy(alpha=.82f)).padding(6.dp).clickable(onClick=onClick) else modifier.padding(6.dp))
}

private fun formatNavigationDistance(distanceMeters: Double): String = when {
    distanceMeters >= 1000.0 -> "~${"%.1f".format(distanceMeters / 1000.0)} km"
    else -> "~${distanceMeters.roundToInt()} m"
}

@Composable
private fun NavigationArrow(relativeBearingDegrees: Double?) {
    if (relativeBearingDegrees == null) {
        Text("?", color = PipAmber, fontSize = 44.sp, fontFamily = MapTextFont, fontWeight = FontWeight.Bold)
        return
    }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val length = minOf(size.width, size.height) * 0.36f
        val tip = Offset(center.x, center.y - length)
        val shaftEnd = Offset(center.x, center.y + length * 0.58f)
        val headBase = center.y - length * 0.10f
        val headWidth = length * 0.46f

        // Positive relative bearing is to the right. Canvas rotation is
        // continuous, so the arrow follows every heading degree rather than
        // jumping between eight text glyphs.
        rotate(relativeBearingDegrees.toFloat(), center) {
            drawLine(PipAmber, shaftEnd, tip, strokeWidth = 6f)
            drawLine(PipAmber, tip, Offset(center.x - headWidth, headBase), strokeWidth = 6f)
            drawLine(PipAmber, tip, Offset(center.x + headWidth, headBase), strokeWidth = 6f)
            drawCircle(PipAmber, 3.5f, center)
        }
    }
}

private fun visibleTileKeys(
    data: MbTilesData,
    transform: TerrainViewportTransform,
    tileZoom: Int,
    canvasSize: IntSize
): Set<TileKey> {
    val corners = listOf(
        Offset(0f, 0f),
        Offset(canvasSize.width.toFloat(), 0f),
        Offset(0f, canvasSize.height.toFloat()),
        Offset(canvasSize.width.toFloat(), canvasSize.height.toFloat())
    )
    val world = corners.map { screen ->
        WebMercator.toWorldPixel(transform.screenToGeo(screen.x, screen.y), tileZoom)
    }
    val minX = floor(world.minOf { it.x } / 256.0).toInt() - 1
    val maxX = floor(world.maxOf { it.x } / 256.0).toInt() + 1
    val minY = floor(world.minOf { it.y } / 256.0).toInt() - 1
    val maxY = floor(world.maxOf { it.y } / 256.0).toInt() + 1
    return data.tileKeys.filterTo(linkedSetOf()) { key ->
        key.zoom == tileZoom && key.x in minX..maxX && key.xyzY in minY..maxY
    }
}
