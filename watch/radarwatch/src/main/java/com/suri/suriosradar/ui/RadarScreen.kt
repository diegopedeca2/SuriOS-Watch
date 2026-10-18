package com.suri.suriosradar.ui

import android.graphics.Paint
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.suri.suriosradar.R
import com.suri.suriosradar.RadarDataMode
import com.suri.suriosradar.RadarDisplayState
import com.suri.suriosradar.RadarUiState
import com.suri.suriosradar.RadarViewModel
import com.suri.suriosradar.model.RadarTarget
import com.suri.suriosradar.model.TargetState
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarScreen(
    viewModel: RadarViewModel,
    permissionsGranted: Boolean,
    onRequestPermissions: () -> Unit
) {
    val ui by viewModel.uiState.collectAsStateWithLifecycle()

    BackHandler(enabled = ui.mode != null) {
        viewModel.clearMode()
    }

    MaterialTheme {
        if (ui.mode == null) {
            RadarModeMenu(
                permissionsGranted = permissionsGranted,
                onChooseReal = {
                    viewModel.setMode(RadarDataMode.REAL_BLE)
                    if (!permissionsGranted) onRequestPermissions()
                },
                onChooseSimulated = { viewModel.setMode(RadarDataMode.SIMULATED) }
            )
        } else {
            RadarGridScreen(
                ui = ui,
                permissionsGranted = permissionsGranted,
                onRequestPermissions = onRequestPermissions
            )
        }
    }
}

