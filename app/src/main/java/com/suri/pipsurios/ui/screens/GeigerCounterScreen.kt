package com.suri.pipsurios.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.geiger.ClickScheduler
import com.suri.pipsurios.geiger.GeigerEngine
import com.suri.pipsurios.geiger.GeigerSnapshot
import com.suri.pipsurios.geiger.VolumeKeyController
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GeigerCounterLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Text("LOADING...", color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun GeigerCounterScreen(
    volumeKeyController: VolumeKeyController,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val engine = remember { GeigerEngine() }
    val clickScheduler = remember { ClickScheduler(context.applicationContext) }
    var volumeUpPressed by remember { mutableStateOf(false) }
    var snapshot by remember { mutableStateOf(engine.snapshot()) }

    DisposableEffect(volumeKeyController) {
        volumeKeyController.activate { volumeUpPressed = it }
        onDispose { volumeKeyController.deactivate() }
    }

    DisposableEffect(clickScheduler) {
        onDispose { clickScheduler.release() }
    }

    LaunchedEffect(engine) {
        while (true) {
            delay(50)
            snapshot = engine.update(volumeUpPressed, 0.05f)
        }
    }

    LaunchedEffect(clickScheduler) {
        clickScheduler.run { snapshot.level }
    }

    val animatedNeedle by NeedleAnimation(snapshot.needleLevel)
    GeigerCounterContent(snapshot, animatedNeedle, onBack)
}

@Composable
fun NeedleAnimation(targetLevel: Float): State<Float> = animateFloatAsState(
    targetValue = targetLevel.coerceIn(0f, 1f),
    animationSpec = tween(durationMillis = 180, easing = LinearEasing),
    label = "GeigerNeedle"
)

@Composable
private fun GeigerCounterContent(
    snapshot: GeigerSnapshot,
    needleLevel: Float,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "GEIGER COUNTER",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Text(
            text = "☢",
            color = PipGreen,
            fontSize = 34.sp,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 48.dp)
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AnalogGeigerMeter(needleLevel)
            Text("STATUS", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
            Text(snapshot.status.name, color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )

        Text(
            text = "PIP-SuriOS v1.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun AnalogGeigerMeter(level: Float) {
    Box(modifier = Modifier.width(420.dp).height(170.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height * 0.88f)
            val radius = size.height * 0.72f
            val arcRect = Rect(
                left = center.x - radius,
                top = center.y - radius,
                right = center.x + radius,
                bottom = center.y + radius
            )

            drawArc(
                color = PipGreenDim,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(width = 3.dp.toPx())
            )

            repeat(11) { index ->
                val angle = Math.toRadians((180f + index * 18f).toDouble())
                val outer = Offset(
                    center.x + cos(angle).toFloat() * radius,
                    center.y + sin(angle).toFloat() * radius
                )
                val innerRadius = radius - if (index % 5 == 0) 20.dp.toPx() else 12.dp.toPx()
                val inner = Offset(
                    center.x + cos(angle).toFloat() * innerRadius,
                    center.y + sin(angle).toFloat() * innerRadius
                )
                drawLine(PipGreenDim, inner, outer, strokeWidth = 2.dp.toPx())
            }

            val needleAngle = Math.toRadians((180f + level.coerceIn(0f, 1f) * 180f).toDouble())
            val needleEnd = Offset(
                center.x + cos(needleAngle).toFloat() * (radius - 26.dp.toPx()),
                center.y + sin(needleAngle).toFloat() * (radius - 26.dp.toPx())
            )
            drawLine(
                color = PipGreen,
                start = center,
                end = needleEnd,
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
            drawCircle(PipGreen, radius = 7.dp.toPx(), center = center)
        }

        Text(
            text = "LOW",
            color = PipGreen,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart)
        )
        Text(
            text = "HIGH",
            color = PipGreen,
            fontSize = 16.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}
