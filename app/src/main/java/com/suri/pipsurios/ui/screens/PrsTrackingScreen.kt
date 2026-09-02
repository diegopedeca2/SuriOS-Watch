package com.suri.pipsurios.ui.screens

import android.Manifest
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.prs.BleScanStatus
import com.suri.pipsurios.prs.BleScanner
import com.suri.pipsurios.prs.PrsGridProbe
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsContactTracker
import com.suri.pipsurios.prs.PrsDeviceRegistry
import com.suri.pipsurios.prs.PrsProbeDisplayTuning
import com.suri.pipsurios.prs.PrsProbeNodeSnapshot
import com.suri.pipsurios.prs.PrsProximityBand
import com.suri.pipsurios.prs.PrsSnapshot
import com.suri.pipsurios.prs.PrsTrend
import com.suri.pipsurios.prs.PrsTuning
import com.suri.pipsurios.prs.ProbeLink
import com.suri.pipsurios.prs.ProbeTelemetryStore
import com.suri.pipsurios.prs.categorySuffix
import com.suri.pipsurios.terrain.TerrainLocation
import com.suri.pipsurios.terrain.TerrainLocationFix
import kotlin.math.cos
import kotlin.math.PI
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipNeutral
import com.suri.pipsurios.ui.theme.PipNeutralDim
import com.suri.pipsurios.ui.theme.PipPanel
import com.suri.pipsurios.ui.theme.PipActionBackground
import kotlinx.coroutines.delay

enum class PrsCompactPage {
    SCAN,
    GRID
}