@Composable
private fun RadarModeMenu(
    permissionsGranted: Boolean,
    onChooseReal: () -> Unit,
    onChooseSimulated: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarPalette.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SURIOS RADAR",
                color = RadarPalette.GreenBright,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "SELECCIONA MODO",
                color = RadarPalette.NeutralDim,
                fontSize = 9.sp
            )
            ModeButton("REAL", RadarPalette.Green, onChooseReal)
            ModeButton("SIM", RadarPalette.Blue, onChooseSimulated)
            if (!permissionsGranted) {
                Text(
                    text = "REAL requiere permiso BLE",
                    color = RadarPalette.NeutralDim,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun ModeButton(label: String, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 116.dp, height = 38.dp)
            .border(1.5.dp, color, RoundedCornerShape(19.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RadarGridScreen(
    ui: RadarUiState,
    permissionsGranted: Boolean,
    onRequestPermissions: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarPalette.Black)
    ) {
        RadarBackdrop(modifier = Modifier.fillMaxSize())

        RadarCone(
            targets = ui.targets,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 10.dp, end = 10.dp, top = 44.dp, bottom = 30.dp)
                .offset(y = (-12).dp)
        )

        TerminalScanline(modifier = Modifier.fillMaxSize())

        Text(
            text = ui.statusText(),
            color = ui.statusColor(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 7.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SEQ ${ui.lastSequence?.toString() ?: "--"} | PERD ${ui.lostPackets} | ${ui.lastPacketAgeMillis?.formatAge() ?: "--"}",
                color = RadarPalette.NeutralDim,
                fontSize = 8.sp
            )
            if (!permissionsGranted && ui.mode == RadarDataMode.REAL_BLE) {
                Text(
                    text = "PERMISO BLE",
                    color = RadarPalette.Red,
                    fontSize = 8.sp,
                    modifier = Modifier.clickable(onClick = onRequestPermissions)
                )
            }
        }
    }
}

@Composable
private fun RadarBackdrop(modifier: Modifier) {
    BoxWithConstraints(modifier = modifier) {
        val density = LocalDensity.current
        val scale = min(
            with(density) { maxWidth.toPx() },
            with(density) { maxHeight.toPx() }
        ) / 450f
        val emblemWidth = with(density) { (322f * scale).toDp() }
        val emblemHeight = with(density) { (382f * scale).toDp() }

        Image(
            painter = painterResource(R.drawable.brotherhood_emblem_pipgreendim),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alpha = 88f / 255f,
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = emblemWidth, height = emblemHeight)
        )
        Image(
            painter = painterResource(R.drawable.gear_frame),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun TerminalScanline(modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "terminal_scanline")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6_000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terminal_scanline_progress"
    )

    Canvas(modifier = modifier) {
        val y = size.height * progress
        drawLine(
            color = RadarPalette.Green.copy(alpha = 0.4f),
            start = Offset(size.width * 12f / 450f, y),
            end = Offset(size.width * 438f / 450f, y),
            strokeWidth = 2f
        )
    }
}

@Composable
private fun RadarCone(targets: List<RadarTarget>, modifier: Modifier) {
    val density = LocalDensity.current
    Canvas(modifier = modifier) {
        val edgeMargin = with(density) { 6.dp.toPx() }
        val halfBeam = RADAR_BEAM_DEGREES / 2f
        val origin = Offset(size.width / 2f, size.height - edgeMargin)
        val widthLimit = (size.width / 2f - edgeMargin) /
            sin(Math.toRadians(halfBeam.toDouble())).toFloat()
        val displayRadius = min(widthLimit, origin.y - edgeMargin)
        val leftEnd = pointAtRange(origin, displayRadius, -halfBeam, 1f)
        val rightEnd = pointAtRange(origin, displayRadius, halfBeam, 1f)
        val outerCurve = rangeCurve(origin, displayRadius, 1f, halfBeam)
        val fill = Path().apply {
            moveTo(origin.x, origin.y)
            lineTo(leftEnd.x, leftEnd.y)
            addPath(outerCurve)
            lineTo(origin.x, origin.y)
            close()
        }

        drawPath(fill, RadarPalette.Blue.copy(alpha = 0.09f))

        for (meter in 1..MAX_RANGE_METERS) {
            val fraction = meter / MAX_RANGE_METERS.toFloat()
            drawPath(
                path = rangeCurve(origin, displayRadius, fraction, halfBeam),
                color = RadarPalette.Blue.copy(alpha = if (meter == MAX_RANGE_METERS) 0.9f else 0.48f),
                style = Stroke(width = if (meter == MAX_RANGE_METERS) 1.5f else 1f)
            )
        }

        drawLine(RadarPalette.Blue, origin, leftEnd, strokeWidth = 1.5f, cap = StrokeCap.Round)
        drawLine(RadarPalette.Blue, origin, rightEnd, strokeWidth = 1.5f, cap = StrokeCap.Round)
        val forwardEnd = pointAtRange(origin, displayRadius, 0f, 1f)
        drawLine(RadarPalette.GreenDim, origin, forwardEnd, strokeWidth = 1.5f, cap = StrokeCap.Round)

        for (meter in 1 until MAX_RANGE_METERS) {
            val fraction = meter / MAX_RANGE_METERS.toFloat()
            drawRangeTick(origin, leftEnd, fraction)
            drawRangeTick(origin, rightEnd, fraction)
        }

        val visibleTargets = targets.filter { target ->
            val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
            val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
            target.visible &&
                target.yMm >= 0 &&
                distance <= MAX_RANGE_MM &&
                abs(bearing) <= halfBeam
        }

        val radarMarker = Path().apply {
            moveTo(origin.x, origin.y - 9f)
            lineTo(origin.x - 6f, origin.y + 3f)
            lineTo(origin.x + 6f, origin.y + 3f)
            close()
        }
        drawPath(radarMarker, RadarPalette.Green)

        visibleTargets.forEach { target ->
            val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
            val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
            val marker = pointAtRange(
                origin,
                displayRadius,
                bearing,
                distance / MAX_RANGE_MM
            )
            val color = markerColor(target.slot)
            val alpha = if (target.state == TargetState.RETAINED) 0.42f else 1f
            drawCircle(RadarPalette.Black, radius = 7f, center = marker)
            drawCircle(color.copy(alpha = alpha), radius = 5f, center = marker)
            if (target.state == TargetState.RETAINED) {
                drawCircle(
                    color = color.copy(alpha = 0.8f),
                    radius = 9f,
                    center = marker,
                    style = Stroke(width = 1.5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 3f)))
                )
            }
        }

        drawIntoCanvas { canvas ->
            val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = with(density) { 8.sp.toPx() }
                typeface = android.graphics.Typeface.MONOSPACE
            }
            visibleTargets.forEach { target ->
                val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
                val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
                val marker = pointAtRange(
                    origin,
                    displayRadius,
                    bearing,
                    distance / MAX_RANGE_MM
                )
                val label = "T${target.slot}"
                targetPaint.color = markerColor(target.slot).toArgb()
                val labelWidth = targetPaint.measureText(label)
                val labelX = if (marker.x < origin.x) {
                    marker.x - labelWidth - 8f
                } else {
                    marker.x + 8f
                }.coerceIn(2f, size.width - labelWidth - 2f)
                val labelY = (marker.y - 6f).coerceIn(targetPaint.textSize, size.height - 2f)
                canvas.nativeCanvas.drawText(label, labelX, labelY, targetPaint)
            }
        }
    }
}

