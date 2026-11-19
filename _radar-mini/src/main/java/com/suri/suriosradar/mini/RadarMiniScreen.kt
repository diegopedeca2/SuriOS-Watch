package com.suri.suriosradar.mini

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.suriosradar.R
import com.suri.suriosradar.ui.RadarPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

private object MiniRadarColors {
    val Background = RadarPalette.Black
    val T60Yellow = Color(0xFFFFC857)
    val Grid = T60Yellow
    val GridDim = T60Yellow.copy(alpha = 0.75f)
    val Sweep = T60Yellow
    val Accent = T60Yellow
    val Amber = T60Yellow
    val White = T60Yellow
    val Muted = RadarPalette.GreenDim
    val Red = RadarPalette.Red
}

@Composable
fun RadarMiniScreen(
    state: RadarMiniUiState
) {
    var sweepProgress by remember { mutableStateOf(0f) }
    LaunchedEffect(Unit) {
        while (isActive) {
            sweepProgress = (System.currentTimeMillis() % SWEEP_PERIOD_MS).toFloat() / SWEEP_PERIOD_MS
            delay(16L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiniRadarColors.Background)
    ) {
        BrotherhoodBackdrop()
        MiniRadarPlot(state, sweepProgress)
        Text(
            text = "SURIOS // v1.1",
            color = MiniRadarColors.GridDim,
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 8.dp)
        )
    }
}

@Composable
private fun BrotherhoodBackdrop() {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
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
            alpha = 42f / 255f,
            modifier = Modifier
                .align(Alignment.Center)
                .size(width = emblemWidth, height = emblemHeight)
        )
    }
}

@Composable
private fun MiniRadarPlot(state: RadarMiniUiState, sweepProgress: Float) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 51.dp, bottom = 36.dp)
    ) {
        val displayHalfAngle = DISPLAY_HALF_ANGLE_DEGREES
        val edge = min(size.width, size.height) * 0.035f
        val origin = Offset(size.width / 2f, size.height - edge)
        val radius = min(
            (size.width / 2f - edge) / sin(Math.toRadians(displayHalfAngle.toDouble())).toFloat(),
            origin.y - edge
        )
        val inner = radius * 0.20f
        val total = radius

        for (ring in 0..4) {
            val ringRadius = inner + (total - inner) * ring / 4f
            drawFanArc(origin, ringRadius, displayHalfAngle, MiniRadarColors.GridDim, if (ring == 4) 1.5f else 1f)
        }

        for (angle in listOf(-60f, -30f, 0f, 30f, 60f)) {
            drawLine(
                if (angle == 0f) MiniRadarColors.Grid else MiniRadarColors.GridDim,
                point(origin, total, angle),
                point(origin, inner, angle),
                strokeWidth = if (angle == 0f) 1.4f else 1f,
                cap = StrokeCap.Round
            )
        }
        drawCircle(MiniRadarColors.Accent, radius = 4f, center = origin)

        for (tail in 4 downTo 0) {
            val sweepRadius = inner + (total - inner) * sweepProgress - tail * 5f
            if (sweepRadius > inner) {
                drawFanArc(
                    origin,
                    sweepRadius,
                    displayHalfAngle,
                    MiniRadarColors.Sweep.copy(alpha = (0.82f - tail * 0.15f).coerceAtLeast(0.1f)),
                    2f
                )
            }
        }

        val targetPositions = if (state.demo) {
            listOf(demoTarget())
        } else {
            state.packet?.targets
                ?.asSequence()
                ?.filter { it.hasTarget }
                ?.map { it.xMm.toFloat() to it.yMm.toFloat() }
                ?.toList()
                .orEmpty()
        }
        targetPositions.forEach { (rawX, rawY) ->
            val x = if (state.mirrored) rawX else -rawX
            val distance = hypot(x, rawY)
            val bearing = Math.toDegrees(atan2(x.toDouble(), rawY.toDouble())).toFloat()
            if (rawY > 0f && distance <= state.rangeMm && abs(bearing) <= 60f) {
                val compressedBearing = bearing * (displayHalfAngle / 60f)
                val marker = point(origin, inner + (total - inner) * distance / state.rangeMm, compressedBearing)
                val markerColor = if (state.demo) MiniRadarColors.Amber else MiniRadarColors.Accent
                drawCircle(markerColor.copy(alpha = 0.18f), radius = 13f, center = marker)
                drawCircle(markerColor.copy(alpha = 0.40f), radius = 8f, center = marker)
                drawCircle(markerColor, radius = 3.5f, center = marker)
                drawCircle(MiniRadarColors.White, radius = 1.5f, center = marker)
                drawLine(markerColor, marker.copy(x = marker.x - 10f), marker.copy(x = marker.x - 5f), strokeWidth = 1.5f)
                drawLine(markerColor, marker.copy(x = marker.x + 5f), marker.copy(x = marker.x + 10f), strokeWidth = 1.5f)
                drawLine(markerColor, marker.copy(y = marker.y - 10f), marker.copy(y = marker.y - 5f), strokeWidth = 1.5f)
                drawLine(markerColor, marker.copy(y = marker.y + 5f), marker.copy(y = marker.y + 10f), strokeWidth = 1.5f)
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawFanArc(
    origin: Offset,
    radius: Float,
    halfAngle: Float,
    color: Color,
    strokeWidth: Float
) {
    val path = Path()
    for (step in 0..ARC_STEPS) {
        val bearing = -halfAngle + (halfAngle * 2f) * step / ARC_STEPS
        val current = point(origin, radius, bearing)
        if (step == 0) path.moveTo(current.x, current.y) else path.lineTo(current.x, current.y)
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

private fun point(origin: Offset, radius: Float, bearingDegrees: Float): Offset {
    val angle = Math.toRadians(bearingDegrees.toDouble())
    return Offset(
        x = origin.x + sin(angle).toFloat() * radius,
        y = origin.y - cos(angle).toFloat() * radius
    )
}

private fun demoTarget(): Pair<Float, Float> {
    val time = System.currentTimeMillis() / 1000f
    return sin(time * 0.45f) * 900f to 1700f + cos(time * 0.3f) * 500f
}

private const val DISPLAY_HALF_ANGLE_DEGREES = 60f
private const val ARC_STEPS = 48
private const val SWEEP_PERIOD_MS = 800L