@Composable
fun PrsTrackingScreen(
    mode: PrsOperatingMode,
    onBack: () -> Unit,
    compact: Boolean = false,
    compactPage: PrsCompactPage = PrsCompactPage.SCAN,
    onCompactPageSelected: (PrsCompactPage) -> Unit = {},
    onCompactDevicesSelected: () -> Unit = {}
) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    val probeLink = remember(context) { ProbeLink(context.applicationContext) }
    val phoneLocation = remember(context) { TerrainLocation(context.applicationContext) }
    val tracker = remember { PrsContactTracker() }
    val deviceRegistry = remember(context) { PrsDeviceRegistry.from(context.applicationContext) }
    var snapshot by remember { mutableStateOf(PrsSnapshot()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var probeNode by remember { mutableStateOf(PrsProbeNodeSnapshot()) }
    var probeLinkStatus by remember { mutableStateOf(if (mode.probeEnabled) "STARTING" else "NOT USED") }
    var phoneFix by remember { mutableStateOf<TerrainLocationFix?>(null) }
    var selectedContactId by remember { mutableStateOf<String?>(null) }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var retryVersion by remember { mutableIntStateOf(0) }
    val sessionId = remember { "PRS-${System.currentTimeMillis()}" }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions()) {
            permissionLauncher.launch(prsPermissions())
        }
    }

    DisposableEffect(permissionVersion, retryVersion, mode) {
        scanStatus = if (mode.localScannerEnabled) {
            scanner.start(
                onObservation = { observation ->
                    if (!deviceRegistry.isIgnored(observation)) {
                        tracker.observe(observation)
                        snapshot = tracker.snapshot()
                    }
                },
                onStatusChanged = { scanStatus = it }
            )
        } else {
            scanner.stop()
            BleScanStatus.IDLE
        }
        if (mode.probeEnabled && phoneLocation.hasPermission()) {
            phoneLocation.start(
                onFix = { phoneFix = it },
                onUnavailable = { phoneFix = null }
            )
        }
        onDispose {
            scanner.stop()
            phoneLocation.stop()
        }
    }

    DisposableEffect(mode) {
        if (mode.probeEnabled) {
            probeLinkStatus = "STARTING"
            probeLink.send(mode.command!!, sessionId) { success, detail ->
                probeLinkStatus = if (success) "COMMAND SENT // $detail" else "ERROR // $detail"
            }
        }
        onDispose {
            if (mode.probeEnabled) {
                probeLink.send(com.suri.probeprotocol.ProbeProtocol.Command.STOP, sessionId) { _, _ -> }
            }
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
                    if (!deviceRegistry.isIgnored(observation)) {
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

    DisposableEffect(scanner) {
        onDispose {
            scanner.releaseSession()
            tracker.clear()
        }
    }

    LaunchedEffect(tracker) {
        while (true) {
            delay(PrsTuning.DEFAULT.evaluationIntervalMillis)
            tracker.evaluate(SystemClock.elapsedRealtime())
            snapshot = tracker.snapshot()
            if (selectedContactId != null && snapshot.contact(selectedContactId) == null) {
                selectedContactId = null
            }
        }
    }

    val gridProbe = probeGridPosition(phoneFix, probeNode, mode)

    PrsTrackingContent(
        mode = mode,
        compact = compact,
        compactPage = compactPage,
        snapshot = snapshot,
        scanStatus = scanStatus,
        probeNode = probeNode,
        probeLinkStatus = probeLinkStatus,
        gridProbe = gridProbe,
        selectedContactId = selectedContactId,
        onSelectContact = { selectedContactId = it },
        onClearTarget = { selectedContactId = null },
        onGrantPermission = { permissionLauncher.launch(prsPermissions()) },
        onRetry = {
            retryVersion++
            if (mode.probeEnabled) {
                probeLinkStatus = "RETRYING"
                mode.command?.let { command ->
                    probeLink.send(command, sessionId) { success, detail ->
                        probeLinkStatus = if (success) "COMMAND SENT // $detail" else "ERROR // $detail"
                    }
                }
            }
        },
        onClear = {
            tracker.clear()
            snapshot = tracker.snapshot()
            selectedContactId = null
        },
        onBack = onBack,
        onCompactPageSelected = onCompactPageSelected,
        onCompactDevicesSelected = onCompactDevicesSelected
    )
}

@Composable
private fun PrsTrackingContent(
    mode: PrsOperatingMode,
    compact: Boolean,
    compactPage: PrsCompactPage,
    snapshot: PrsSnapshot,
    scanStatus: BleScanStatus,
    probeNode: PrsProbeNodeSnapshot,
    probeLinkStatus: String,
    gridProbe: PrsGridProbe?,
    selectedContactId: String?,
    onSelectContact: (String) -> Unit,
    onClearTarget: () -> Unit,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onCompactPageSelected: (PrsCompactPage) -> Unit,
    onCompactDevicesSelected: () -> Unit
) {
    val selected = snapshot.contact(selectedContactId)
    if (compact) {
        PrsCompactTrackingContent(
            page = compactPage,
            snapshot = snapshot,
            scanStatus = scanStatus,
            gridProbe = gridProbe,
            selectedContactId = selectedContactId,
            onSelectContact = onSelectContact,
            onClearTarget = onClearTarget,
            onGrantPermission = onGrantPermission,
            onRetry = onRetry,
            onClear = onClear,
            onBack = onBack,
            onPageSelected = onCompactPageSelected,
            onDevicesSelected = onCompactDevicesSelected
        )
        return
    }
    val panelScroll = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "P.R.S. / ${mode.displayName}",
                    color = PipGreen,
                    fontSize = 26.sp,
                    fontFamily = FontFamily.Monospace
                )
                PrsDensityGrid(
                    contacts = snapshot.contacts,
                    selectedContactId = selectedContactId,
                    selectedDisplayName = selected?.displayNameWithCategory(),
                    probeNodes = listOfNotNull(gridProbe),
                    modifier = Modifier.fillMaxHeight().aspectRatio(1f).padding(top = 8.dp)
                )
                Text(
                    "GRID: DENSITY ONLY  /  AZIMUTH: UNAVAILABLE",
                    color = PipGreenDim,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            Column(
                modifier = Modifier
                    .width(360.dp)
                    .fillMaxHeight()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.55f))
                    .background(PipPanel)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text("P.R.S.", color = PipGreen, fontSize = 26.sp, fontFamily = FontFamily.Monospace)
                    Text("${snapshot.contacts.size} NODES", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                }
                Text(mode.subtitle, color = PipNeutralDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "A56: ${if (mode.localScannerEnabled) scanStatus.name.replace('_', ' ') else "STANDBY"}",
                        color = scanStatusColor(scanStatus),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        "EVAL ${PrsTuning.DEFAULT.evaluationIntervalMillis / 1_000}s",
                        color = PipGreenDim,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                if (mode.probeEnabled) {
                    Text(
                        "PROBE: ${probeNode.state}",
                        color = probeStatusColor(probeNode.state),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        probeLinkStatus,
                        color = PipGreenDim,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    probeNode.location?.let { location ->
                        Text(
                            "PROBE FIX: ±${formatRssi(location.accuracyMeters)} m  BAT ${location.batteryPercent?.toString() ?: "--"}%",
                            color = PipNeutralDim,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } ?: Text(
                        "PROBE FIX: WAITING FOR LOCATION",
                        color = PipAmber,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 7.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(panelScroll),
                        verticalArrangement = Arrangement.spacedBy(7.dp)
                    ) {
                        Text("CONTACT LIST // ALL NODES", color = PipAmber, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
                        if (snapshot.contacts.isEmpty()) {
                            Text("WAITING FOR BLE ADVERTISEMENTS...", color = PipGreenDim, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        } else {
                            snapshot.contacts.forEach { contact ->
                                ContactListRow(
                                    contact = contact,
                                    selected = contact.contactId == selectedContactId,
                                    onClick = {
                                        if (contact.contactId == selectedContactId) {
                                            onClearTarget()
                                        } else {
                                            onSelectContact(contact.contactId)
                                        }
                                    }
                                )
                            }
                        }

                        selected?.let { contact ->
                            PrsTargetDetails(contact = contact, onClearTarget = onClearTarget)
                        }
                    }
                }

                if (selected != null || (mode.probeEnabled && probeNode.state != "ACTIVE")) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        if (selected != null) {
                            PrsButton("> STOP TRACKING", onClick = onClearTarget, modifier = Modifier.weight(1f))
                        }
                        if (mode.probeEnabled && probeNode.state != "ACTIVE") {
                            PrsButton("> RETRY PROBE LINK", onClick = onRetry, modifier = Modifier.weight(1f))
                        }
                    }
                }

                val retryNeeded = scanStatus == BleScanStatus.PERMISSION_REQUIRED ||
                    scanStatus == BleScanStatus.BLUETOOTH_OFF ||
                    scanStatus == BleScanStatus.ERROR
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    if (retryNeeded) {
                        when (scanStatus) {
                            BleScanStatus.PERMISSION_REQUIRED -> PrsButton("> ALLOW BLUETOOTH", onClick = onGrantPermission, modifier = Modifier.weight(1f))
                            else -> PrsButton("> TRY AGAIN", onClick = onRetry, modifier = Modifier.weight(1f))
                        }
                    }
                    PrsButton(
                        "> CLEAR CONTACTS",
                        onClick = onClear,
                        modifier = Modifier.weight(if (retryNeeded) 1f else 2f)
                    )
                }
            }
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
    }
}

@Composable
private fun PrsCompactTrackingContent(
    page: PrsCompactPage,
    snapshot: PrsSnapshot,
    scanStatus: BleScanStatus,
    gridProbe: PrsGridProbe?,
    selectedContactId: String?,
    onSelectContact: (String) -> Unit,
    onClearTarget: () -> Unit,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onPageSelected: (PrsCompactPage) -> Unit,
    onDevicesSelected: () -> Unit
) {
    val selected = snapshot.contact(selectedContactId)
    val retryNeeded = scanStatus == BleScanStatus.PERMISSION_REQUIRED ||
        scanStatus == BleScanStatus.BLUETOOTH_OFF ||
        scanStatus == BleScanStatus.ERROR

    Box(modifier = Modifier.fillMaxSize().background(PipBlack).safeDrawingPadding()) {
        when (page) {
            PrsCompactPage.SCAN -> PrsCompactScanPage(
                snapshot = snapshot,
                scanStatus = scanStatus,
                selectedContactId = selectedContactId,
                retryNeeded = retryNeeded,
                onSelectContact = onSelectContact,
                onGrantPermission = onGrantPermission,
                onRetry = onRetry,
                onClearTarget = onClearTarget,
                onClear = onClear
            )

            PrsCompactPage.GRID -> PrsCompactGridPage(
                snapshot = snapshot,
                scanStatus = scanStatus,
                gridProbe = gridProbe,
                selectedContactId = selectedContactId,
                selectedDisplayName = selected?.displayNameWithCategory()
            )
        }

        PrsCompactNavigation(
            page = page,
            onPageSelected = onPageSelected,
            onDevicesSelected = onDevicesSelected,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(10.dp)
        )
    }
}

@Composable
private fun PrsCompactScanPage(
    snapshot: PrsSnapshot,
    scanStatus: BleScanStatus,
    selectedContactId: String?,
    retryNeeded: Boolean,
    onSelectContact: (String) -> Unit,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onClearTarget: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 76.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text("SCAN", color = PipGreen, fontSize = 21.sp, fontFamily = FontFamily.Monospace)
            Text("${snapshot.contacts.size} NODES", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        }
        Text(
            "A56: ${scanStatus.name.replace('_', ' ')}",
            color = scanStatusColor(scanStatus),
            fontSize = 9.sp,
            fontFamily = FontFamily.Monospace
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .border(1.dp, PipGreenDim.copy(alpha = 0.45f))
                .padding(6.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (snapshot.contacts.isEmpty()) {
                Text(
                    "WAITING FOR BLE ADVERTISEMENTS...",
                    color = PipGreenDim,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            } else {
                snapshot.contacts.forEach { contact ->
                    CompactContactListRow(
                        contact = contact,
                        selected = contact.contactId == selectedContactId,
                        onClick = {
                            if (contact.contactId == selectedContactId) onClearTarget()
                            else onSelectContact(contact.contactId)
                        }
                    )
                }
            }
        }
        CompactActionRow(
            retryNeeded = retryNeeded,
            permissionRequired = scanStatus == BleScanStatus.PERMISSION_REQUIRED,
            selected = selectedContactId != null,
            onGrantPermission = onGrantPermission,
            onRetry = onRetry,
            onClearTarget = onClearTarget,
            onClear = onClear
        )
    }
}

@Composable
private fun PrsCompactGridPage(
    snapshot: PrsSnapshot,
    scanStatus: BleScanStatus,
    gridProbe: PrsGridProbe?,
    selectedContactId: String?,
    selectedDisplayName: String?
) {
    Box(modifier = Modifier.fillMaxSize().padding(bottom = 52.dp)) {
        PrsDensityGrid(
            contacts = snapshot.contacts,
            selectedContactId = selectedContactId,
            selectedDisplayName = selectedDisplayName,
            probeNodes = listOfNotNull(gridProbe),
            showFrame = false,
            modifier = Modifier.fillMaxSize()
        )

        if (scanStatus != BleScanStatus.SCANNING) {
            Text(
                "A56: ${scanStatus.name.replace('_', ' ')}",
                color = scanStatusColor(scanStatus),
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
            )
        }
    }
}

@Composable
private fun CompactActionRow(
    retryNeeded: Boolean,
    permissionRequired: Boolean,
    selected: Boolean,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onClearTarget: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (retryNeeded) {
            PrsButton(
                if (permissionRequired) "> ALLOW" else "> RETRY",
                onClick = if (permissionRequired) onGrantPermission else onRetry,
                modifier = Modifier.weight(1f)
            )
        }
        if (selected) {
            PrsButton(
                "> STOP",
                onClick = onClearTarget,
                modifier = Modifier.weight(1f)
            )
        }
        PrsButton(
            "> CLEAR",
            onClick = onClear,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PrsCompactNavigation(
    page: PrsCompactPage,
    onPageSelected: (PrsCompactPage) -> Unit,
    onDevicesSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(PipBlack)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CompactNavigationItem("SCAN", page == PrsCompactPage.SCAN) {
            onPageSelected(PrsCompactPage.SCAN)
        }
        CompactNavigationItem("GRID", page == PrsCompactPage.GRID) {
            onPageSelected(PrsCompactPage.GRID)
        }
        CompactNavigationItem("DEVICES", selected = false, onClick = onDevicesSelected)
    }
}

@Composable
private fun CompactNavigationItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = "> $label",
        color = if (selected) PipAmber else PipGreenDim,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 2.dp, vertical = 2.dp)
    )
}

