package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import com.suri.pipsurios.ui.state.ComplementCatalog
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay
import kotlin.math.min

@Composable
fun StatusLoadingScreen(onFinished: () -> Unit) {
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
fun StatusScreen(
    activeLoadout: LoadoutConfiguration,
    onDontForgetSelected: () -> Unit,
    onAccessoriesSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "STATUS",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        StatusArmorDiagram(
            activeLoadout = activeLoadout,
            onAccessoriesSelected = onAccessoriesSelected
        )

        Text(
            text = "DON'T FORGET",
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 64.dp)
                .clickable(onClick = onDontForgetSelected)
        )

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun StatusArmorDiagram(
    activeLoadout: LoadoutConfiguration,
    onAccessoriesSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 60.dp, bottom = 76.dp)
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val designWidth = 780f
            val designHeight = 224f
            val scale = min(size.width / designWidth, size.height / designHeight)
            val originX = (size.width - designWidth * scale) / 2f
            val originY = (size.height - designHeight * scale) / 2f
            fun point(x: Float, y: Float) = Offset(originX + x * scale, originY + y * scale)
            fun connector(from: Offset, to: Offset) {
                drawLine(PipGreenDim, from, to, strokeWidth = 2f * scale)
            }

            val outline = PipGreenDim
            val fill = PipGreenDim.copy(alpha = 0.18f)
            connector(point(390f, 48f), point(390f, 37f))
            connector(point(345f, 94f), point(250f, 72f))
            connector(point(435f, 94f), point(530f, 72f))
            connector(point(390f, 153f), point(250f, 138f))
            connector(point(425f, 101f), point(530f, 138f))
            connector(point(413f, 207f), point(530f, 190f))

            drawCircle(fill, 24f * scale, point(390f, 58f))
            drawCircle(outline, 24f * scale, point(390f, 58f), style = Stroke(2f * scale))
            drawRoundRect(
                color = fill,
                topLeft = point(371f, 84f),
                size = Size(38f * scale, 70f * scale),
                cornerRadius = CornerRadius(8f * scale),
            )
            drawRoundRect(
                color = outline,
                topLeft = point(371f, 84f),
                size = Size(38f * scale, 70f * scale),
                cornerRadius = CornerRadius(8f * scale),
                style = Stroke(2f * scale),
            )
            drawLine(outline, point(371f, 89f), point(345f, 96f), strokeWidth = 12f * scale)
            drawLine(outline, point(409f, 89f), point(435f, 96f), strokeWidth = 12f * scale)
            drawLine(outline, point(345f, 96f), point(326f, 125f), strokeWidth = 10f * scale)
            drawLine(outline, point(435f, 96f), point(454f, 125f), strokeWidth = 10f * scale)
            drawCircle(fill, 8f * scale, point(326f, 125f))
            drawCircle(fill, 8f * scale, point(454f, 125f))
            drawCircle(outline, 8f * scale, point(326f, 125f), style = Stroke(2f * scale))
            drawCircle(outline, 8f * scale, point(454f, 125f), style = Stroke(2f * scale))
            drawLine(outline, point(390f, 154f), point(390f, 163f), strokeWidth = 8f * scale)
            drawLine(outline, point(371f, 145f), point(409f, 145f), strokeWidth = 3f * scale)
            drawLine(outline, point(371f, 155f), point(409f, 155f), strokeWidth = 3f * scale)
            drawLine(outline, point(379f, 157f), point(372f, 207f), strokeWidth = 14f * scale)
            drawLine(outline, point(401f, 157f), point(408f, 207f), strokeWidth = 14f * scale)
            drawCircle(fill, 7f * scale, point(372f, 207f))
            drawCircle(fill, 7f * scale, point(408f, 207f))
            drawCircle(outline, 7f * scale, point(372f, 207f), style = Stroke(2f * scale))
            drawCircle(outline, 7f * scale, point(408f, 207f), style = Stroke(2f * scale))
            drawLine(outline, point(365f, 214f), point(379f, 214f), strokeWidth = 4f * scale)
            drawLine(outline, point(401f, 214f), point(415f, 214f), strokeWidth = 4f * scale)
        }

        StatusDiagramLabel(
            value = statusDisplayValue(activeLoadout.headgearProfile),
            modifier = Modifier.align(Alignment.TopCenter)
        )
        StatusDiagramLabel(
            value = statusDisplayValue(activeLoadout.secondaryWeapon?.displayName),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 48.dp)
        )
        StatusDiagramLabel(
            value = statusDisplayValue(activeLoadout.primaryWeaponDisplayName()),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 48.dp)
        )
        StatusDiagramLabel(
            value = statusDisplayValue(activeLoadout.frontPanelRole),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 116.dp)
        )
        StatusDiagramLabel(
            value = statusDisplayValue(activeLoadout.uniform),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp, top = 178.dp)
        )
        StatusAccessoriesButton(
            onClick = onAccessoriesSelected,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 116.dp)
        )
    }
}

@Composable
private fun StatusDiagramLabel(
    value: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        color = PipGreenDim,
        fontSize = 15.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier.widthIn(max = 230.dp)
    )
}

@Composable
private fun StatusAccessoriesButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = "ACCESORIES",
        color = PipGreen,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 3.dp)
    )
}

@Composable
fun StatusAccessoriesScreen(
    activeLoadout: LoadoutConfiguration,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "STATUS - ACCESORIES",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        val accessories = activeLoadout.accesories.sortedBy { it.displayName }
        if (accessories.isEmpty()) {
            Text(
                text = "> N/A",
                color = PipGreenDim,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                accessories.forEach { item ->
                    Text(
                        text = "> ${item.displayName}",
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

private fun statusDisplayValue(value: String?): String =
    value?.trim().takeIf { !it.isNullOrEmpty() } ?: "N/A"

/*
 * The T-45 silhouette is deliberately drawn in Compose for now. It is a
 * replaceable visual placeholder until the final armor asset is available.
 */

@Composable
fun DontForgetScreen(activeLoadout: LoadoutConfiguration, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "STATUS - DON'T FORGET",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        val reminderLines = ComplementCatalog.reminderLines(activeLoadout)
        if (reminderLines.isNotEmpty()) {
            var checkedItems by remember(reminderLines) {
                mutableStateOf(emptySet<String>())
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 88.dp)
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                reminderLines.forEach { item ->
                    Text(
                        text = "${if (item in checkedItems) "[X]" else "[ ]"} $item",
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable {
                            checkedItems = if (item in checkedItems) {
                                checkedItems - item
                            } else {
                                checkedItems + item
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                text = "UNDER CONSTRUCTION",
                color = PipRed,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun StatusEntry(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = "> ${value ?: "NOT CONFIGURED"}",
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
