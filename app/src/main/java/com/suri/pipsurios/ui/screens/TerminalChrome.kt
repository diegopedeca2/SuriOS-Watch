package com.suri.pipsurios.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.R
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

/**
 * Shared visual shell for the boot and home surfaces.
 * It only draws decoration, so each screen keeps its existing behaviour.
 */
@Composable
fun TerminalScreen(
    modifier: Modifier = Modifier,
    showEmblem: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "terminal_chrome")
    val scanlinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terminal_scanline"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PipBlack)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawTerminalBackdrop(scanlinePosition)
        }

        if (showEmblem) {
            TerminalEmblem()
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp)
                .terminalFrame()
                .padding(12.dp),
            content = content
        )
    }
}

/**
 * Applies the same terminal decoration over a screen that owns its own root
 * background. This lets older menus keep their layout and actions unchanged.
 */
@Composable
fun TerminalOverlay(
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "terminal_overlay")
    val scanlinePosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4_200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "terminal_overlay_scanline"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize(), content = content)
        Box(modifier = Modifier.fillMaxSize()) {
            TerminalEmblem()
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawTerminalBackdrop(scanlinePosition)
                drawTerminalFrame(margin = 18.dp.toPx())
            }
        }
    }
}

@Composable
fun TerminalPanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(PipBlack.copy(alpha = 0.78f))
            .terminalFrame()
            .padding(18.dp),
        content = content
    )
}

private fun Modifier.terminalFrame(): Modifier = drawBehind {
    drawTerminalFrame()
}

private fun DrawScope.drawTerminalFrame(margin: Float = 0f) {
    val frameColor = PipGreenDim.copy(alpha = 0.56f)
    val cornerColor = PipGreen.copy(alpha = 0.72f)
    val stroke = 1.dp.toPx()
    val cornerLength = 22.dp.toPx()
    val right = size.width - margin
    val bottom = size.height - margin
    val frameWidth = (right - margin).coerceAtLeast(0f)
    val frameHeight = (bottom - margin).coerceAtLeast(0f)

    drawRect(
        color = frameColor,
        topLeft = Offset(margin, margin),
        size = Size(frameWidth, frameHeight),
        style = Stroke(width = stroke)
    )

    drawLine(cornerColor, Offset(margin, margin + cornerLength), Offset(margin, margin), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(margin, margin), Offset(margin + cornerLength, margin), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(right - cornerLength, margin), Offset(right, margin), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(right, margin), Offset(right, margin + cornerLength), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(margin, bottom - cornerLength), Offset(margin, bottom), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(margin, bottom), Offset(margin + cornerLength, bottom), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(right - cornerLength, bottom), Offset(right, bottom), strokeWidth = stroke * 2f)
    drawLine(cornerColor, Offset(right, bottom - cornerLength), Offset(right, bottom), strokeWidth = stroke * 2f)
}

@Composable
private fun TerminalEmblem() {
    Box(modifier = Modifier.fillMaxSize()) {
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val rotation = if (isPortrait) 90f else 0f
        Image(
            painter = painterResource(R.drawable.brotherhood_emblem_pipgreen),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(0.82f)
                .alpha(0.26f)
                .graphicsLayer { rotationZ = rotation },
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(PipGreenDim)
        )
    }
}

private fun DrawScope.drawTerminalBackdrop(scanlinePosition: Float) {
    val gridColor = PipGreenDim.copy(alpha = 0.075f)
    val gridStep = 42.dp.toPx()
    val scanColor = PipGreen.copy(alpha = 0.12f)

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

    val scanlineY = size.height * scanlinePosition
    drawLine(
        color = scanColor,
        start = Offset(0f, scanlineY),
        end = Offset(size.width, scanlineY),
        strokeWidth = 2.dp.toPx()
    )
}
