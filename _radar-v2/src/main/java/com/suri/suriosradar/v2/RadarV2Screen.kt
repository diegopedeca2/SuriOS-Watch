package com.suri.suriosradar.v2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.suriosradar.RadarDisplayState
import com.suri.suriosradar.ui.RadarPalette
import java.util.Locale
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RadarV2Screen(
    state: RadarV2UiState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RadarPalette.Black)
    ) {
        RadarV2Plot(state)

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            Text(
                text = "RADAR v2 / ESPHOME",
                color = RadarPalette.GreenBright,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = statusLabel(state),
                color = statusColor(state),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = "T:${state.targetCount} M:${state.movingTargetCount} S:${state.stillTargetCount} " +
                    "P:${if (state.presence) "ON" else "OFF"}",
                color = RadarPalette.Gray,
                fontSize = 7.sp
            )
            Text(
                text = "RX:${state.receivedFrames} SEQ:${state.lastSequence ?: "--"} " +
                    "AGE:${state.lastFrameAgeMillis?.let { String.format(Locale.US, "%.1fs", it / 1000f) } ?: "--"} " +
                    "LOST:${state.lostFrames}",
                color = RadarPalette.Gray,
                fontSize = 7.sp
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                V2Button("START", onStart)
                V2Button("STOP", onStop)
            }
            state.error?.let {
                Text(text = it, color = RadarPalette.Red, fontSize = 7.sp)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            state.targets.forEach { target ->
                Text(
                    text = "${target.slot}:" + if (target.active) {
                        "${target.xMm},${target.yMm} ${target.speedMmPerSecond}"
                    } else {
                        "NA"
                    },
                    color = if (target.active) RadarPalette.GreenDim else RadarPalette.Gray,
                    fontSize = 6.sp
                )
            }
        }
    }
}

@Composable
private fun V2Button(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 16.dp)
            .border(1.dp, RadarPalette.GreenDim, RoundedCornerShape(3.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(text = label, color = RadarPalette.Green, fontSize = 7.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun RadarV2Plot(state: RadarV2UiState) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 8.dp, end = 8.dp, top = 35.dp, bottom = 52.dp)
    ) {
        val halfBeam = BEAM_DEGREES / 2f
        val edge = min(size.width, size.height) * 0.04f
        val origin = Offset(size.width / 2f, size.height - edge)
        val radius = min(
            (size.width / 2f - edge) / sin(Math.toRadians(halfBeam.toDouble())).toFloat(),
            origin.y - edge
        )
        val left = point(origin, radius, -halfBeam, 1f)
        val right = point(origin, radius, halfBeam, 1f)
        val cone = Path().apply {
            moveTo(origin.x, origin.y)
            lineTo(left.x, left.y)
            for (step in 0..CURVE_STEPS) {
                val bearing = -halfBeam + BEAM_DEGREES * step / CURVE_STEPS
                val p = point(origin, radius, bearing, 1f)
                lineTo(p.x, p.y)
            }
            lineTo(origin.x, origin.y)
            close()
        }
        drawPath(cone, RadarPalette.Blue.copy(alpha = 0.08f))
        for (meter in 1..MAX_RANGE_METERS) {
            val fraction = meter / MAX_RANGE_METERS.toFloat()
            drawArc(
                color = RadarPalette.Blue.copy(alpha = if (meter == MAX_RANGE_METERS) 0.8f else 0.35f),
                startAngle = 240f,
                sweepAngle = 60f,
                useCenter = false,
                topLeft = Offset(origin.x - radius * fraction, origin.y - radius * fraction),
                size = androidx.compose.ui.geometry.Size(radius * 2f * fraction, radius * 2f * fraction),
                style = Stroke(width = if (meter == MAX_RANGE_METERS) 1.5f else 1f)
            )
        }
        drawLine(RadarPalette.Blue, origin, left, strokeWidth = 1.3f, cap = StrokeCap.Round)
        drawLine(RadarPalette.Blue, origin, right, strokeWidth = 1.3f, cap = StrokeCap.Round)
        drawLine(
            RadarPalette.GreenDim,
            origin,
            point(origin, radius, 0f, 1f),
            strokeWidth = 1.3f,
            cap = StrokeCap.Round
        )
        drawCircle(RadarPalette.Green, radius = 4f, center = origin)

        state.heldTargets.forEach { target ->
            val distance = hypot(target.xMm.toFloat(), target.yMm.toFloat())
            val bearing = Math.toDegrees(atan2(target.xMm.toDouble(), target.yMm.toDouble())).toFloat()
            if (target.yMm <= 0 || distance > MAX_RANGE_MM || abs(bearing) > halfBeam) return@forEach
            val marker = point(origin, radius, bearing, distance / MAX_RANGE_MM)
            val isCurrent = state.targets.firstOrNull { it.slot == target.slot }?.active == true
            drawCircle(
                color = if (isCurrent) RadarPalette.Red else RadarPalette.Amber,
                radius = if (isCurrent) 5f else 4f,
                center = marker
            )
        }
    }
}

private fun point(origin: Offset, radius: Float, bearingDegrees: Float, fraction: Float): Offset {
    val angle = Math.toRadians(bearingDegrees.toDouble())
    return Offset(
        x = origin.x + sin(angle).toFloat() * radius * fraction,
        y = origin.y - cos(angle).toFloat() * radius * fraction
    )
}

private fun statusLabel(state: RadarV2UiState): String = when {
    state.displayState == RadarDisplayState.RECEIVING -> "BLE | RECIBIENDO"
    state.displayState == RadarDisplayState.SCANNING -> "BLE | BUSCANDO"
    state.displayState == RadarDisplayState.CONNECTING -> "BLE | CONECTANDO"
    state.displayState == RadarDisplayState.ERROR -> "BLE | ERROR"
    else -> "BLE | DESCONECTADO"
}

private fun statusColor(state: RadarV2UiState): Color = when (state.displayState) {
    RadarDisplayState.RECEIVING -> RadarPalette.Green
    RadarDisplayState.ERROR -> RadarPalette.Red
    else -> RadarPalette.Amber
}

private const val BEAM_DEGREES = 120f
private const val MAX_RANGE_METERS = 6
private const val MAX_RANGE_MM = 7560f
private const val CURVE_STEPS = 32