private fun rangeCurve(
    origin: Offset,
    displayRadius: Float,
    fraction: Float,
    halfBeam: Float
): Path {
    val path = Path()
    for (index in 0..CURVE_STEPS) {
        val bearing = -halfBeam + RADAR_BEAM_DEGREES * index / CURVE_STEPS
        val point = pointAtRange(origin, displayRadius, bearing, fraction)
        if (index == 0) path.moveTo(point.x, point.y) else path.lineTo(point.x, point.y)
    }
    return path
}

private fun pointAtRange(
    origin: Offset,
    displayRadius: Float,
    bearingDegrees: Float,
    fraction: Float
): Offset {
    val angle = Math.toRadians(bearingDegrees.toDouble())
    val direction = Offset(
        x = sin(angle).toFloat(),
        y = -cos(angle).toFloat()
    )
    val distance = displayRadius * fraction
    return Offset(
        x = origin.x + direction.x * distance,
        y = origin.y + direction.y * distance
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRangeTick(
    origin: Offset,
    end: Offset,
    fraction: Float
) {
    val point = Offset(
        x = origin.x + (end.x - origin.x) * fraction,
        y = origin.y + (end.y - origin.y) * fraction
    )
    val length = hypot(end.x - origin.x, end.y - origin.y)
    val normal = Offset(
        x = -(end.y - origin.y) / length * 4f,
        y = (end.x - origin.x) / length * 4f
    )
    drawLine(
        color = RadarPalette.Blue,
        start = Offset(point.x - normal.x, point.y - normal.y),
        end = Offset(point.x + normal.x, point.y + normal.y),
        strokeWidth = 1.5f,
        cap = StrokeCap.Round
    )
}

private fun markerColor(slot: Int): Color = RadarPalette.Red

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(),
    (red * 255).toInt(),
    (green * 255).toInt(),
    (blue * 255).toInt()
)

private fun RadarUiState.statusText(): String = when (mode) {
    RadarDataMode.SIMULATED -> "SIM | PRUEBA"
    RadarDataMode.REAL_BLE -> "BLE | ${displayState.label()}"
    null -> ""
}

private fun RadarUiState.statusColor(): Color = when (mode) {
    RadarDataMode.SIMULATED -> RadarPalette.Blue
    RadarDataMode.REAL_BLE -> when (displayState) {
        RadarDisplayState.RECEIVING -> RadarPalette.Green
        RadarDisplayState.ERROR -> RadarPalette.Red
        else -> RadarPalette.Amber
    }
    null -> RadarPalette.Neutral
}

private fun RadarDisplayState.label(): String = when (this) {
    RadarDisplayState.DISCONNECTED -> "DESCONECTADO"
    RadarDisplayState.SCANNING -> "BUSCANDO"
    RadarDisplayState.CONNECTING -> "CONECTANDO"
    RadarDisplayState.CONNECTED -> "CONECTADO"
    RadarDisplayState.RECEIVING -> "RECIBIENDO"
    RadarDisplayState.ERROR -> "ERROR"
}

private fun Long.formatAge(): String = if (this < 1000L) "0s" else String.format(Locale.US, "%.1fs", this / 1000f)

private const val RADAR_BEAM_DEGREES = 120f
private const val MAX_RANGE_METERS = 6
private const val MAX_RANGE_MM = 6000f
private const val CURVE_STEPS = 48
