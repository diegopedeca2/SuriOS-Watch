package com.suri.pipsurios.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.geiger.ClickScheduler
import com.suri.pipsurios.terrain.*
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.ui.theme.PipMapBackground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.ceil
import kotlin.math.log2
import kotlin.math.pow
import kotlin.math.roundToInt
import java.util.UUID

private enum class TerrainEditMode { NONE, ADD_RESPAWN, ADD_RAD_ZONE }
private enum class TerrainCompassMode { GYRO, NORTH_UP }

@Composable
fun MapTerrainScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val mapOptions = OfflineMapCatalog.maps
    var selectedMapId by remember { mutableStateOf(OfflineMapCatalog.NAVY7.mapId) }
    var fieldMenuExpanded by remember { mutableStateOf(false) }
    val selectedDefinition = mapOptions.firstOrNull { it.mapId == selectedMapId }
    val definition = selectedDefinition ?: mapOptions.first()
    val hasSelectedMap = selectedDefinition != null
    val overlayRepository = remember { TerrainOverlayRepository.from(context.applicationContext) }
    val locationSource = remember { TerrainLocation(context.applicationContext) }
    val headingSource = remember { TerrainHeading(context.applicationContext) }
    val clickScheduler = remember { ClickScheduler(context.applicationContext) }
    var mapData by remember { mutableStateOf<MbTilesData?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }
    var overlays by remember { mutableStateOf(overlayRepository.load(definition.mapId)) }
    var overlaysMapId by remember { mutableStateOf<String?>(definition.mapId) }
    var selection by remember { mutableStateOf<MapSelection>(MapSelection.None) }
    var editMode by remember { mutableStateOf(TerrainEditMode.NONE) }
    var draftZone by remember { mutableStateOf(emptyList<GeoPoint>()) }
    var center by remember { mutableStateOf(definition.bounds.center) }
    var zoom by remember { mutableFloatStateOf(17.5f) }
    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    var fix by remember { mutableStateOf<TerrainLocationFix?>(null) }
    var locationStatus by remember { mutableStateOf("WAITING GPS") }
    var geigerLevel by remember { mutableFloatStateOf(0f) }
    var heading by remember { mutableFloatStateOf(0f) }
    var headingStatus by remember { mutableStateOf("HEADING WAIT") }
    var compassMode by remember { mutableStateOf(TerrainCompassMode.GYRO) }
    val radiation = remember { TerrainRadiationController() }
    val currentGeigerLevel by rememberUpdatedState(geigerLevel)
    val effectiveHeading = if (compassMode == TerrainCompassMode.GYRO) heading else 0f
    val currentHeading by rememberUpdatedState(effectiveHeading)
    val currentCenter by rememberUpdatedState(center)
    val currentZoom by rememberUpdatedState(zoom)

    LaunchedEffect(selectedMapId) {
        mapData = null
        loadError = null
        selection = MapSelection.None
        editMode = TerrainEditMode.NONE
        draftZone = emptyList()
        center = definition.bounds.center
        zoom = 17.5f
        if (!hasSelectedMap) {
            overlaysMapId = null
            overlays = MapOverlays()
            fix = null
            geigerLevel = 0f
            heading = 0f
            locationStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
            headingStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
            return@LaunchedEffect
        }
        locationStatus = "WAITING GPS"
        headingStatus = "HEADING WAIT"
        overlays = withContext(Dispatchers.IO) { overlayRepository.load(definition.mapId) }
        overlaysMapId = definition.mapId
        runCatching { withContext(Dispatchers.IO) { MbTilesRepository(context.applicationContext).load(definition) } }
            .onSuccess { mapData = it }
            .onFailure { loadError = it.message ?: "MAP LOAD FAILED" }
    }
    val tileCoverage = remember(mapData) {
        mapData?.let { TerrainTileCoverage.from(it.tileKeys, definition.maxNativeZoom) }
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
        onDispose { locationSource.stop(); clickScheduler.release() }
    }
    DisposableEffect(headingSource, compassMode, hasSelectedMap) {
        if (!hasSelectedMap) {
            headingSource.stop()
            headingStatus = TerrainFieldSelection.CHOOSE_LOCATION_LABEL
        } else if (compassMode == TerrainCompassMode.GYRO) {
            headingSource.start(
                { heading = it; headingStatus = "HDG ${it.roundToInt()}" },
                { headingStatus = "HEADING UNAVAILABLE" }
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
        geigerLevel = radiation.update(distance?.first, distance?.second == true, current?.accuracyMeters ?: Float.MAX_VALUE)
    }
    LaunchedEffect(geigerLevel > 0.005f, hasSelectedMap) {
        if (!hasSelectedMap) {
            clickScheduler.release()
        } else if (geigerLevel > 0.005f) {
            clickScheduler.run { currentGeigerLevel }
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
    fun hitRespawn(offset: Offset) = overlays.respawns.minByOrNull { (geoToScreen(it.point) - offset).getDistance() }
        ?.takeIf { (geoToScreen(it.point) - offset).getDistance() <= 30f }
    fun hitZone(point: GeoPoint) = overlays.radZones.lastOrNull { TerrainGeometry.isInside(point, it.vertices) }

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
                    .pointerInput(editMode, overlays, center, zoom, hasSelectedMap) {
                        detectTapGestures(
                            onTap = { offset ->
                                if (hasSelectedMap) {
                                    val point = screenToGeo(offset)
                                    val respawn = hitRespawn(offset); val zone = if (respawn == null) hitZone(point) else null
                                    when {
                                        respawn != null -> selection = MapSelection.RespawnSelected(respawn.id)
                                        zone != null -> selection = MapSelection.ZoneSelected(zone.id)
                                        editMode == TerrainEditMode.ADD_RESPAWN -> {
                                            overlays = overlays.copy(respawns = overlays.respawns + Respawn(UUID.randomUUID().toString(), point)); editMode = TerrainEditMode.NONE
                                        }
                                        editMode == TerrainEditMode.ADD_RAD_ZONE -> draftZone = draftZone + point
                                        else -> selection = MapSelection.None
                                    }
                                }
                            },
                            onDoubleTap = { offset ->
                                if (hasSelectedMap) {
                                    val point = screenToGeo(offset)
                                    if (hitRespawn(offset) == null && hitZone(point) == null && editMode == TerrainEditMode.NONE) selection = MapSelection.EmptyOffered
                                }
                            }
                        )
                    }
            ) {
                drawRect(PipMapBackground)
                val transform = viewportTransform()
                clipRect {
                    rotate(-currentHeading, Offset(transform.pivotX, transform.pivotY)) {
                        val data = mapData
                        if (data != null) {
                            val tileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
                            val scale = 2.0.pow(zoom.toDouble() - tileZoom).toFloat()
                            val centerPixel = WebMercator.toWorldPixel(center, tileZoom)
                            data.tileKeys.filter { it.zoom == tileZoom }.forEach { key ->
                                data.loadTile(key)?.let { image ->
                                    val x = (size.width / 2 + (key.x * 256.0 - centerPixel.x) * scale).roundToInt()
                                    val y = (size.height / 2 + (key.xyzY * 256.0 - centerPixel.y) * scale).roundToInt()
                                    drawImage(image, dstOffset = IntOffset(x, y), dstSize = IntSize(ceil(256 * scale).toInt(), ceil(256 * scale).toInt()))
                                }
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
                        fix?.let { drawCircle(if (definition.bounds.contains(it.point)) PipAmber else PipRed, 9f, geoToMapScreen(it.point)); drawCircle(PipAmber.copy(alpha=.5f), 18f, geoToMapScreen(it.point), style=Stroke(2f)) }
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
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("MAP - TERRAIN", color=PipGreen, fontSize=18.sp, fontFamily=FontFamily.Monospace)
                Text("FIELD", color=PipGreenDim, fontSize=12.sp, fontFamily=FontFamily.Monospace)
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
                            fontFamily = FontFamily.Monospace
                        )
                        Text(if (fieldMenuExpanded) "^" else "v", color=PipGreen, fontSize=14.sp, fontFamily=FontFamily.Monospace)
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
                when (val current = selection) {
                    is MapSelection.RespawnSelected -> TerrainAction("DELETE", modifier = Modifier.fillMaxWidth()) { selection = MapSelection.DeleteRespawnConfirm(current.id) }
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
                Text(loadError ?: "$locationStatus  $headingStatus  Z${"%.1f".format(zoom)}", color=if(loadError==null) PipGreenDim else PipRed, fontSize=11.sp, fontFamily=FontFamily.Monospace)
                TerrainAction("< BACK", editMode == TerrainEditMode.NONE, Modifier.fillMaxWidth(), onBack)
            }
        }
    }
}

@Composable
private fun TerrainAction(text: String, enabled: Boolean=true, modifier: Modifier=Modifier, onClick:()->Unit) {
    Text(text, color=if(enabled) PipGreen else PipGreenDim, fontSize=14.sp, fontFamily=FontFamily.Monospace,
        modifier=if(enabled) modifier.background(PipBlack.copy(alpha=.82f)).clickable(onClick=onClick).padding(6.dp) else modifier.padding(6.dp))
}
