package com.suri.pipsurios.ui.screens

import android.Manifest
import android.os.Build
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.remoteprobe.RemoteProbeSession
import com.suri.pipsurios.remoteprobe.RemoteProbeSnapshot
import com.suri.pipsurios.remoteprobe.RemoteProbeLink
import com.suri.pipsurios.sonar.BleScanStatus
import com.suri.pipsurios.sonar.BleScanner
import com.suri.pipsurios.sonar.PresenceScanPhase
import com.suri.pipsurios.sonar.PresenceScanSnapshot
import com.suri.pipsurios.sonar.PresenceScannerSession
import com.suri.pipsurios.sonar.PresenceSignalPoint
import com.suri.pipsurios.sonar.PresenceTuning
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun ProximityPresenceV2Screen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scanner = remember(context) { BleScanner(context.applicationContext) }
    val session = remember { PresenceScannerSession() }
    val remoteProbe = remember(context) { RemoteProbeSession(context.applicationContext) }
    var snapshot by remember { mutableStateOf(session.snapshot()) }
    var remoteSnapshot by remember { mutableStateOf(remoteProbe.snapshot()) }
    var scanStatus by remember { mutableStateOf(BleScanStatus.IDLE) }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var retryVersion by remember { mutableIntStateOf(0) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionVersion++
        retryVersion++
    }

    LaunchedEffect(Unit) {
        if (!scanner.hasRequiredPermissions() || !hasLocalNetworkPermission(context)) {
            permissionLauncher.launch(presencePermissions())
        }
    }

    DisposableEffect(permissionVersion, retryVersion) {
        scanStatus = scanner.start(
            onObservation = { observation ->
                remoteProbe.observeOperator(observation)
                session.observe(observation)
                snapshot = session.snapshot(SystemClock.elapsedRealtime())
            },
            onStatusChanged = { scanStatus = it }
        )
        onDispose { scanner.stop() }
    }

    DisposableEffect(scanner) {
        onDispose { scanner.releaseSession() }
    }

    LaunchedEffect(remoteProbe, permissionVersion) {
        if (!scanner.hasRequiredPermissions() || !hasLocalNetworkPermission(context)) return@LaunchedEffect
        remoteProbe.start()
        while (true) {
            remoteSnapshot = remoteProbe.snapshot()
            delay(500L)
        }
    }

    DisposableEffect(remoteProbe) {
        onDispose { remoteProbe.stop() }
    }

    LaunchedEffect(snapshot.phase) {
        when (snapshot.phase) {
            PresenceScanPhase.REFERENCE -> {
                delay(PresenceTuning.CLOSE_DURATION_MILLIS)
                session.finishReference(SystemClock.elapsedRealtime())
                snapshot = session.snapshot(SystemClock.elapsedRealtime())
            }
            PresenceScanPhase.DOOR -> {
                delay(PresenceTuning.WIDE_DURATION_MILLIS)
                session.finishDoor(SystemClock.elapsedRealtime())
                snapshot = session.snapshot(SystemClock.elapsedRealtime())
            }
            PresenceScanPhase.IDLE, PresenceScanPhase.COMPLETE -> Unit
        }
    }

    LaunchedEffect(snapshot.phase) {
        while (snapshot.phase == PresenceScanPhase.REFERENCE || snapshot.phase == PresenceScanPhase.DOOR) {
            delay(250L)
            snapshot = session.snapshot(SystemClock.elapsedRealtime())
        }
    }

    fun startCloseScan() {
        session.startReference(SystemClock.elapsedRealtime())
        snapshot = session.snapshot(SystemClock.elapsedRealtime())
    }

    fun startWideScan() {
        session.startDoor(SystemClock.elapsedRealtime())
        snapshot = session.snapshot(SystemClock.elapsedRealtime())
    }

    fun resetSurvey() {
        session.reset()
        snapshot = session.snapshot(SystemClock.elapsedRealtime())
    }

    ProximityPresenceV2Content(
        snapshot = snapshot,
        remoteProbeSnapshot = remoteSnapshot,
        scanStatus = scanStatus,
        onStartClose = ::startCloseScan,
        onStartWide = ::startWideScan,
        onReset = ::resetSurvey,
        onGrantPermission = { permissionLauncher.launch(presencePermissions()) },
        onRetry = { retryVersion++ },
        onBack = onBack
    )
}

