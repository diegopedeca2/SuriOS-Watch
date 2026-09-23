package com.suri.pipsurios.ui.screens

import com.suri.pipsurios.PipSuriOsVersion
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.runtime.rememberUpdatedState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.individualtracking.IndividualTrackingSelection
import com.suri.pipsurios.individualtracking.IndividualTrackingTarget
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.prs.BleScanStatus
import com.suri.pipsurios.prs.BleScanner
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsContactTracker
import com.suri.pipsurios.prs.PrsDeviceRegistry
import com.suri.pipsurios.prs.PrsDeviceRuleType
import com.suri.pipsurios.prs.PrsProximityBand
import com.suri.pipsurios.prs.PrsReadingSound
import com.suri.pipsurios.prs.PrsSavedDevice
import com.suri.pipsurios.prs.PrsSnapshot
import com.suri.pipsurios.prs.PrsTargetAreaEstimate
import com.suri.pipsurios.prs.PrsTargetAreaEstimator
import com.suri.pipsurios.prs.PrsTrend
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.prs.PrsProbeNodeSnapshot
import com.suri.pipsurios.prs.ProbeLink
import com.suri.pipsurios.prs.ProbeTelemetryStore
import com.suri.pipsurios.prs.addressTypeLabel
import com.suri.pipsurios.terrain.GeoPoint
import com.suri.pipsurios.terrain.MapOverlays
import com.suri.pipsurios.terrain.MbTilesData
import com.suri.pipsurios.terrain.MbTilesRepository
import com.suri.pipsurios.terrain.OfflineMapCatalog
import com.suri.pipsurios.terrain.OfflineMapDefinition
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
import com.suri.probeprotocol.ProbeProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.abs
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
                text = selection?.let { "OBJETIVO: ${it.target.displayName}" } ?: "OBJETIVO: NO SELECCIONADO",
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
            text = PipSuriOsVersion,
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
    onBack: () -> Unit,
    mode: PrsOperatingMode = PrsOperatingMode.LOCAL_SCAN,
    modeLabel: String = mode.displayName,
    title: String = "INDIVIDUAL TRACKER / TARGET",
    locationStepLabel: String = "PASO 1 // SELECCIONAR CAMPO",
    targetStepLabel: String = "PASO 2 // SELECCIONAR OBJETIVO DETECTADO",
    splitLayout: Boolean = false,
    useProbabilityArea: Boolean = false
) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    val probeLink = remember(context, mode) { ProbeLink(context.applicationContext) }
    val tracker = remember { PrsContactTracker() }
    val registry = remember(context) { PrsDeviceRegistry.from(context.applicationContext) }
    var snapshot by remember { mutableStateOf(PrsSnapshot()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var probeNode by remember { mutableStateOf(PrsProbeNodeSnapshot()) }
    var probeLinkStatus by remember { mutableStateOf(if (mode.probeEnabled) "INICIANDO" else "NO USADO") }
    var selectedMapId by remember { mutableStateOf<String?>(null) }
    var targetQuery by remember { mutableStateOf("") }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var retryVersion by remember { mutableIntStateOf(0) }
    val sessionId = remember(mode) { "PRS-V4-TARGET-${System.currentTimeMillis()}" }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) permissionLauncher.launch(prsPermissions())
    }

    DisposableEffect(permissionVersion, retryVersion, mode) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                tracker.observe(observation)
                snapshot = tracker.snapshot()
            },
            onStatusChanged = { scanStatus = it }
        )
        if (mode.probeEnabled) {
            probeLinkStatus = "INICIANDO"
            probeLink.send(mode.command!!, sessionId) { success, detail ->
                probeLinkStatus = if (success) "COMANDO ENVIADO // $detail" else "ERROR // $detail"
            }
        }
        onDispose {
            scanner.releaseSession()
            if (mode.probeEnabled) {
                probeLink.send(ProbeProtocol.Command.STOP, sessionId) { _, _ -> }
            }
            tracker.clear()
        }
    }

    DisposableEffect(mode) {
        if (mode.probeEnabled) {
            val removeListener = ProbeTelemetryStore.observe(
                onSnapshot = { probeNode = it },
                onObservation = { sample ->
                    val observation = com.suri.pipsurios.prs.BleObservation(
                        temporaryId = sample.temporaryId,
                        rssi = sample.rssi,
                        observedAt = SystemClock.elapsedRealtime(),
                        deviceIdentifier = sample.deviceIdentifier,
                        deviceName = sample.deviceName,
                        advertisingDataHex = sample.advertisingDataHex,
                        deviceType = sample.deviceType,
                        observedAtEpochMillis = sample.timestampEpochMillis,
                        source = PrsObservationSource.PROBE_WATCH_2
                    )
                    tracker.observe(observation)
                    snapshot = tracker.snapshot()
                }
            )
            onDispose(removeListener)
        } else {
            probeNode = PrsProbeNodeSnapshot()
            onDispose { }
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
    val savedDevices = remember(registry) { registry.snapshot() }
    val observedSavedDevices = snapshot.contacts
        .mapNotNull { registry.savedDeviceFor(it.measured) }
        .toSet()
    val offlineSavedDevices = savedDevices.filterNot(observedSavedDevices::contains)
    val visibleContacts = snapshot.contacts
        .filter { contact ->
            targetMatchesQuery(contact, registry.savedDeviceFor(contact.measured), targetQuery)
        }
        .sortedWith(targetContactComparator(registry))
    val visibleOfflineSavedDevices = offlineSavedDevices
        .filter { device -> savedDeviceMatchesQuery(device, targetQuery) }
    fun chooseTarget(contact: PrsContactSnapshot) {
        val map = selectedMap ?: return
        val knownRule = registry.savedDeviceFor(contact.measured)
        onTargetSelected(
            IndividualTrackingSelection(
                mapId = map.mapId,
                target = IndividualTrackingTarget(
                    contactId = contact.contactId,
                    deviceIdentifier = contact.measured.deviceIdentifier,
                    displayName = knownRule?.displayName ?: contact.displayName,
                    source = contact.source,
                    knownRule = knownRule
                )
            )
        )
    }

    fun chooseSavedDevice(device: PrsSavedDevice) {
        val map = selectedMap ?: return
        onTargetSelected(
            IndividualTrackingSelection(
                mapId = map.mapId,
                target = IndividualTrackingTarget(
                    contactId = "SAVED-${device.type.name}-${device.value}",
                    deviceIdentifier = if (device.type == PrsDeviceRuleType.ADDRESS) {
                        device.value
                    } else {
                        ""
                    },
                    displayName = device.displayName ?: device.value,
                    source = null,
                    knownRule = device
                )
            )
        )
    }
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        if (splitLayout) {
            V4TargetSplitLayout(
                title = title,
                modeLabel = modeLabel,
                locationStepLabel = locationStepLabel,
                targetStepLabel = targetStepLabel,
                selectedMap = selectedMap,
                scanStatus = scanStatus,
                mode = mode,
                registry = registry,
                probeNode = probeNode,
                probeLinkStatus = probeLinkStatus,
                targetQuery = targetQuery,
                onTargetQueryChanged = { targetQuery = it },
                visibleContacts = visibleContacts,
                totalContactCount = snapshot.contacts.size,
                onMapSelected = { selectedMapId = it.mapId },
                onChangeLocation = { selectedMapId = null; targetQuery = "" },
                onTargetSelected = ::chooseTarget,
                offlineSavedDevices = visibleOfflineSavedDevices,
                onSavedDeviceSelected = ::chooseSavedDevice,
                onAllowBluetooth = { permissionLauncher.launch(prsPermissions()) },
                onRetry = { retryVersion++ },
                onBack = onBack,
                useProbabilityArea = useProbabilityArea
            )
        } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, top = 20.dp, end = 24.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            if (selectedMap == null) {
                Text(locationStepLabel, color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text("Selecciona el campo antes de identificar el objetivo BLE.", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
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
                Text("CAMPO: ${selectedMap.name}", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text(targetStepLabel, color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                Text("MODO: $modeLabel", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                if (mode.probeEnabled) {
                    Text("PROBE: ${prsProbeStateLabel(probeNode.state)} // $probeLinkStatus", color = if (probeNode.state == "ACTIVE") PipGreen else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Text(
                    "DISPOSITIVOS CONOCIDOS: ${registry.snapshot().size}  //  REGLAS P.R.S. REUTILIZADAS",
                    color = PipGreenDim,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                TargetSearchField(
                    query = targetQuery,
                    onQueryChanged = { targetQuery = it },
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    if (visibleContacts.isEmpty() && visibleOfflineSavedDevices.isEmpty()) {
                        Text(
                            if (snapshot.contacts.isEmpty() && offlineSavedDevices.isEmpty()) {
                                "${scanStatusLabel(scanStatus)} // ESPERANDO ANUNCIOS BLE..."
                            } else {
                                "SIN COINCIDENCIAS PARA LA BÚSQUEDA"
                            },
                            color = individualScanStatusColor(scanStatus),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        visibleContacts.forEach { contact ->
                            val knownRule = registry.savedDeviceFor(contact.measured)
                            val omitted = registry.isIgnored(contact.measured)
                            IndividualTargetRow(
                                contact = contact,
                                knownRule = knownRule,
                                omittedByPrs = omitted,
                                onClick = { chooseTarget(contact) }
                            )
                        }
                        visibleOfflineSavedDevices.forEach { device ->
                            SavedOfflineTargetRow(
                                device = device,
                                selectionEnabled = true,
                                onClick = { chooseSavedDevice(device) }
                            )
                        }
                    }
                }
                IndividualMenuAction("> CAMBIAR CAMPO", { selectedMapId = null })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (scanStatus == BleScanStatus.PERMISSION_REQUIRED) {
                    IndividualMenuAction("> PERMITIR BLUETOOTH", { permissionLauncher.launch(prsPermissions()) }, Modifier.weight(1f))
                } else if (scanStatus == BleScanStatus.BLUETOOTH_OFF || scanStatus == BleScanStatus.ERROR) {
                    IndividualMenuAction("> REINTENTAR", { retryVersion++ }, Modifier.weight(1f))
                }
                IndividualMenuAction("< BACK", onBack, Modifier.weight(1f))
            }
        }
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
        }
        if (splitLayout) {
            TerminalOverlay { }
        }
    }
}

@Composable
private fun V4TargetSplitLayout(
    title: String,
    modeLabel: String,
    locationStepLabel: String,
    targetStepLabel: String,
    selectedMap: OfflineMapDefinition?,
    visibleContacts: List<PrsContactSnapshot>,
    totalContactCount: Int,
    scanStatus: BleScanStatus,
    mode: PrsOperatingMode,
    registry: PrsDeviceRegistry,
    probeNode: PrsProbeNodeSnapshot,
    probeLinkStatus: String,
    targetQuery: String,
    onTargetQueryChanged: (String) -> Unit,
    useProbabilityArea: Boolean,
    onMapSelected: (OfflineMapDefinition) -> Unit,
    onChangeLocation: () -> Unit,
    onTargetSelected: (PrsContactSnapshot) -> Unit,
    offlineSavedDevices: List<PrsSavedDevice>,
    onSavedDeviceSelected: (PrsSavedDevice) -> Unit,
    onAllowBluetooth: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 34.dp, top = 34.dp, end = 34.dp, bottom = 34.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        TerminalPanel(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text(title, color = PipGreen, fontSize = 21.sp, fontFamily = FontFamily.Monospace)
                Text("MODO: $modeLabel", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(locationStepLabel, color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                if (selectedMap == null) {
                Text("SELECCIONA EL CAMPO PARA ESTA SESIÓN.", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        OfflineMapCatalog.maps.forEach { map ->
                            IndividualMenuAction("> ${map.name}", { onMapSelected(map) })
                        }
                    }
                } else {
                    Text("UBICACIÓN: ${selectedMap.name}", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text(targetStepLabel, color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text("SELECCIONA UN OBJETIVO DE LA LISTA DE DISPOSITIVOS.", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(
                            if (useProbabilityArea) "PASO 2 // ÁREA DEL OBJETIVO" else "PASO 2 // GRID SOBRE EL MAPA",
                            color = PipGreen,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            if (useProbabilityArea) "El objetivo seleccionado abre el mapa del área objetivo."
                            else "El objetivo seleccionado abre el mapa GRID.",
                            color = PipGreenDim,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        IndividualMenuAction("> CAMBIAR UBICACIÓN", onChangeLocation)
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    val retryNeeded = scanStatus == BleScanStatus.PERMISSION_REQUIRED ||
                        scanStatus == BleScanStatus.BLUETOOTH_OFF ||
                        scanStatus == BleScanStatus.ERROR
                    if (retryNeeded) {
                        IndividualMenuAction(
                            if (scanStatus == BleScanStatus.PERMISSION_REQUIRED) "> PERMITIR BLUETOOTH" else "> REINTENTAR",
                            if (scanStatus == BleScanStatus.PERMISSION_REQUIRED) onAllowBluetooth else onRetry,
                            Modifier.weight(1f)
                        )
                    }
                    IndividualMenuAction("< BACK", onBack, Modifier.weight(1f))
                }
            }
        }

        TerminalPanel(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                Text("DISPOSITIVOS DETECTADOS", color = PipGreen, fontSize = 21.sp, fontFamily = FontFamily.Monospace)
                if (mode.probeEnabled) {
                    Text("PROBE: ${prsProbeStateLabel(probeNode.state)} // $probeLinkStatus", color = if (probeNode.state == "ACTIVE") PipGreen else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                TargetSearchField(
                    query = targetQuery,
                    onQueryChanged = onTargetQueryChanged,
                )
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    if (visibleContacts.isEmpty() && offlineSavedDevices.isEmpty()) {
                        Text(
                            if (totalContactCount == 0 && offlineSavedDevices.isEmpty()) {
                                "${scanStatusLabel(scanStatus)} // ESPERANDO ANUNCIOS BLE..."
                            } else {
                                "SIN COINCIDENCIAS PARA LA BÚSQUEDA"
                            },
                            color = individualScanStatusColor(scanStatus),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        visibleContacts.forEach { contact ->
                            val knownRule = registry.savedDeviceFor(contact.measured)
                            val omitted = registry.isIgnored(contact.measured)
                            IndividualTargetRow(
                                contact = contact,
                                knownRule = knownRule,
                                omittedByPrs = omitted,
                                selectionEnabled = selectedMap != null,
                                onClick = { onTargetSelected(contact) }
                            )
                        }
                        offlineSavedDevices.forEach { device ->
                            SavedOfflineTargetRow(
                                device = device,
                                selectionEnabled = selectedMap != null,
                                onClick = { onSavedDeviceSelected(device) }
                            )
                        }
                    }
                    if (selectedMap == null) {
                        Text("SELECCIONA PRIMERO LA UBICACIÓN PARA ACTIVAR LA SELECCIÓN.", color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

@Composable
fun IndividualTrackingTrackerScreen(
    selection: IndividualTrackingSelection?,
    onSelectTarget: () -> Unit,
    onBack: () -> Unit,
    mode: PrsOperatingMode = PrsOperatingMode.LOCAL_SCAN,
    modeLabel: String = mode.displayName,
    title: String = "INDIVIDUAL TRACKER",
    useProbabilityArea: Boolean = false
) {
    if (selection == null) {
        Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
                Text("OBJETIVO NO SELECCIONADO", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                IndividualMenuAction("> ABRIR TARGET", onSelectTarget)
            }
            PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(24.dp))
        }
        return
    }
    IndividualTrackerMapContent(
        selection = selection,
        onBack = onBack,
        mode = mode,
        modeLabel = modeLabel,
        title = title,
        useProbabilityArea = useProbabilityArea
    )
}

@Composable
private fun IndividualTrackerMapContent(
    selection: IndividualTrackingSelection,
    onBack: () -> Unit,
    mode: PrsOperatingMode,
    modeLabel: String,
    title: String,
    useProbabilityArea: Boolean
) {
    val context = LocalContext.current
    val definition = OfflineMapCatalog.maps.firstOrNull { it.mapId == selection.mapId }
    if (definition == null) {
        Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
            Text("CAMPO TERRAIN NO ENCONTRADO", color = PipRed, fontSize = 18.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.Center))
            PrsBackButton(onBack = onBack, modifier = Modifier.align(Alignment.BottomStart).padding(24.dp))
        }
        return
    }

    val scanner = remember(selection, mode) { BleScanner(context.applicationContext) }
    val probeLink = remember(selection, mode) { ProbeLink(context.applicationContext) }
    val tracker = remember(selection, mode) { PrsContactTracker() }
    val readingSound = remember(selection, mode) { PrsReadingSound(context.applicationContext) }
    val targetAreaEstimator = remember(selection, mode) { PrsTargetAreaEstimator() }
    val locationSource = remember(selection, mode) { TerrainLocation(context.applicationContext) }
    val headingSource = remember(selection, mode) { TerrainHeading(context.applicationContext) }
    val overlayRepository = remember(selection, mode) { TerrainOverlayRepository.from(context.applicationContext) }
    var snapshot by remember(selection) { mutableStateOf(PrsSnapshot()) }
    var scanStatus by remember(selection) { mutableStateOf(BleScanStatus.IDLE) }
    var probeNode by remember(selection, mode) { mutableStateOf(PrsProbeNodeSnapshot()) }
    var probeLinkStatus by remember(selection, mode) { mutableStateOf(if (mode.probeEnabled) "INICIANDO" else "NO USADO") }
    var fix by remember(selection) { mutableStateOf<TerrainLocationFix?>(null) }
    var locationStatus by remember(selection) { mutableStateOf("ESPERANDO GPS") }
    var heading by remember(selection) { mutableFloatStateOf(0f) }
    var headingStatus by remember(selection) { mutableStateOf("RUMBO PENDIENTE") }
    var permissionVersion by remember(selection) { mutableIntStateOf(0) }
    var retryVersion by remember(selection) { mutableIntStateOf(0) }
    var mapData by remember(selection) { mutableStateOf<MbTilesData?>(null) }
    var loadedTiles by remember(selection) { mutableStateOf<Map<TileKey, ImageBitmap>>(emptyMap()) }
    var overlays by remember(selection) { mutableStateOf(MapOverlays()) }
    var loadError by remember(selection) { mutableStateOf<String?>(null) }
    var center by remember(selection) { mutableStateOf(definition.bounds.center) }
    var zoom by remember(selection) { mutableFloatStateOf(17.5f) }
    var canvasSize by remember(selection) { mutableStateOf(IntSize.Zero) }
    var targetArea by remember(selection, mode) { mutableStateOf(PrsTargetAreaEstimate()) }
    val sessionId = remember(selection, mode) { "PRS-V4-${System.currentTimeMillis()}" }
    val latestFix by rememberUpdatedState(fix)
    val latestHeading by rememberUpdatedState(heading)
    val latestHeadingStatus by rememberUpdatedState(headingStatus)
    val latestProbeNode by rememberUpdatedState(probeNode)

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

    DisposableEffect(permissionVersion, retryVersion, selection, mode) {
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
            locationStatus = "ESPERANDO GPS"
            locationSource.start(
                onFix = {
                    fix = it
                    locationStatus = if (definition.bounds.contains(it.point)) "GPS ACTIVO // EN CAMPO" else "GPS ACTIVO // FUERA DE CAMPO"
                },
                onUnavailable = { locationStatus = "GPS NO DISPONIBLE" }
            )
        } else {
            locationStatus = "SE NECESITA PERMISO DE UBICACIÓN"
        }
        headingSource.start(
            onHeading = {
                heading = it
                headingStatus = "RUMBO ACTIVO"
            },
            onUnavailable = { headingStatus = "RUMBO NO DISPONIBLE" }
        )
        if (mode.probeEnabled) {
            probeLinkStatus = "INICIANDO"
            probeLink.send(mode.command!!, sessionId) { success, detail ->
                probeLinkStatus = if (success) "COMANDO ENVIADO // $detail" else "ERROR // $detail"
            }
        }
        onDispose {
            scanner.releaseSession()
            tracker.clear()
            locationSource.stop()
            headingSource.stop()
            if (mode.probeEnabled) {
                probeLink.send(ProbeProtocol.Command.STOP, sessionId) { _, _ -> }
            }
        }
    }

    DisposableEffect(selection, mode) {
        if (mode.probeEnabled) {
            val removeListener = ProbeTelemetryStore.observe(
                onSnapshot = { probeNode = it },
                onObservation = { sample ->
                    val observation = com.suri.pipsurios.prs.BleObservation(
                        temporaryId = sample.temporaryId,
                        rssi = sample.rssi,
                        observedAt = SystemClock.elapsedRealtime(),
                        deviceIdentifier = sample.deviceIdentifier,
                        deviceName = sample.deviceName,
                        advertisingDataHex = sample.advertisingDataHex,
                        deviceType = sample.deviceType,
                        observedAtEpochMillis = sample.timestampEpochMillis,
                        source = PrsObservationSource.PROBE_WATCH_2
                    )
                    if (selection.target.matches(observation)) {
                        tracker.observe(observation)
                        snapshot = tracker.snapshot()
                    }
                }
            )
            onDispose(removeListener)
        } else {
            probeNode = PrsProbeNodeSnapshot()
            onDispose { }
        }
    }

    LaunchedEffect(tracker) {
        while (true) {
            delay(com.suri.pipsurios.prs.PrsTuning.DEFAULT.evaluationIntervalMillis)
            val readingCompleted = tracker.evaluate(SystemClock.elapsedRealtime())
            if (readingCompleted) readingSound.play()
            val currentSnapshot = tracker.snapshot()
            if (readingCompleted) {
                val contact = currentSnapshot.contacts.firstOrNull()
                if (contact == null) {
                    targetAreaEstimator.clear()
                    targetArea = PrsTargetAreaEstimate()
                } else {
                    val measurementPoint = when (contact.source) {
                        PrsObservationSource.PROBE_WATCH_2 -> latestProbeNode.location?.let {
                            GeoPoint(it.latitude, it.longitude)
                        }
                        else -> latestFix?.point
                    }?.takeIf(definition.bounds::contains)
                    val measurementAccuracy = when (contact.source) {
                        PrsObservationSource.PROBE_WATCH_2 -> latestProbeNode.location?.accuracyMeters
                        else -> latestFix?.accuracyMeters
                    } ?: 25f
                    val measurementHeading = if (
                        contact.source == PrsObservationSource.A56 &&
                        latestHeadingStatus == "RUMBO ACTIVO"
                    ) {
                        latestHeading
                    } else {
                        null
                    }
                    targetArea = targetAreaEstimator.update(
                        contact = contact,
                        measurementPoint = measurementPoint,
                        headingDegrees = measurementHeading,
                        gpsAccuracyMeters = measurementAccuracy
                    )
                }
            }
            snapshot = currentSnapshot
        }
    }

    DisposableEffect(readingSound) {
        onDispose { readingSound.release() }
    }

    LaunchedEffect(definition) {
        mapData = null
        loadedTiles = emptyMap()
        loadError = null
        overlays = withContext(Dispatchers.IO) { overlayRepository.load(definition.mapId) }
        runCatching {
            withContext(Dispatchers.IO) { MbTilesRepository(context.applicationContext).load(definition) }
        }.onSuccess { mapData = it }
            .onFailure { loadError = it.message ?: "ERROR AL CARGAR EL MAPA" }
    }

    DisposableEffect(mapData) {
        val current = mapData
        onDispose { current?.close() }
    }

    val tileCoverage = remember(mapData) {
        mapData?.let { TerrainTileCoverage.from(it.tileKeys, definition.maxNativeZoom) }
    }
    val minimumCoverageZoom = tileCoverage?.minimumDisplayZoom(
        canvasSize.width,
        canvasSize.height,
        definition.minZoom.toFloat(),
        definition.maxDisplayZoom.toFloat()
    ) ?: definition.minZoom.toFloat()
    val currentCenter by rememberUpdatedState(center)
    val currentZoom by rememberUpdatedState(zoom)
    val currentHeading by rememberUpdatedState(heading)
    val tileRequestHeading = (heading / 15f).roundToInt() * 15f
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
    LaunchedEffect(mapData, center, zoom, tileRequestHeading, canvasSize) {
        val data = mapData ?: return@LaunchedEffect
        if (canvasSize.width <= 0 || canvasSize.height <= 0) return@LaunchedEffect
        delay(80)
        val tileZoom = zoom.roundToInt().coerceIn(definition.minZoom, definition.maxNativeZoom)
        val transform = TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, tileRequestHeading)
        val requested = individualVisibleTileKeys(data, transform, tileZoom, canvasSize)
        val missing = requested.filterNot(loadedTiles::containsKey)
        if (missing.isNotEmpty()) {
            val loaded = withContext(Dispatchers.IO) {
                data.loadTiles(missing.toSet())
            }
            loadedTiles = loadedTiles.filterKeys { it in requested } + loaded
        } else {
            loadedTiles = loadedTiles.filterKeys { it in requested }
        }
    }

    val selectedContact = snapshot.contacts.firstOrNull()
    val gridProbe = probeGridPosition(fix, probeNode, mode)
    val mapTransform = if (canvasSize.width > 0 && canvasSize.height > 0) {
        TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, heading)
    } else {
        null
    }
    val panelScroll = rememberScrollState()
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .onSizeChanged { canvasSize = it }
                    .pointerInput(tileCoverage, canvasSize, definition.mapId) {
                        detectTransformGestures { centroid, pan, zoomChange, _ ->
                            if (canvasSize.width <= 0 || canvasSize.height <= 0) return@detectTransformGestures
                            val currentTransform = TerrainViewportTransform(
                                currentCenter,
                                currentZoom,
                                canvasSize.width,
                                canvasSize.height,
                                currentHeading
                            )
                            val updated = currentTransform.applyGesture(
                                centroid.x,
                                centroid.y,
                                pan.x,
                                pan.y,
                                zoomChange,
                                minimumCoverageZoom,
                                definition.maxDisplayZoom.toFloat()
                            )
                            center = tileCoverage?.constrainCenterMovement(
                                currentCenter,
                                updated.center,
                                updated.zoom,
                                canvasSize.width,
                                canvasSize.height
                            ) ?: updated.center
                            zoom = updated.zoom
                        }
                    }
                    .pointerInput(fix, center, zoom, heading, tileCoverage, canvasSize) {
                        detectTapGestures { offset ->
                            val userFix = fix ?: return@detectTapGestures
                            if (canvasSize.width <= 0 || canvasSize.height <= 0) return@detectTapGestures
                            val transform = TerrainViewportTransform(
                                center,
                                zoom,
                                canvasSize.width,
                                canvasSize.height,
                                heading
                            )
                            val marker = transform.geoToScreen(userFix.point)
                            val markerOffset = Offset(marker.first, marker.second)
                            if ((markerOffset - offset).getDistance() <= 38f) {
                                center = tileCoverage?.clampCenterForFullRotation(
                                    userFix.point,
                                    zoom,
                                    canvasSize.width,
                                    canvasSize.height
                                ) ?: userFix.point
                            }
                        }
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(PipMapBackground)
                    if (canvasSize.width > 0 && canvasSize.height > 0) {
                        val transform = TerrainViewportTransform(center, zoom, canvasSize.width, canvasSize.height, heading)
                        clipRect {
                            rotate(-heading, Offset(transform.pivotX, transform.pivotY)) {
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
                            }
                        }
                    }
                }
                if (useProbabilityArea) {
                    PrsProbabilityArea(
                        estimate = targetArea,
                        mapTransform = mapTransform,
                        modifier = Modifier.fillMaxSize().padding(2.dp)
                    )
                } else {
                    PrsDensityGrid(
                        contacts = selectedContact?.let(::listOf) ?: emptyList(),
                        selectedContactId = selectedContact?.contactId,
                        selectedDisplayName = selection.target.displayName,
                        probeNodes = listOfNotNull(gridProbe),
                        modifier = Modifier.fillMaxSize().padding(2.dp),
                        surfaceColor = Color.Transparent,
                        showEmblem = false,
                        showTargetLabel = false
                    )
                }
                if (fix != null && mapTransform != null) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val userPoint = fix?.point ?: return@Canvas
                        val marker = mapTransform.geoToScreen(userPoint)
                        drawUserLocationMarker(Offset(marker.first, marker.second))
                    }
                }
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
                Text(title, color = PipGreen, fontSize = 19.sp, fontFamily = FontFamily.Monospace)
                Text("CAMPO: ${definition.name}", color = PipAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Text("MODO: $modeLabel", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(
                    if (useProbabilityArea) "VISTA: ÁREA DEL OBJETIVO" else "GRID: SOLO OBJETIVO",
                    color = PipGreenDim,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text("CENTRO: ÁREA OBJETIVO // OBJETIVO ESTÁTICO", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                Text("LADO: INFERENCIA DE RUMBO + RSSI", color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                if (useProbabilityArea) {
                    if (targetArea.center == null) {
                        Text("ÁREA OBJETIVO: ESPERANDO LECTURAS", color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        Text("LADO DEL OBJETIVO: ${targetArea.side.label}", color = PipRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text("MUESTRAS DE ÁREA: ${targetArea.sampleCount} // CONF ${formatSignal(targetArea.confidence)}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
                Column(
                    modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(panelScroll),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text("OBJETIVO", color = PipAmber, fontSize = 14.sp, fontFamily = FontFamily.Monospace)
                    Text(selection.target.displayName + selection.target.knownRule.categoryLabel(), color = PipGreen, fontSize = 15.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("ID: ${selection.target.deviceIdentifier}", color = PipGreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("ORIGEN: ${selection.target.source?.displayName ?: "A56 + WATCH 2"}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("P.R.S.: ${scanStatusLabel(scanStatus)}", color = individualScanStatusColor(scanStatus), fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    if (mode.probeEnabled) {
                        Text("PROBE: ${prsProbeStateLabel(probeNode.state)}", color = if (probeNode.state == "ACTIVE") PipGreen else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Text(probeLinkStatus, color = PipGreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        probeNode.location?.let { location ->
                            Text("POSICIÓN PROBE: ±${formatSignal(location.accuracyMeters)} m  BATERÍA ${location.batteryPercent?.toString() ?: "--"}%", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        } ?: Text("POSICIÓN PROBE: ESPERANDO UBICACIÓN", color = PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                    Text("GPS: $locationStatus", color = if (locationStatus.contains("ACTIVO")) PipGreen else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("TOCA EL PUNTO AZUL // RECENTRAR", color = com.suri.pipsurios.ui.theme.PipBlue, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("${headingStatus} // ${formatSignal(heading)}°", color = if (headingStatus.contains("ACTIVO")) PipGreenDim else PipAmber, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    if (selectedContact == null) {
                        Text("OBJETIVO NO DISPONIBLE // BUSCANDO", color = PipAmber, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    } else {
                        IndividualTargetDetails(selectedContact)
                    }
                    Text("MODELO DE SEÑAL: RELATIVO / EXPERIMENTAL", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("Sin coordenada exacta ni conversión RSSI-metros calibrada.", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
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
    Text("RSSI BRUTO: ${contact.measured.rssi} dBm", color = PipGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("RSSI SUAVIZADO: ${formatSignal(contact.processed.smoothedRssi)} dBm", color = PipGreen, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("TENDENCIA: ${contact.inference.trend.displayLabel()}", color = trendColor(contact.inference.trend), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("BANDA: ${contact.inference.proximity.displayLabel()}", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
    Text("MUESTRAS: ${contact.sampleCount} // CONF: ${formatSignal(contact.inference.densityCloud.confidence)}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
}

@Composable
private fun TargetSearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
            .padding(horizontal = 7.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            "BUSCAR // NOMBRE, ID O MAC",
            color = PipAmber,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace,
        )
        BasicTextField(
            value = query,
            onValueChange = { onQueryChanged(it.take(32)) },
            singleLine = true,
            textStyle = TextStyle(
                color = PipGreen,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (query.isBlank()) {
                    Text(
                        "ESCRIBE PARA FILTRAR",
                        color = PipGreenDim,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                    )
                }
                innerTextField()
            }
        )
    }
}

private fun targetMatchesQuery(
    contact: PrsContactSnapshot,
    knownRule: PrsSavedDevice?,
    query: String,
): Boolean {
    val terms = targetQueryTerms(query)
    if (terms.isEmpty()) return true
    val searchable = listOf(
        contact.displayName,
        knownRule?.displayName,
        contact.measured.deviceName,
        contact.measured.deviceIdentifier,
        contact.contactId,
    ).filterNotNull().joinToString(" ").uppercase(Locale.US)
    return terms.all(searchable::contains)
}

private fun savedDeviceMatchesQuery(device: PrsSavedDevice, query: String): Boolean {
    val terms = targetQueryTerms(query)
    if (terms.isEmpty()) return true
    val searchable = listOf(device.displayName, device.value, device.type.label)
        .filterNotNull()
        .joinToString(" ")
        .uppercase(Locale.US)
    return terms.all(searchable::contains)
}

private fun targetQueryTerms(query: String): List<String> = query
    .trim()
    .uppercase(Locale.US)
    .split(Regex("\\s+"))
    .filter(String::isNotBlank)

private fun targetContactComparator(registry: PrsDeviceRegistry): Comparator<PrsContactSnapshot> =
    compareByDescending<PrsContactSnapshot> { registry.savedDeviceFor(it.measured) != null }
        .thenByDescending { it.processed.smoothedRssi }
        .thenByDescending { it.measured.observedAt }
        .thenBy { it.displayName.uppercase(Locale.US) }

private fun shortDeviceIdentifier(value: String): String {
    val compact = value.replace(":", "").replace("-", "").uppercase(Locale.US)
    return compact.takeLast(6).ifBlank { "------" }
}

private fun compactAddressType(contact: PrsContactSnapshot): String = when {
    contact.measured.addressTypeLabel() == "ALEATORIA / PRIVADA" -> "PRIVADA"
    contact.measured.addressTypeLabel() == "PÚBLICA" -> "PUBLICA"
    contact.measured.addressTypeLabel() == "ANÓNIMA" -> "ANONIMA"
    else -> "ID"
}

@Composable
private fun IndividualTargetRow(
    contact: PrsContactSnapshot,
    knownRule: PrsSavedDevice?,
    omittedByPrs: Boolean,
    selectionEnabled: Boolean = true,
    onClick: () -> Unit
) {
    // DEVICES rules exclude noise from SENTRY, but TRACKER is an explicit
    // target picker. A known device must remain selectable here even when its
    // SENTRY exclusion rule is active.
    val enabled = selectionEnabled
    val savedDevice = knownRule != null
    val color = when {
        savedDevice -> PipAmber
        enabled -> PipGreen
        else -> PipGreenDim
    }
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
            text = "${contact.source.displayName} // RSSI ${contact.measured.rssi} // ID ${shortDeviceIdentifier(contact.measured.deviceIdentifier)} // ${compactAddressType(contact)}",
            color = if (savedDevice) PipAmber else PipGreenDim,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (savedDevice) {
            Text(
                if (omittedByPrs) {
                    "GUARDADO // REGLA SENTRY ACTIVA // OBJETIVO DISPONIBLE"
                } else {
                    "GUARDADO // OBJETIVO DISPONIBLE"
                },
                color = PipAmber,
                fontSize = 9.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        if (!selectionEnabled) {
            Text("SELECCIONA LA UBICACIÓN ANTES DEL OBJETIVO", color = PipAmber, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
private fun SavedOfflineTargetRow(
    device: PrsSavedDevice,
    selectionEnabled: Boolean,
    onClick: () -> Unit
) {
    val color = if (selectionEnabled) PipAmber else PipGreenDim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, color.copy(alpha = 0.6f))
            .clickable(enabled = selectionEnabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 7.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = device.displayName ?: device.value,
            color = color,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${device.type.label} // SIN CONEXIÓN // BUSCAR EN TRACKER",
            color = PipAmber,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = if (selectionEnabled) {
                "DISPOSITIVO GUARDADO // OBJETIVO LISTO"
            } else {
                "SELECCIONA LA UBICACIÓN ANTES DEL OBJETIVO"
            },
            color = PipGreenDim,
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
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

private fun PrsSavedDevice?.categoryLabel(): String = this?.displayName?.let { " // CONOCIDO" } ?: ""

private fun PrsTrend.displayLabel(): String = when (this) {
    PrsTrend.APPROACHING -> "ACERCÁNDOSE"
    PrsTrend.MOVING_AWAY -> "ALEJÁNDOSE"
    PrsTrend.STABLE -> "ESTABLE"
    PrsTrend.INSUFFICIENT_DATA -> "ESPERANDO"
}

private fun PrsProximityBand.displayLabel(): String = when (this) {
    PrsProximityBand.UNKNOWN -> "DESCONOCIDA"
    PrsProximityBand.NEAR -> "CERCA"
    PrsProximityBand.MEDIUM -> "MEDIA"
    PrsProximityBand.FAR -> "LEJOS"
}

private fun scanStatusLabel(status: BleScanStatus): String = when (status) {
    BleScanStatus.IDLE -> "EN ESPERA"
    BleScanStatus.SCANNING -> "ESCANEANDO"
    BleScanStatus.PERMISSION_REQUIRED -> "PERMISO NECESARIO"
    BleScanStatus.BLUETOOTH_OFF -> "BLUETOOTH DESACTIVADO"
    BleScanStatus.UNSUPPORTED -> "NO COMPATIBLE"
    BleScanStatus.ERROR -> "ERROR"
}

private fun prsProbeStateLabel(state: String): String = when (state) {
    "ACTIVE" -> "ACTIVO"
    "DISCONNECTED" -> "DESCONECTADO"
    "STARTING" -> "INICIANDO"
    "STOPPED" -> "DETENIDO"
    "STALE" -> "DESACTUALIZADO"
    else -> state
}

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
