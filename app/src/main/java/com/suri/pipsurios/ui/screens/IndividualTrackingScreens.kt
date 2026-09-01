package com.suri.pipsurios.ui.screens

import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.individualtracking.IndividualTrackingSelection
import com.suri.pipsurios.individualtracking.IndividualTrackingTarget
import com.suri.pipsurios.prs.BleScanStatus
import com.suri.pipsurios.prs.BleScanner
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsContactTracker
import com.suri.pipsurios.prs.PrsDeviceRegistry
import com.suri.pipsurios.prs.PrsProximityBand
import com.suri.pipsurios.prs.PrsSavedDevice
import com.suri.pipsurios.prs.PrsSnapshot
import com.suri.pipsurios.prs.PrsTrend
import com.suri.pipsurios.terrain.GeoPoint
import com.suri.pipsurios.terrain.MapOverlays
import com.suri.pipsurios.terrain.MbTilesData
import com.suri.pipsurios.terrain.MbTilesRepository
import com.suri.pipsurios.terrain.OfflineMapCatalog
import com.suri.pipsurios.terrain.TerrainHeading
import com.suri.pipsurios.terrain.TerrainLocation
import com.suri.pipsurios.terrain.TerrainLocationFix
import com.suri.pipsurios.terrain.TerrainOverlayRepository
import com.suri.pipsurios.terrain.TerrainTileCoverage
import com.suri.pipsurios.terrain.TerrainViewportTransform
import com.suri.pipsurios.terrain.TileKey
import com.suri.pipsurios.terrain.WebMercator
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipMapBackground
import com.suri.pipsurios.ui.theme.PipPanel
import com.suri.pipsurios.ui.theme.PipRed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.roundToInt