@Composable
private fun ProximityPresenceV2Content(
    snapshot: PresenceScanSnapshot,
    remoteProbeSnapshot: RemoteProbeSnapshot,
    scanStatus: BleScanStatus,
    onStartClose: () -> Unit,
    onStartWide: () -> Unit,
    onReset: () -> Unit,
    onGrantPermission: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    val phaseText = when (snapshot.phase) {
        PresenceScanPhase.REFERENCE -> "CLOSE RANGE SCAN"
        PresenceScanPhase.DOOR -> "WIDE RANGE SCAN"
        PresenceScanPhase.COMPLETE -> "SCAN COMPLETE"
        PresenceScanPhase.IDLE -> if (snapshot.referenceComplete) "READY FOR WIDE SCAN" else "READY"
    }

    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Row(
            modifier = Modifier.align(Alignment.Center).fillMaxWidth().padding(horizontal = 64.dp),
            horizontalArrangement = Arrangement.spacedBy(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                PresenceSignalGrid(
                    points = snapshot.signalPoints,
                    probeConnected = remoteProbeSnapshot.link == RemoteProbeLink.CONNECTED,
                    modifier = Modifier.fillMaxWidth().height(390.dp)
                )
            }

            Column(modifier = Modifier.width(300.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("P.R.S.", color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace)
                Text(phaseText, color = Color.LightGray, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    PresenceCounter("CLOSE", snapshot.referenceDeviceCount, Modifier.weight(1f))
                    PresenceCounter("NEW", snapshot.newDeviceCount, Modifier.weight(1f))
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NodeLegend(PipGreen, "A56")
                    NodeLegend(PipBlue, "WATCH 2")
                }
                Text(
                    "PROBE: ${remoteProbeSnapshot.link.name}",
                    color = if (remoteProbeSnapshot.link.name == "CONNECTED") PipGreen else PipGreenDim,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    "A56: ${remoteProbeSnapshot.operatorContactCount}  WATCH: ${remoteProbeSnapshot.probeContactCount}",
                    color = PipGreenDim,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    "TOTAL: ${remoteProbeSnapshot.combinedContactCount}  MATCHED: ${remoteProbeSnapshot.matchedContactCount}",
                    color = PipGreenDim,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    "BLUE DOT = LINK NODE, NOT LOCATION",
                    color = PipGreenDim,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                when (scanStatus) {
                    BleScanStatus.PERMISSION_REQUIRED -> PresenceButton("> ALLOW BLUETOOTH", onClick = onGrantPermission)
                    BleScanStatus.BLUETOOTH_OFF, BleScanStatus.ERROR -> PresenceButton("> TRY AGAIN", onClick = onRetry)
                    else -> when (snapshot.phase) {
                        PresenceScanPhase.REFERENCE -> PresenceButton("> CLOSE SCAN ${remainingSeconds(snapshot, PresenceTuning.CLOSE_DURATION_MILLIS)}s", enabled = false)
                        PresenceScanPhase.DOOR -> PresenceButton("> WIDE SCAN ${remainingSeconds(snapshot, PresenceTuning.WIDE_DURATION_MILLIS)}s", enabled = false)
                        PresenceScanPhase.IDLE -> if (snapshot.referenceComplete) {
                            PresenceButton("> START WIDE SCAN", onClick = onStartWide)
                        } else {
                            PresenceButton("> START CLOSE SCAN", onClick = onStartClose)
                        }
                        PresenceScanPhase.COMPLETE -> PresenceButton("> NEW SCAN", onClick = onStartClose)
                    }
                }
                PresenceButton("> RESET", onClick = onReset)
            }
        }

        Text(
            "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).clickable(onClick = onBack).padding(24.dp)
        )
    }
}

/** A stable, approximate signal map; the centre is the phone and the radius is signal strength. */
@Composable
private fun PresenceSignalGrid(
    points: List<PresenceSignalPoint>,
    probeConnected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.border(1.dp, PipGreenDim).background(Color(0xFF06130C))) {
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val gridColor = PipGreenDim.copy(alpha = 0.32f)
            val columns = 6
            val rows = 4
            repeat(columns + 1) { index ->
                val x = size.width * index / columns
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            }
            repeat(rows + 1) { index ->
                val y = size.height * index / rows
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            }

            // Subtle CRT scanlines and corner brackets give the map its retro terminal feel.
            var scanlineY = 0f
            while (scanlineY < size.height) {
                drawLine(PipGreenDim.copy(alpha = 0.08f), Offset(0f, scanlineY), Offset(size.width, scanlineY), strokeWidth = 1f)
                scanlineY += 6.dp.toPx()
            }
            val bracket = 18.dp.toPx()
            val corners = listOf(
                Offset(0f, 0f) to 1,
                Offset(size.width, 0f) to 2,
                Offset(0f, size.height) to 3,
                Offset(size.width, size.height) to 4
            )
            corners.forEach { (corner, kind) ->
                val xDirection = if (kind == 2 || kind == 4) -1f else 1f
                val yDirection = if (kind == 3 || kind == 4) -1f else 1f
                drawLine(PipGreen, corner, corner + Offset(bracket * xDirection, 0f), strokeWidth = 2f)
                drawLine(PipGreen, corner, corner + Offset(0f, bracket * yDirection), strokeWidth = 2f)
            }

            val centre = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = min(size.width, size.height) * 0.46f
            listOf(0.25f, 0.50f, 0.75f, 1.0f).forEach { fraction ->
                drawCircle(
                    PipGreenDim.copy(alpha = 0.24f),
                    radius = maxRadius * fraction,
                    center = centre,
                    style = Stroke(1.dp.toPx())
                )
            }
            points.forEach { point ->
                val angle = Math.toRadians(point.angleDegrees.toDouble())
                val radius = maxRadius * point.distanceFraction
                val position = Offset(
                    centre.x + cos(angle).toFloat() * radius,
                    centre.y + sin(angle).toFloat() * radius
                )
                val colour = if (point.isNew) PipAmber else PipGreenDim
                drawCircle(colour.copy(alpha = 0.12f), radius = 18.dp.toPx(), center = position)
                drawCircle(colour.copy(alpha = 0.45f), radius = 10.dp.toPx(), center = position, style = Stroke(1.dp.toPx()))
                drawCircle(colour, radius = 4.dp.toPx(), center = position)
            }
            drawCircle(PipGreen, radius = 6.dp.toPx(), center = centre)

            // The probe marker identifies the connected remote node only. Its fixed
            // corner position is deliberately not a claimed physical coordinate.
            val probeMarker = Offset(24.dp.toPx(), 24.dp.toPx())
            val probeColour = if (probeConnected) PipBlue else PipBlue.copy(alpha = 0.35f)
            drawCircle(probeColour.copy(alpha = 0.12f), radius = 18.dp.toPx(), center = probeMarker)
            drawCircle(probeColour.copy(alpha = 0.45f), radius = 10.dp.toPx(), center = probeMarker, style = Stroke(1.dp.toPx()))
            drawCircle(probeColour, radius = 5.dp.toPx(), center = probeMarker)
        }
    }
}