@Composable
private fun CompactContactListRow(
    contact: PrsContactSnapshot,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = if (selected) PipAmber else PipGreenDim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = if (selected) 0.9f else 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 9.dp, vertical = 9.dp)
    ) {
        Text(
            contact.displayNameWithCategory(),
            color = accent,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ContactListRow(
    contact: PrsContactSnapshot,
    selected: Boolean,
    onClick: () -> Unit
) {
    val accent = if (selected) PipAmber else PipGreenDim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, accent.copy(alpha = if (selected) 0.9f else 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "${contact.source.displayName} / ${contact.displayNameWithCategory()}",
                color = accent,
                fontSize = 15.sp,
                fontFamily = FontFamily.Monospace,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(contact.inference.trend.displayLabel(), color = trendColor(contact.inference.trend), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
        }
        Text(
            "RAW ${contact.measured.rssi}   SMOOTH ${formatRssi(contact.processed.smoothedRssi)}   ${contact.inference.proximity.displayLabel()}",
            color = PipNeutralDim,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun PrsTargetDetails(contact: PrsContactSnapshot, onClearTarget: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipAmber.copy(alpha = 0.75f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TRACK TARGET", color = PipAmber, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            Text("[STOP TRACKING]", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.clickable(onClick = onClearTarget))
        }
        Text(contact.displayNameWithCategory(), color = PipNeutral, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text("SOURCE: ${contact.source.displayName}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Text("ID: ${contact.contactId}", color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        TargetValue("SIGNAL RAW", "${contact.measured.rssi} dBm")
        TargetValue("SIGNAL SMOOTHED", "${formatRssi(contact.processed.smoothedRssi)} dBm")
        TargetValue("TREND", contact.inference.trend.displayLabel())
        TargetValue("PROXIMITY", "${contact.inference.proximity.displayLabel()} / RELATIVE")
        Text("DIRECTION: NOT MEASURED", color = PipAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Text(contact.inference.explanation, color = PipNeutralDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
        Text("RECENT RSSI", color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        contact.processed.history.takeLast(PrsTuning.DEFAULT.historyWindowSize).forEach { point ->
            Text(
                "${point.observedAtElapsedMillis}  RAW ${point.rawRssi}  SMOOTH ${formatRssi(point.smoothedRssi)}  Δ ${point.variationFromPreviousDb?.let(::formatRssi) ?: "--"}",
                color = PipNeutralDim,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
private fun TargetValue(label: String, value: String) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PipGreenDim, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
        Text(value, color = PipNeutral, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

private fun PrsContactSnapshot.displayNameWithCategory(): String =
    "$displayName${measured.categorySuffix()}"

@Composable
private fun PrsButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text,
        textAlign = TextAlign.Center,
        color = PipNeutral,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth().background(PipActionBackground).clickable(onClick = onClick).padding(horizontal = 5.dp, vertical = 4.dp)
    )
}

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

private fun formatRssi(value: Float): String = "%.1f".format(java.util.Locale.US, value)

private fun trendColor(trend: PrsTrend): Color = when (trend) {
    PrsTrend.APPROACHING -> PipGreen
    PrsTrend.MOVING_AWAY -> PipAmber
    PrsTrend.STABLE -> PipNeutralDim
    PrsTrend.INSUFFICIENT_DATA -> PipGreenDim
}

private fun scanStatusColor(status: BleScanStatus): Color = when (status) {
    BleScanStatus.SCANNING -> PipGreen
    BleScanStatus.PERMISSION_REQUIRED,
    BleScanStatus.BLUETOOTH_OFF,
    BleScanStatus.ERROR -> PipAmber
    else -> PipGreenDim
}

private fun probeStatusColor(status: String): Color = when (status.uppercase()) {
    "ACTIVE" -> PipBlue
    "ERROR", "DISCONNECTED", "STOPPED" -> PipAmber
    else -> PipGreenDim
}

internal fun probeGridPosition(
    phoneFix: TerrainLocationFix?,
    probeNode: PrsProbeNodeSnapshot,
    mode: PrsOperatingMode
): PrsGridProbe? {
    if (!mode.probeEnabled) return null
    val probeLocation = probeNode.location ?: return null
    val phonePoint = phoneFix?.point ?: return null
    val meanLatitudeRadians = ((phonePoint.latitude + probeLocation.latitude) / 2.0) * PI / 180.0
    val eastMeters = (probeLocation.longitude - phonePoint.longitude) * 111_320.0 * cos(meanLatitudeRadians)
    val northMeters = (probeLocation.latitude - phonePoint.latitude) * 110_540.0
    val halfSpan = PrsProbeDisplayTuning.GRID_HALF_SPAN_METERS
    val age = (System.currentTimeMillis() - probeLocation.timestampEpochMillis).coerceAtLeast(0L)
    return PrsGridProbe(
        label = "WATCH 2",
        xFraction = (0.5 + eastMeters / (2.0 * halfSpan)).toFloat().coerceIn(0.08f, 0.92f),
        yFraction = (0.5 - northMeters / (2.0 * halfSpan)).toFloat().coerceIn(0.08f, 0.92f),
        accuracyMeters = probeLocation.accuracyMeters,
        state = if (age > PrsProbeDisplayTuning.MAX_LOCATION_AGE_MILLIS) "STALE" else probeNode.state,
        observationCount = probeNode.observationCount
    )
}

internal fun prsPermissions(): Array<String> = arrayOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.ACCESS_FINE_LOCATION
)
