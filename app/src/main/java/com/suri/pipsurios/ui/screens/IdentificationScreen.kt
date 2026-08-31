package com.suri.pipsurios.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun IdentificationScreen(onAuthenticated: () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition(label = "fingerprint_scan")
    val scanProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fingerprint_scan_progress"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawPipBoyFrame()
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = "IDENTIFICATION",
                color = PipGreen,
                fontSize = 21.sp,
                fontFamily = FontFamily.Monospace
            )

            Box(
                modifier = Modifier
                    .size(292.dp)
                    .clickable(onClick = onAuthenticated),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawFingerprintReader(
                        scanProgress = scanProgress,
                        fingerprintColor = PipGreen
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawPipBoyFrame() {
    val frameColor = PipGreenDim.copy(alpha = 0.26f)
    val gridColor = PipGreenDim.copy(alpha = 0.07f)
    val gridStep = 54.dp.toPx()
    val margin = 25.dp.toPx()
    val cornerLength = 34.dp.toPx()

    var x = 0f
    while (x <= size.width) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = 1.dp.toPx()
        )
        x += gridStep
    }

    var y = 0f
    while (y <= size.height) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = 1.dp.toPx()
        )
        y += gridStep
    }

    drawLine(frameColor, Offset(margin, margin), Offset(margin + cornerLength, margin), 2.dp.toPx())
    drawLine(frameColor, Offset(margin, margin), Offset(margin, margin + cornerLength), 2.dp.toPx())
    drawLine(frameColor, Offset(size.width - margin, margin), Offset(size.width - margin - cornerLength, margin), 2.dp.toPx())
    drawLine(frameColor, Offset(size.width - margin, margin), Offset(size.width - margin, margin + cornerLength), 2.dp.toPx())
    drawLine(frameColor, Offset(margin, size.height - margin), Offset(margin + cornerLength, size.height - margin), 2.dp.toPx())
    drawLine(frameColor, Offset(margin, size.height - margin), Offset(margin, size.height - margin - cornerLength), 2.dp.toPx())
    drawLine(frameColor, Offset(size.width - margin, size.height - margin), Offset(size.width - margin - cornerLength, size.height - margin), 2.dp.toPx())
    drawLine(frameColor, Offset(size.width - margin, size.height - margin), Offset(size.width - margin, size.height - margin - cornerLength), 2.dp.toPx())
}

private fun DrawScope.drawFingerprintReader(
    scanProgress: Float,
    fingerprintColor: Color
) {
    val side = min(size.width, size.height)
    val center = Offset(size.width / 2f, size.height / 2f)
    val radius = side * 0.44f
    val thinStroke = 1.dp.toPx()
    val strongStroke = 2.dp.toPx()
    val dimColor = PipGreenDim.copy(alpha = 0.72f)
    val faintColor = PipGreenDim.copy(alpha = 0.20f)

    drawCircle(
        color = faintColor,
        radius = radius,
        center = center,
        style = Stroke(width = strongStroke)
    )
    drawCircle(
        color = dimColor,
        radius = radius * 0.86f,
        center = center,
        style = Stroke(width = thinStroke)
    )
    drawCircle(
        color = faintColor,
        radius = radius * 0.62f,
        center = center,
        style = Stroke(width = thinStroke)
    )

    repeat(24) { index ->
        val angle = Math.toRadians(index * 15.0)
        val outerRadius = radius * if (index % 3 == 0) 1f else 0.96f
        val innerRadius = radius * if (index % 3 == 0) 0.88f else 0.91f
        drawLine(
            color = dimColor,
            start = Offset(
                center.x + cos(angle).toFloat() * innerRadius,
                center.y + sin(angle).toFloat() * innerRadius
            ),
            end = Offset(
                center.x + cos(angle).toFloat() * outerRadius,
                center.y + sin(angle).toFloat() * outerRadius
            ),
            strokeWidth = if (index % 3 == 0) strongStroke else thinStroke
        )
    }

    drawArc(
        color = fingerprintColor,
        startAngle = -90f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
        style = Stroke(width = 3.dp.toPx())
    )
    drawArc(
        color = fingerprintColor.copy(alpha = 0.55f),
        startAngle = 90f,
        sweepAngle = 110f,
        useCenter = false,
        topLeft = Offset(center.x - radius, center.y - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2f, radius * 2f),
        style = Stroke(width = 2.dp.toPx())
    )

    drawLine(
        color = fingerprintColor.copy(alpha = 0.76f),
        start = Offset(center.x - radius * 0.78f, center.y - radius * 0.66f + scanProgress * radius * 1.32f),
        end = Offset(center.x + radius * 0.78f, center.y - radius * 0.66f + scanProgress * radius * 1.32f),
        strokeWidth = 2.dp.toPx()
    )

    drawLine(
        color = faintColor,
        start = Offset(center.x - radius * 0.70f, center.y),
        end = Offset(center.x + radius * 0.70f, center.y),
        strokeWidth = thinStroke
    )
    drawLine(
        color = faintColor,
        start = Offset(center.x, center.y - radius * 0.70f),
        end = Offset(center.x, center.y + radius * 0.70f),
        strokeWidth = thinStroke
    )

    drawFingerprint(fingerprintColor)
    drawCircle(color = fingerprintColor, radius = 2.dp.toPx(), center = center)
}