private fun remainingSeconds(snapshot: PresenceScanSnapshot, durationMillis: Long): Long =
    ((durationMillis - snapshot.phaseElapsedMillis).coerceAtLeast(0L) + 999L) / 1_000L

@Composable
private fun NodeLegend(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("●", color = color, fontSize = 17.sp, fontFamily = FontFamily.Monospace)
        Text(label, color = Color.LightGray, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PresenceCounter(label: String, value: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .border(1.dp, PipGreenDim)
            .background(Color(0xFF071A10))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = PipGreenDim, fontSize = 13.sp, fontFamily = FontFamily.Monospace)
        Text(value.toString(), color = Color.White, fontSize = 28.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun PresenceButton(text: String, enabled: Boolean = true, onClick: () -> Unit = {}) {
    Text(
        text,
        color = if (enabled) Color.White else Color.Gray,
        fontSize = 17.sp,
        fontFamily = FontFamily.Monospace,
        modifier = (if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .background(if (enabled) Color(0xFF303030) else Color(0xFF202020))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

private fun presencePermissions(): Array<String> = buildList {
    add(Manifest.permission.BLUETOOTH_SCAN)
    add(Manifest.permission.BLUETOOTH_CONNECT)
    add(Manifest.permission.ACCESS_FINE_LOCATION)
    if (Build.VERSION.SDK_INT >= 37) add("android.permission.ACCESS_LOCAL_NETWORK")
}.toTypedArray()

private fun hasLocalNetworkPermission(context: android.content.Context): Boolean =
    Build.VERSION.SDK_INT < 37 ||
        context.checkSelfPermission("android.permission.ACCESS_LOCAL_NETWORK") ==
        android.content.pm.PackageManager.PERMISSION_GRANTED
