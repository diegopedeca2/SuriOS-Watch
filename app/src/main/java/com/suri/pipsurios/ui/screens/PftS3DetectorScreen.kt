package com.suri.pipsurios.ui.screens

import android.Manifest
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suri.pipsurios.detector.RadarPacketCodec
import com.suri.pipsurios.detector.RadarTarget
import com.suri.pipsurios.detector.SequenceTracker
import com.suri.pipsurios.pft.PftS3BleClient
import com.suri.pipsurios.pft.PftS3ConnectionState
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import kotlinx.coroutines.delay
import kotlin.math.hypot
import kotlin.math.min

private val PFT_S3_DETECTOR_PERMISSIONS = arrayOf(
    Manifest.permission.BLUETOOTH_SCAN,
    Manifest.permission.BLUETOOTH_CONNECT,
    Manifest.permission.ACCESS_FINE_LOCATION
)

private const val PFT_S3_DETECTOR_MAX_RANGE_MM = 6_000f
private const val PFT_S3_DETECTOR_TIMEOUT_MILLIS = 1_500L

/** DETECTOR local usando radar WROOM -> UART -> S3 -> BLE -> A56. */
@Composable
fun PftS3DetectorScreen(onBack: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val client = remember(context) { PftS3BleClient(context) }
    val status by client.status.collectAsStateWithLifecycle()
    val sequenceTracker = remember { SequenceTracker() }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var targets by remember { mutableStateOf(emptyList<RadarTarget>()) }
    var lastSequence by remember { mutableStateOf<Int?>(null) }
    var missingPackets by remember { mutableIntStateOf(0) }
    var invalidPackets by remember { mutableIntStateOf(0) }
    var lastPacketAt by remember { mutableLongStateOf(0L) }
    var clock by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionVersion++ }

    LaunchedEffect(Unit) {
        if (!client.hasRequiredPermissions()) {
            permissionLauncher.launch(PFT_S3_DETECTOR_PERMISSIONS)
        }
    }

    LaunchedEffect(client, permissionVersion) {
        if (client.hasRequiredPermissions()) client.start()
    }

    LaunchedEffect(client) {
        client.packets.collect { bytes ->
            val packet = runCatching { RadarPacketCodec.decode(bytes) }.getOrNull()
            if (packet == null) {
                invalidPackets++
                return@collect
            }
            val observation = sequenceTracker.observe(packet.sequence)
            if (observation.accepted) {
                targets = packet.targets
                lastSequence = packet.sequence
                missingPackets += observation.missingPackets
                lastPacketAt = SystemClock.elapsedRealtime()
            }
        }
    }

    LaunchedEffect(status.state) {
        if (status.state == PftS3ConnectionState.CONNECTED) {
            client.sendCommand("TOOL,DETECTOR")
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            clock = SystemClock.elapsedRealtime()
            delay(250L)
        }
    }

    DisposableEffect(client) {
        onDispose { client.close() }
    }

    val packetAge = if (lastPacketAt == 0L) null else clock - lastPacketAt
    val receiving = packetAge != null && packetAge <= PFT_S3_DETECTOR_TIMEOUT_MILLIS
    val visibleTargets = targets.filter { it.visible }
    val displayStatus = when {
        receiving -> "RECIBIENDO"
        status.state == PftS3ConnectionState.CONNECTED -> "CONECTADO"
        else -> status.state.name
    }
    val displayColor = when {
        receiving -> PipGreen
        status.state == PftS3ConnectionState.ERROR -> PipRed
        else -> PipAmber
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
    ) {
        PftS3RadarPlot(
            targets = targets,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp, end = 24.dp, top = 56.dp, bottom = 70.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                "SURIOS DETECTOR // S3",
                color = PipGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 18.sp
            )
            Text(
                "UART WROOM/S3 // BLE // $displayStatus",
                color = displayColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            Text(
                "SEQ ${lastSequence ?: "--"} // TARGETS ${visibleTargets.size} // MISSING $missingPackets",
                color = PipGreenDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 46.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            visibleTargets.forEach { target ->
                Text(
                    "T${target.slot} (${target.xMm},${target.yMm})",
                    color = PipRed,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
            if (visibleTargets.isEmpty()) {
                Text("SIN OBJETIVOS", color = PipGreenDim, fontFamily = FontFamily.Monospace, fontSize = 10.sp)
            }
        }

        if (invalidPackets > 0) {
            Text(
                "PAQUETES INVALIDOS $invalidPackets",
                color = PipAmber,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)
            )
        }

        Text(
            "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .safeDrawingPadding()
                .padding(24.dp)
                .clickable(onClick = onBack)
        )
    }
}

@Composable
private fun PftS3RadarPlot(targets: List<RadarTarget>, modifier: Modifier) {
    Canvas(modifier) {
        val origin = Offset(size.width / 2f, size.height - 10.dp.toPx())
        val radius = min(size.width * 0.44f, size.height * 0.88f)

        for (index in 1..4) {
            val ringRadius = radius * index / 4f
            drawArc(
                topLeft = Offset(origin.x - ringRadius, origin.y - ringRadius),
                size = Size(ringRadius * 2f, ringRadius * 2f),
                startAngle = -150f,
                sweepAngle = 120f,
                useCenter = false,
                color = PipBlue.copy(alpha = if (index == 4) 0.8f else 0.45f),
                style = Stroke(width = if (index == 4) 1.5f else 1f)
            )
        }

        drawLine(
            color = PipBlue,
            start = origin,
            end = Offset(origin.x - radius * 0.866f, origin.y - radius * 0.5f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = PipBlue,
            start = origin,
            end = Offset(origin.x + radius * 0.866f, origin.y - radius * 0.5f),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = PipGreenDim,
            start = origin,
            end = Offset(origin.x, origin.y - radius),
            strokeWidth = 1.5f,
            cap = StrokeCap.Round
        )

        targets.filter { target ->
            target.visible && target.yMm >= 0 &&
                hypot(target.xMm.toFloat(), target.yMm.toFloat()) <= PFT_S3_DETECTOR_MAX_RANGE_MM
        }.forEach { target ->
            val x = origin.x + target.xMm / PFT_S3_DETECTOR_MAX_RANGE_MM * radius
            val y = origin.y - target.yMm / PFT_S3_DETECTOR_MAX_RANGE_MM * radius
            drawCircle(color = PipRed, radius = 6f, center = Offset(x, y))
        }

        drawCircle(color = PipGreen, radius = 5f, center = origin)
        drawRect(color = PipGreenDim.copy(alpha = 0.25f), style = Stroke(width = 1f))
    }
}