private fun DrawScope.drawFingerprint(color: Color) {
    val side = min(size.width, size.height)
    val center = Offset(size.width / 2f, size.height * 0.55f)
    val stroke = side * 0.018f
    val paths = listOf(
        Path().apply {
            moveTo(center.x - side * 0.34f, center.y + side * 0.24f)
            cubicTo(
                center.x - side * 0.48f,
                center.y - side * 0.16f,
                center.x - side * 0.24f,
                center.y - side * 0.43f,
                center.x,
                center.y - side * 0.43f
            )
            cubicTo(
                center.x + side * 0.24f,
                center.y - side * 0.43f,
                center.x + side * 0.48f,
                center.y - side * 0.16f,
                center.x + side * 0.34f,
                center.y + side * 0.24f
            )
        },
        Path().apply {
            moveTo(center.x - side * 0.25f, center.y + side * 0.30f)
            cubicTo(
                center.x - side * 0.38f,
                center.y - side * 0.08f,
                center.x - side * 0.18f,
                center.y - side * 0.33f,
                center.x,
                center.y - side * 0.33f
            )
            cubicTo(
                center.x + side * 0.18f,
                center.y - side * 0.33f,
                center.x + side * 0.38f,
                center.y - side * 0.08f,
                center.x + side * 0.25f,
                center.y + side * 0.30f
            )
        },
        Path().apply {
            moveTo(center.x - side * 0.15f, center.y + side * 0.34f)
            cubicTo(
                center.x - side * 0.26f,
                center.y + side * 0.02f,
                center.x - side * 0.12f,
                center.y - side * 0.24f,
                center.x,
                center.y - side * 0.24f
            )
            cubicTo(
                center.x + side * 0.12f,
                center.y - side * 0.24f,
                center.x + side * 0.26f,
                center.y + side * 0.02f,
                center.x + side * 0.15f,
                center.y + side * 0.34f
            )
        },
        Path().apply {
            moveTo(center.x, center.y + side * 0.40f)
            cubicTo(
                center.x - side * 0.04f,
                center.y + side * 0.18f,
                center.x - side * 0.12f,
                center.y - side * 0.01f,
                center.x - side * 0.12f,
                center.y - side * 0.15f
            )
            cubicTo(
                center.x - side * 0.12f,
                center.y - side * 0.30f,
                center.x - side * 0.06f,
                center.y - side * 0.38f,
                center.x,
                center.y - side * 0.38f
            )
            cubicTo(
                center.x + side * 0.06f,
                center.y - side * 0.38f,
                center.x + side * 0.12f,
                center.y - side * 0.30f,
                center.x + side * 0.12f,
                center.y - side * 0.15f
            )
            cubicTo(
                center.x + side * 0.12f,
                center.y - side * 0.01f,
                center.x + side * 0.04f,
                center.y + side * 0.18f,
                center.x,
                center.y + side * 0.40f
            )
        },
        Path().apply {
            moveTo(center.x - side * 0.05f, center.y + side * 0.39f)
            cubicTo(
                center.x - side * 0.18f,
                center.y + side * 0.14f,
                center.x - side * 0.20f,
                center.y - side * 0.08f,
                center.x - side * 0.20f,
                center.y - side * 0.19f
            )
        },
        Path().apply {
            moveTo(center.x + side * 0.05f, center.y + side * 0.39f)
            cubicTo(
                center.x + side * 0.18f,
                center.y + side * 0.14f,
                center.x + side * 0.20f,
                center.y - side * 0.08f,
                center.x + side * 0.20f,
                center.y - side * 0.19f
            )
        }
    )

    paths.forEach { path ->
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}
