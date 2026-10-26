package com.suri.pipsurios.ui.screens

import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suri.pipsurios.PipSuriOsVersion
import com.suri.pipsurios.R
import com.suri.pipsurios.detector.RadarBleClient
import com.suri.pipsurios.detector.RadarConnectionState
import com.suri.pipsurios.detector.RadarPacketCodec
import com.suri.pipsurios.detector.RadarTarget
import com.suri.pipsurios.detector.SequenceTracker
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin
import kotlinx.coroutines.delay

private const val RADAR_BEAM_DEGREES = 120f
private const val MAX_RANGE_METERS = 6
private const val MAX_RANGE_MM = 6000f
private const val CURVE_STEPS = 48
private const val RECEIVING_TIMEOUT_MILLIS = 1_500L

/** Detector real para el ESP32 SuriOS Radar con sensor HLK-LD2450. */
@Composable
fun DetectorScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val client = remember(context) { RadarBleClient(context) }
    val status by client.status.collectAsStateWithLifecycle()
    val sequenceTracker = remember { SequenceTracker() }
    var permissionVersion by remember { mutableIntStateOf(0) }
    var targets by remember { mutableStateOf(emptyList<RadarTarget>()) }
    var lastSequence by remember { mutableStateOf<Int?>(null) }
    var lostPackets by remember { mutableIntStateOf(0) }
    var lastPacketAt by remember { mutableStateOf<Long?>(null) }
    var invalidPackets by remember { mutableIntStateOf(0) }
    var clock by remember { mutableLongStateOf(SystemClock.elapsedRealtime()) }
    var ecoMode by remember { mutableStateOf(false) }
    var controlMessage by remember { mutableStateOf<String?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionVersion++ }

    LaunchedEffect(Unit) {
        if (!client.hasRequiredPermissions()) permissionLauncher.launch(prsPermissions())
    }

    LaunchedEffect(client, permissionVersion) {
        if (client.hasRequiredPermissions()) client.start()
    }

    LaunchedEffect(Unit) {
        while (true) {
            clock = SystemClock.elapsedRealtime()
            delay(250L)
        }
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
                lostPackets += observation.missingPackets
                lastPacketAt = SystemClock.elapsedRealtime()
            }
        }
    }

    DisposableEffect(client) {
        onDispose { client.close() }
    }

    val packetAge = lastPacketAt?.let { clock - it }
    val receiving = packetAge != null && packetAge <= RECEIVING_TIMEOUT_MILLIS
    val displayStatus = when {
        ecoMode -> "ECO // BLE"
        receiving -> "RECIBIENDO"
        status.state == RadarConnectionState.CONNECTED -> "CONECTADO"
        else -> status.state.label()
    }
    val displayColor = when {
        receiving -> PipGreen
        status.state == RadarConnectionState.ERROR -> PipRed
        else -> PipAmber
    }

    Box(Modifier.fillMaxSize().background(PipBlack)) {
        RadarBackdrop(Modifier.fillMaxSize())
        RadarCone(
            targets = targets,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 54.dp)
        )
        TerminalScanline(Modifier.fillMaxSize())

        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text("SURIOS RADAR", color = PipGreen, fontFamily = FontFamily.Monospace, fontSize = 16.sp)
            Text(
                "ESP32 // LD2450 // $displayStatus",
                color = displayColor,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
            if (status.detail != null && status.state != RadarConnectionState.CONNECTED) {
                Text(status.detail!!, color = PipGreenDim, fontFamily = FontFamily.Monospace, fontSize = 8.sp)
            }
        }

        if (invalidPackets > 0) {
            Text(
                "PAQUETES INVALIDOS $invalidPackets",
                color = PipAmber,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 66.dp)
            )
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            RadarControlButton("ECO", selected = ecoMode) {
                if (client.setEcoMode(true)) {
                    ecoMode = true
                    controlMessage = "ECO_BLE"
                } else {
                    controlMessage = "CONTROL_NO_DISPONIBLE"
                }
            }
            RadarControlButton("NORMAL", selected = !ecoMode) {
                if (client.setEcoMode(false)) {
                    ecoMode = false
                    controlMessage = "NORMAL"
                } else {
                    controlMessage = "CONTROL_NO_DISPONIBLE"
                }
            }
        }
        if (controlMessage != null) {
            Text(
                controlMessage!!,
                color = PipGreenDim,
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp)
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
        Text(
            PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .safeDrawingPadding()
                .padding(24.dp)
        )
    }
}

@Composable
private fun RadarControlButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = "[ $label ]",
        color = if (selected) PipGreen else PipGreenDim,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        modifier = Modifier
            .width(120.dp)
            .border(1.dp, if (selected) PipGreen else PipGreenDim)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    )
}

@Composable
private fun RadarBackdrop(modifier: Modifier) {
    BoxWithConstraints(modifier) {
        val emblemSize = min(maxWidth.value, maxHeight.value) * 0.66f
        Image(
            painter = painterResource(R.drawable.brotherhood_emblem_pipgreen),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            alpha = 0.08f,
            modifier = Modifier.align(Alignment.Center).size(emblemSize.dp)
        )
        Canvas(Modifier.fillMaxSize()) {
            val frame = 10.dp.toPx()
            drawRect(
                color = PipGreenDim.copy(alpha = 0.28f),
                topLeft = Offset(frame, frame),
                size = androidx.compose.ui.geometry.Size(size.width - frame * 2, size.height - frame * 2),
                style = Stroke(width = 1.dp.toPx())
            )
            drawLine(
                PipGreenDim.copy(alpha = 0.2f),
                Offset(size.width / 2f, frame),
                Offset(size.width / 2f, size.height - frame),
                1f
            )
        }
    }
}

