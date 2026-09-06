package com.suri.pipsurios.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGray
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

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

    TerminalScreen {
        Column(
            modifier = Modifier.align(Alignment.Center),
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
                    .size(width = 246.dp, height = 168.dp)
                    .clickable(onClick = onAuthenticated),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawFingerprintReader(
                        scanProgress = scanProgress,
                        scannerColor = PipBlue
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawFingerprintReader(
    scanProgress: Float,
    scannerColor: Color
) {
    val outerStroke = 3.dp.toPx()
    val innerStroke = 2.dp.toPx()
    val frameWidth = size.width * 0.82f
    val frameHeight = size.height * 0.96f
    val frameLeft = (size.width - frameWidth) / 2f
    val frameTop = (size.height - frameHeight) / 2f
    val frameSize = Size(frameWidth, frameHeight)
    val frameCorner = CornerRadius(18.dp.toPx(), 18.dp.toPx())

    drawRoundRect(
        color = Color(0xFF0E171D),
        topLeft = Offset(frameLeft, frameTop),
        size = frameSize,
        cornerRadius = frameCorner
    )
    drawRoundRect(
        color = PipGreenDim.copy(alpha = 0.82f),
        topLeft = Offset(frameLeft, frameTop),
        size = frameSize,
        cornerRadius = frameCorner,
        style = Stroke(width = outerStroke)
    )
    drawRoundRect(
        color = Color(0xFF1D2A33),
        topLeft = Offset(frameLeft + 8.dp.toPx(), frameTop + 8.dp.toPx()),
        size = Size(frameWidth - 16.dp.toPx(), frameHeight - 16.dp.toPx()),
        cornerRadius = CornerRadius(13.dp.toPx(), 13.dp.toPx()),
        style = Stroke(width = innerStroke)
    )

    val screenLeft = frameLeft + frameWidth * 0.17f
    val screenTop = frameTop + frameHeight * 0.13f
    val screenWidth = frameWidth * 0.66f
    val screenHeight = frameHeight * 0.74f
    val screenSize = Size(screenWidth, screenHeight)
    val screenCorner = CornerRadius(9.dp.toPx(), 9.dp.toPx())
    drawRoundRect(
        color = Color(0xFF03233A),
        topLeft = Offset(screenLeft, screenTop),
        size = screenSize,
        cornerRadius = screenCorner
    )
    drawRoundRect(
        color = scannerColor.copy(alpha = 0.9f),
        topLeft = Offset(screenLeft, screenTop),
        size = screenSize,
        cornerRadius = screenCorner,
        style = Stroke(width = 2.5.dp.toPx())
    )
    drawRoundRect(
        color = scannerColor.copy(alpha = 0.22f),
        topLeft = Offset(screenLeft + 5.dp.toPx(), screenTop + 5.dp.toPx()),
        size = Size(screenWidth - 10.dp.toPx(), screenHeight - 10.dp.toPx()),
        cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
        style = Stroke(width = 1.dp.toPx())
    )

    val scanY = screenTop + screenHeight * (0.16f + scanProgress * 0.68f)
    drawRoundRect(
        color = scannerColor.copy(alpha = 0.14f),
        topLeft = Offset(screenLeft + 4.dp.toPx(), scanY - 9.dp.toPx()),
        size = Size(screenWidth - 8.dp.toPx(), 18.dp.toPx()),
        cornerRadius = CornerRadius(5.dp.toPx(), 5.dp.toPx())
    )
    drawLine(
        color = scannerColor.copy(alpha = 0.42f),
        start = Offset(screenLeft + 8.dp.toPx(), scanY),
        end = Offset(screenLeft + screenWidth - 8.dp.toPx(), scanY),
        strokeWidth = 7.dp.toPx()
    )
    drawLine(
        color = scannerColor,
        start = Offset(screenLeft + 8.dp.toPx(), scanY),
        end = Offset(screenLeft + screenWidth - 8.dp.toPx(), scanY),
        strokeWidth = 2.5.dp.toPx()
    )

    val screwRadius = 4.dp.toPx()
    val screwOffset = 16.dp.toPx()
    listOf(
        Offset(frameLeft + screwOffset, frameTop + screwOffset),
        Offset(frameLeft + frameWidth - screwOffset, frameTop + screwOffset),
        Offset(frameLeft + screwOffset, frameTop + frameHeight - screwOffset),
        Offset(frameLeft + frameWidth - screwOffset, frameTop + frameHeight - screwOffset)
    ).forEach { screw ->
        drawCircle(PipGray, screwRadius, screw)
        drawCircle(PipBlack, screwRadius * 0.55f, screw)
        drawLine(PipGray.copy(alpha = 0.8f), screw - Offset(2.dp.toPx(), 2.dp.toPx()), screw + Offset(2.dp.toPx(), 2.dp.toPx()), 1.dp.toPx())
    }

    drawCircle(PipAmber, 4.dp.toPx(), Offset(frameLeft + frameWidth - screwOffset, frameTop + frameHeight * 0.09f))
    drawCircle(PipAmber.copy(alpha = 0.24f), 8.dp.toPx(), Offset(frameLeft + frameWidth - screwOffset, frameTop + frameHeight * 0.09f))
    drawCircle(PipGreen, 4.dp.toPx(), Offset(frameLeft + screwOffset, frameTop + frameHeight * 0.91f))
}
