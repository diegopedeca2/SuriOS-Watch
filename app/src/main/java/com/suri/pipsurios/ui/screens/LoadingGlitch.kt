package com.suri.pipsurios.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipGreen
import kotlin.math.cos
import kotlin.math.sin

/** Shared terminal loading text: the signal tears into horizontal slices and recomposes. */
@Composable
fun LoadingGlitchText(
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 30.sp
) {
    val interference = rememberInfiniteTransition(label = "loading_glitch")
    val phase by interference.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loading_glitch_phase"
    )
    val flicker by interference.animateFloat(
        initialValue = 0.05f,
        targetValue = 0.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loading_glitch_flicker"
    )
    val textMeasurer = rememberTextMeasurer()
    val textStyle = TextStyle(
        color = PipGreen,
        fontSize = fontSize,
        fontFamily = FontFamily.Monospace
    )
    val normalText = textMeasurer.measure(text = "LOADING...", style = textStyle)
    val dimText = textMeasurer.measure(
        text = "LOADING...",
        style = textStyle.copy(color = PipGreen.copy(alpha = 0.48f))
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Keeps the component's natural size while Canvas draws the visible version.
        Text(
            text = "LOADING...",
            style = textStyle.copy(color = Color.Transparent)
        )
        Canvas(modifier = Modifier.matchParentSize()) {
            drawLoadingInterference(phase = phase, flicker = flicker)
            drawDistortedLoadingText(normalText = normalText, dimText = dimText, phase = phase)
        }
    }
}

private fun DrawScope.drawLoadingInterference(phase: Float, flicker: Float) {
    val lineHeight = 1.5.dp.toPx()
    repeat(3) { fragment ->
        val y = ((phase + fragment * 0.31f) % 1f) * size.height
        val start = ((fragment * 0.23f + phase * 0.37f) % 1f) * size.width
        val width = size.width * (0.22f + fragment * 0.12f)
        drawRect(
            color = PipGreen.copy(alpha = flicker + 0.04f),
            topLeft = Offset(start, y),
            size = Size(width, lineHeight)
        )
    }
}

private fun DrawScope.drawDistortedLoadingText(
    normalText: TextLayoutResult,
    dimText: TextLayoutResult,
    phase: Float
) {
    val distortion = when {
        phase < 0.20f -> 0f
        phase < 0.34f -> (phase - 0.20f) / 0.14f
        phase < 0.64f -> 1f
        phase < 0.84f -> (0.84f - phase) / 0.20f
        else -> 0f
    }.coerceIn(0f, 1f)
    val textLeft = (size.width - normalText.size.width) / 2f
    val textTop = (size.height - normalText.size.height) / 2f

    if (distortion < 0.01f) {
        drawText(normalText, topLeft = Offset(textLeft, textTop))
        return
    }

    drawText(dimText, topLeft = Offset(textLeft, textTop))

    val sliceHeight = normalText.size.height / 5f
    repeat(5) { slice ->
        val sliceTop = textTop + slice * sliceHeight
        val sliceBottom = if (slice == 4) {
            textTop + normalText.size.height
        } else {
            sliceTop + sliceHeight
        }
        val wave = sin((phase * 18f + slice * 2.3f).toDouble()).toFloat()
        val drift = cos((phase * 13f + slice * 1.7f).toDouble()).toFloat()
        val horizontalTear = wave * (4f + 18f * distortion)
        val verticalTear = drift * (1f + 4f * distortion)

        clipRect(
            left = 0f,
            top = sliceTop,
            right = size.width,
            bottom = sliceBottom
        ) {
            translate(left = horizontalTear, top = verticalTear) {
                drawText(normalText, topLeft = Offset(textLeft, textTop))
            }
        }
    }

    repeat(3) { tear ->
        val y = textTop + normalText.size.height * (0.18f + tear * 0.31f)
        val start = textLeft - 12f + sin((phase * 21f + tear).toDouble()).toFloat() * 9f
        val width = normalText.size.width * (0.26f + tear * 0.10f)
        drawRect(
            color = PipGreen.copy(alpha = 0.20f * distortion),
            topLeft = Offset(start, y),
            size = Size(width, 2.dp.toPx())
        )
    }
}