@Composable
private fun TerminalScanline(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "detector_scanline")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "detector_scanline_progress"
    )
    Canvas(modifier) {
        val y = size.height * progress
        drawLine(
            color = PipGreen.copy(alpha = 0.25f),
            start = Offset(size.width * 0.04f, y),
            end = Offset(size.width * 0.96f, y),
            strokeWidth = 2f
        )
    }
}

@Composable
private fun RadarCone(targets: List<RadarTarget>, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "detector_radar_wave")
    val waveFraction by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "detector_radar_wave_progress"
    )

    Canvas(modifier) {
        val edgeMargin = 6.dp.toPx()
        val halfBeam = RADAR_BEAM_DEGREES / 2f
        val origin = Offset(size.width / 2f, size.height - edgeMargin)
        val widthLimit = (size.width / 2f - edgeMargin) / sin(Math.toRadians(halfBeam.toDouble())).toFloat()
        val displayRadius = min(widthLimit, origin.y - edgeMargin)
        val leftEnd = pointAtRange(origin, displayRadius, -halfBeam, 1f)
        val rightEnd = pointAtRange(origin, displayRadius, halfBeam, 1f)
        val fill = Path().apply {
            moveTo(origin.x, origin.y)
            lineTo(leftEnd.x, leftEnd.y)
            addPath(rangeCurve(origin, displayRadius, 1f, halfBeam))
            lineTo(origin.x, origin.y)
            close()
        }

        drawPath(fill, PipBlue.copy(alpha = 0.09f))
        for (meter in 1..MAX_RANGE_METERS) {
            val fraction = meter / MAX_RANGE_METERS.toFloat()
            drawPath(
                path = rangeCurve(origin, displayRadius, fraction, halfBeam),
                color = PipBlue.copy(alpha = if (meter == MAX_RANGE_METERS) 0.9f else 0.48f),
                style = Stroke(width = if (meter == MAX_RANGE_METERS) 1.5f else 1f)
            )
        }
        drawLine(PipBlue, origin, leftEnd, strokeWidth = 1.5f, cap = StrokeCap.Round)
        drawLine(PipBlue, origin, rightEnd, strokeWidth = 1.5f, cap = StrokeCap.Round)
        drawLine(PipGreenDim, origin, pointAtRange(origin, displayRadius, 0f, 1f), 1.5f, StrokeCap.Round)
        drawPath(
            path = rangeCurve(origin, displayRadius, waveFraction.coerceAtLeast(0.02f), halfBeam),
            color = PipGreen.copy(alpha = 0.9f),
            style = Stroke(width = 2.5f)
        )
        for (meter in 1 until MAX_RANGE_METERS) {
            val fraction = meter / MAX_RANGE_METERS.toFloat()
            drawRangeTick(origin, leftEnd, fraction)
            drawRangeTick(origin, rightEnd, fraction)
        }

        val marker = Path().apply {
            moveTo(origin.x, origin.y - 9f)
            lineTo(origin.x - 6f, origin.y + 3f)
            lineTo(origin.x + 6f, origin.y + 3f)
            close()
        }
        drawPath(marker, PipGreen)

        targets.filter { target ->
            val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
            val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
            target.visible && target.yMm >= 0 && distance <= MAX_RANGE_MM && abs(bearing) <= halfBeam
        }.forEach { target ->
            val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
            val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
            drawCircle(
                color = PipRed,
                radius = 5f,
                center = pointAtRange(origin, displayRadius, bearing, distance / MAX_RANGE_MM)
            )
        }
    }
}

private fun rangeCurve(origin: Offset, displayRadius: Float, fraction: Float, halfBeam: Float): Path {
    val path = Path()
    for (index in 0..CURVE_STEPS) {
        val bearing = -halfBeam + RADAR_BEAM_DEGREES * index / CURVE_STEPS
        val point = pointAtRange(origin, displayRadius, bearing, fraction)
        if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    return path
}

private fun pointAtRange(origin: Offset, displayRadius: Float, bearingDegrees: Float, fraction: Float): Offset {
    val angle = Math.toRadians(bearingDegrees.toDouble())
    val direction = Offset(sin(angle).toFloat(), -cos(angle).toFloat())
    val distance = displayRadius * fraction
    return Offset(origin.x + direction.x * distance, origin.y + direction.y * distance)
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRangeTick(
    origin: Offset,
    end: Offset,
    fraction: Float
) {
    val point = Offset(
        origin.x + (end.x - origin.x) * fraction,
        origin.y + (end.y - origin.y) * fraction
    )
    val length = hypot(end.x - origin.x, end.y - origin.y)
    val normal = Offset(-(end.y - origin.y) / length * 4f, (end.x - origin.x) / length * 4f)
    drawLine(
        PipBlue,
        Offset(point.x - normal.x, point.y - normal.y),
        Offset(point.x + normal.x, point.y + normal.y),
        1.5f,
        StrokeCap.Round
    )
}

private fun RadarConnectionState.label(): String = when (this) {
    RadarConnectionState.DISCONNECTED -> "DESCONECTADO"
    RadarConnectionState.SCANNING -> "BUSCANDO"
    RadarConnectionState.CONNECTING -> "CONECTANDO"
    RadarConnectionState.CONNECTED -> "CONECTADO"
    RadarConnectionState.ERROR -> "ERROR"
}

private fun Long.formatAge(): String = if (this < 1_000L) "0s" else String.format(Locale.US, "%.1fs", this / 1_000f)