@Composable
fun IndividualTrackingMenuScreen(
    selection: IndividualTrackingSelection?,
    onTargetSelected: () -> Unit,
    onTrackerSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "INDIVIDUAL TRACKER",
            color = PipGreen,
            fontSize = 26.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            IndividualMenuAction("> TARGET", onTargetSelected)
            IndividualMenuAction("> TRACKER", onTrackerSelected)
            Text(
                text = selection?.let { "TARGET: ${it.target.displayName}" } ?: "TARGET: NOT SELECTED",
                color = if (selection == null) PipGreenDim else PipAmber,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun IndividualTrackingTargetScreen(
    onTargetSelected: (IndividualTrackingSelection) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    val tracker = remember { PrsContactTracker() }
    val registry = remember(context) { PrsDeviceRegistry.from(context.applicationContext) }
    var snapshot by remember { mutableStateOf(PrsSnapshot()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var selectedMapId by remember { mutableStateOf<String?>(null) }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var retryVersion by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) permissionLauncher.launch(prsPermissions())
    }

    DisposableEffect(permissionVersion, retryVersion) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                tracker.observe(observation)
                snapshot = tracker.snapshot()
            },
            onStatusChanged = { scanStatus = it }
        )
        onDispose {
            scanner.releaseSession()
            tracker.clear()
        }
    }

    LaunchedEffect(tracker) {
        while (true) {
            delay(com.suri.pipsurios.prs.PrsTuning.DEFAULT.evaluationIntervalMillis)
            tracker.evaluate(SystemClock.elapsedRealtime())
            snapshot = tracker.snapshot()
        }
    }

    val selectedMap = selectedMapId?.let { id -> OfflineMapCatalog.maps.firstOrNull { it.mapId == id } }
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("INDIVIDUAL TRACKER / TARGET", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            if (selectedMap == null) {
                Text("STEP 1 // SELECT TERRAIN FIELD", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text("The field is selected before the BLE target.", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OfflineMapCatalog.maps.forEach { map ->
                        IndividualMenuAction("> ${map.name}", { selectedMapId = map.mapId })
                    }
                }
            } else {
                Text("FIELD: ${selectedMap.name}", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text("STEP 2 // SELECT DETECTED TARGET", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text(
                    "KNOWN DEVICES: ${registry.snapshot().size}  //  P.R.S. RULES REUSED",
                    color = PipGreenDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    if (snapshot.contacts.isEmpty()) {
                        Text(
                            "${scanStatusLabel(scanStatus)} // WAITING FOR BLE ADVERTISEMENTS...",
                            color = individualScanStatusColor(scanStatus),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        snapshot.contacts.forEach { contact ->
                            val knownRule = registry.savedDeviceFor(contact.measured)
                            val omitted = registry.isIgnored(contact.measured)
                            IndividualTargetRow(
                                contact = contact,
                                knownRule = knownRule,
                                omittedByPrs = omitted,
                                onClick = {
                                    onTargetSelected(
                                        IndividualTrackingSelection(
                                            mapId = selectedMap.mapId,
                                            target = IndividualTrackingTarget(
                                                contactId = contact.contactId,
                                                deviceIdentifier = contact.measured.deviceIdentifier,
                                                displayName = knownRule?.displayName
                                                    ?: contact.displayName,
                                                source = contact.source,
                                                knownRule = knownRule
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
                IndividualMenuAction("> CHANGE FIELD", { selectedMapId = null })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (scanStatus == BleScanStatus.PERMISSION_REQUIRED) {
                    IndividualMenuAction("> ALLOW BLUETOOTH", { permissionLauncher.launch(prsPermissions()) }, Modifier.weight(1f))
                } else if (scanStatus == BleScanStatus.BLUETOOTH_OFF || scanStatus == BleScanStatus.ERROR) {
                    IndividualMenuAction("> TRY AGAIN", { retryVersion++ }, Modifier.weight(1f))
                }
                IndividualMenuAction("< BACK", onBack, Modifier.weight(1f))
            }
        }
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
    }
}

@Composable
fun IndividualTrackingTrackerScreen(
    selection: IndividualTrackingSelection?,
    onSelectTarget: () -> Unit,
    onBack: () -> Unit
) {
    if (selection == null) {
        Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("INDIVIDUAL TRACKER", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
                Text("TARGET NOT SELECTED", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                IndividualMenuAction("> OPEN TARGET", onSelectTarget)
            }
            PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(24.dp))
        }
        return
    }
    IndividualTrackerMapContent(selection = selection, onBack = onBack)
}

@Composable
private fun IndividualTrackerMapContent(
    selection: IndividualTrackingSelection,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val definition = OfflineMapCatalog.maps.firstOrNull { it.mapId == selection.mapId }
    if (definition == null) {
        Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
            Text("TERRAIN FIELD NOT FOUND", color = PipRed, fontSize = 18.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.Center))
            PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(24.dp))
        }
        return
    }

    val scanner = remember(selection) { BleScanner(context.applicationContext) }
    val tracker = remember(selection) { PrsContactTracker() }
    val locationSource = remember(selection) { TerrainLocation(context.applicationContext) }
    val headingSource = remember(selection) { TerrainHeading(context.applicationContext) }
    val overlayRepository = remember(selection) { TerrainOverlayRepository.from(context.applicationContext) }
    var snapshot by remember(selection) { mutableStateOf(PrsSnapshot()) }
    var scanStatus by remember(selection) { mutableStateOf(BleScanStatus.IDLE) }
    var fix by remember(selection) { mutableStateOf<TerrainLocationFix?>(null) }
    var locationStatus by remember(selection) { mutableStateOf("WAITING GPS") }
    var heading by remember(selection) { mutableFloatStateOf(0f) }
    var headingStatus by remember(selection) { mutableStateOf("HEADING WAIT") }
    var permissionVersion by remember(selection) { mutableIntStateOf(0) }
    var retryVersion by remember(selection) { mutableIntStateOf(0) }
    var mapData by remember(selection) { mutableStateOf<MbTilesData?>(null) }
    var loadedTiles by remember(selection) { mutableStateOf<Map<TileKey, ImageBitmap>>(emptyMap()) }
    var overlays by remember(selection) { mutableStateOf(MapOverlays()) }
    var loadError by remember(selection) { mutableStateOf<String?>(null) }
    var center by remember(selection) { mutableStateOf(definition.bounds.center) }
    var zoom by remember(selection) { mutableFloatStateOf(17.5f) }
    var canvasSize by remember(selection) { mutableStateOf(IntSize.Zero) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions() || !locationSource.hasPermission()) {
            permissionLauncher.launch(prsPermissions())
        }
    }

    DisposableEffect(permissionVersion, retryVersion) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                if (selection.target.matches(observation)) {
                    tracker.observe(observation)
                    snapshot = tracker.snapshot()
                }
            },
            onStatusChanged = { scanStatus = it }
        )
        if (locationSource.hasPermission()) {
            locationStatus = "WAITING GPS"
            locationSource.start(
                onFix = {
                    fix = it
                    locationStatus = if (definition.bounds.contains(it.point)) "GPS ACTIVE // IN FIELD" else "GPS ACTIVE // OUTSIDE FIELD"
                },
                onUnavailable = { locationStatus = "GPS UNAVAILABLE" }
            )
        } else {
            locationStatus = "LOCATION PERMISSION REQUIRED"
        }
        headingSource.start(
            onHeading = {
                heading = it
                headingStatus = "HEADING ACTIVE"
            },
            onUnavailable = { headingStatus = "HEADING UNAVAILABLE" }
        )
        onDispose {
            scanner.releaseSession()
            tracker.clear()
            locationSource.stop()
            headingSource.stop()
        }
    }

    LaunchedEffect(tracker) {
        while (true) {
            delay(com.suri.pipsurios.prs.PrsTuning.DEFAULT.evaluationIntervalMillis)
            tracker.evaluate(SystemClock.elapsedRealtime())
            snapshot = tracker.snapshot()
        }
    }

    LaunchedEffect(definition) {
        mapData = null
        loadedTiles = emptyMap()
        loadError = null
        overlays = withContext(Dispatchers.IO) { overlayRepository.load(definition.mapId) }
        runCatching {
            withContext(Dispatchers.IO) { MbTilesRepository(context.applicationContext).load(definition) }
        }.onSuccess { mapData = it }
            .onFailure { loadError = it.message ?: "MAP LOAD FAILED" }
    }

    DisposableEffect(mapData) {
        val current = mapData
        onDispose { current?.close() }
    }

    val tileCoverage = remember(mapData) {
        mapData?.let { TerrainTileCoverage.from(it.tileKeys, definition.maxNativeZoom) }
    }
    LaunchedEffect(tileCoverage, canvasSize) {
        val coverage = tileCoverage ?: return@LaunchedEffect
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@LaunchedEffect
        zoom = maxOf(
            zoom,
            coverage.minimumDisplayZoom(
                canvasSize.width,
                canvasSize.height,
                definition.minZoom.toFloat(),
                definition.maxDisplayZoom.toFloat()
            )
        )
        center = coverage.clampCenterForFullRotation(center, zoom, canvasSize.width, canvasSize.height)
    }
    LaunchedEffect(fix, tileCoverage, canvasSize, zoom) {
        val point = fix?.point?.takeIf(definition.bounds::contains) ?: return@LaunchedEffect
        center = tileCoverage?.clampCenterForFullRotation(point, zoom, canvasSize.width, canvasSize.height) ?: point
    }
    LaunchedEffect(mapData, center, zoom, heading, canvasSize) {
        val data = mapData ?: return@LaunchedEffect
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@LaunchedEffect
        val tileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
        val transform = TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, heading)
        val requested = individualVisibleTileKeys(data, transform, tileZoom, canvasSize)
        val missing = requested.filterNot(loadedTiles::containsKey)
        if (missing.isNotEmpty()) {
            val loaded = withContext(Dispatchers.IO) {
                missing.mapNotNull { key -> data.loadTile(key)?.let { key to it } }.toMap()
            }
            loadedTiles = loadedTiles.filterKeys { it in requested } + loaded
        } else {
            loadedTiles = loadedTiles.filterKeys { it in requested }
        }
    }

    val selectedContact = snapshot.contacts.firstOrNull()
    val panelScroll = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).fillMaxHeight().onSizeChanged { canvasSize = it }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(PipMapBackground)
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        val transform = TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, heading)
                        clipRect {
                            rotate(-heading, Offset(transform.pivotX, transform.pivotY)) {
                                val tileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
                                val scale = 2.0.pow(zoom.toDouble() - tileZoom).toFloat()
                                val centerPixel = WebMercator.toWorldPixel(center, tileZoom)
                                loadedTiles.filterKeys { it.zoom == tileZoom }.forEach { (key, image) ->
                                    val x = (size.width / 2 + (key.x * 256.0 - centerPixel.x) * scale).roundToInt()
                                    val y = (size.height / 2 + (key.xyzY * 256.0 - centerPixel.y) * scale).roundToInt()
                                    drawImage(
                                        image,
                                        dstOffset = IntOffset(x, y),
                                        dstSize = IntSize(ceil(256 * scale).toInt(), ceil(256 * scale).toInt())
                                    )
                                }
                                overlays.radZones.forEach { zone ->
                                    val path = Path()
                                    zone.vertices.map { point -> transform.geoToMapScreen(point) }.forEachIndexed { index, point ->
                                        if (index == 0) path.moveTo(point.first, point.second) else path.lineTo(point.first, point.second)
                                    }
                                    path.close()
                                    drawPath(path, PipRed.copy(alpha = 0.24f))
                                    drawPath(path, PipRed, style = Stroke(3f))
                                }
                                overlays.respawns.forEach { respawn ->
                                    val point = transform.geoToMapScreen(respawn.point)
                                    drawCircle(PipGreen, 10f, Offset(point.first, point.second), style = Stroke(3f))
                                    drawLine(PipGreen, Offset(point.first - 14f, point.second), Offset(point.first + 14f, point.second), 2f)
                                    drawLine(PipGreen, Offset(point.first, point.second - 14f), Offset(point.first, point.second + 14f), 2f)
                                }
                                fix?.let {
                                    val point = transform.geoToMapScreen(it.point)
                                    drawCircle(
                                        if (definition.bounds.contains(it.point)) PipAmber else PipRed,
                                        9f,
                                        Offset(point.first, point.second)
                                    )
                                }
                            }
                        }
                    }
                }
                PrsDensityGrid(
                    contacts = selectedContact?.let(::listOf) ?: emptyList(),
                    selectedContactId = selectedContact?.contactId,
                    selectedDisplayName = selection.target.displayName,
                    modifier = Modifier.fillMaxSize().padding(2.dp),
                    surfaceColor = Color.Transparent,
                    showEmblem = false,
                    showTargetLabel = false
                )
            }

            Column(
                modifier = Modifier
                    .width(320.dp)
                    .fillMaxHeight()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.55f))
                    .background(PipPanel)
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("INDIVIDUAL TRACKER", color = PipGreen, fontSize = 19.sp, fontFamily = FontFamily.Monospace)
                Text("FIELD: ${definition.name}", color = PipAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("GRID: TARGET ONLY", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text("CENTER: A56 // GPS FOLLOW", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text("AZIMUTH: UNAVAILABLE", color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(panelScroll),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text("TARGET", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text(selection.target.displayName + selection.target.knownRule.categoryLabel(), color = PipGreen, fontSize = 15.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("ID: ${selection.target.deviceIdentifier}", color = PipGreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("SOURCE: ${selection.target.source.displayName}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("P.R.S.: ${scanStatusLabel(scanStatus)}", color = individualScanStatusColor(scanStatus), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("GPS: $locationStatus", color = if (locationStatus.contains("ACTIVE")) PipGreen else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("${headingStatus} // ${formatSignal(heading)}°", color = if (headingStatus.contains("ACTIVE")) PipGreenDim else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    if (selectedContact == null) {
                        Text("WAITING FOR SELECTED TARGET...", color = PipAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        IndividualTargetDetails(selectedContact)
                    }
                    Text("SIGNAL MODEL: RELATIVE / EXPERIMENTAL", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("No target coordinate or RSSI-to-metre conversion.", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    loadError?.let { Text(it, color = PipRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace) }
                }
                IndividualMenuAction("< BACK", onBack, Modifier.fillMaxWidth())
            }
        }
        PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(3.dp))
    }
}

@Composable
private fun IndividualTargetDetails(contact: PrsContactSnapshot) {
    Text("RAW RSSI: ${contact.measured.rssi} dBm", color = PipGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("SMOOTH RSSI: ${formatSignal(contact.processed.smoothedRssi)} dBm", color = PipGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("TREND: ${contact.inference.trend.displayLabel()}", color = trendColor(contact.inference.trend), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("BAND: ${contact.inference.proximity.displayLabel()}", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("SAMPLES: ${contact.sampleCount} // CONF: ${formatSignal(contact.inference.densityCloud.confidence)}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
}

@Composable
private fun IndividualTargetRow(
    contact: PrsContactSnapshot,
    knownRule: PrsSavedDevice?,
    omittedByPrs: Boolean,
    onClick: () -> Unit
) {
    val enabled = !omittedByPrs
    val color = if (enabled) PipGreen else PipGreenDim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.6f))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = knownRule?.displayName ?: contact.displayName,
            color = color,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (knownRule != null) {
                "KNOWN ${knownRule.type.label} // ${contact.source.displayName} // RSSI ${contact.measured.rssi}"
            } else {
                "UNREGISTERED // ${contact.source.displayName} // RSSI ${contact.measured.rssi}"
            },
            color = PipGreenDim,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (omittedByPrs) {
            Text("OMITTED BY P.R.S. DEVICES // DISABLE RULE TO SELECT", color = PipAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun IndividualMenuAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = PipGreen,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier.clickable(onClick = onClick).padding(horizontal = 4.dp, vertical = 4.dp)
    )
}

private fun PrsSavedDevice?.categoryLabel(): String = this?.displayName?.let { " // KNOWN" } ?: ""

private fun PrsTrend.displayLabel(): String = when (this) {
    PrsTrend.APPROACHING -> "APPROACHING"
    PrsTrend.MOVING_AWAY -> "MOVING AWAY"
    PrsTrend.STABLE -> "STABLE"
    PrsTrend.INSUFFICIENT_DATA -> "WAITING"
}

private fun PrsProximityBand.displayLabel(): String = when (this) {
    PrsProximityBand.UNKNOWN -> "UNKNOWN"
    PrsProximityBand.NEAR -> "NEAR"
    PrsProximityBand.MEDIUM -> "MEDIUM"
    PrsProximityBand.FAR -> "FAR"
}

private fun scanStatusLabel(status: BleScanStatus): String = status.name.replace('_', ' ')

private fun individualScanStatusColor(status: BleScanStatus): Color = when (status) {
    BleScanStatus.SCANNING -> PipGreen
    BleScanStatus.PERMISSION_REQUIRED,
    BleScanStatus.BLUETOOTH_OFF,
    BleScanStatus.ERROR -> PipAmber
    else -> PipGreenDim
}

private fun trendColor(trend: PrsTrend): Color = when (trend) {
    PrsTrend.APPROACHING -> PipGreen
    PrsTrend.MOVING_AWAY -> PipAmber
    PrsTrend.STABLE -> PipGreenDim
    PrsTrend.INSUFFICIENT_DATA -> PipAmber
}

private fun formatSignal(value: Number): String = when (value) {
    is Float -> "%.1f".format(Locale.US, value)
    is Double -> "%.1f".format(Locale.US, value)
    else -> value.toString()
}

private fun individualVisibleTileKeys(
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
