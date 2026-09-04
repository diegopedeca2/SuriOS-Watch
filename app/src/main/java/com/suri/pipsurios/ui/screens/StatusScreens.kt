package com.suri.pipsurios.ui.screens

import com.suri.pipsurios.PipSuriOsVersion
import androidx.compose.foundation.background
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import com.suri.pipsurios.R
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import com.suri.pipsurios.ui.state.ComplementCatalog
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay

@Composable
fun StatusLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    TerminalScreen {
        LoadingGlitchText(modifier = Modifier.align(Alignment.Center))
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
            text = PipSuriOsVersion,
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
    Box(modifier = Modifier.fillMaxSize()) {
        // TERMINAL uses 18.dp for the frame and 12.dp for its content inset.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(STATUS_GRID_CONTENT_INSET)
        ) {
            Image(
                painter = painterResource(R.drawable.status_armor),
                contentDescription = "STATUS ARMOR",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            Canvas(modifier = Modifier.fillMaxSize()) {
                val divisions = STATUS_GRID_LINE_COUNT - 1

                // STATUS line 01: from vertical 5.25 / horizontal 1 to 6.9 / 1.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((5.25f - 1f) / divisions),
                        y = 0f
                    ),
                    end = Offset(
                        x = size.width * ((6.9f - 1f) / divisions),
                        y = 0f
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // STATUS line 02: from vertical 3.1 / horizontal 4 to 4 / 4.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((3.1f - 1f) / divisions),
                        y = size.height * ((4f - 1f) / divisions)
                    ),
                    end = Offset(
                        x = size.width * ((4f - 1f) / divisions),
                        y = size.height * ((4f - 1f) / divisions)
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // STATUS line 03: from 6 / 5 to vertical 6.9 / horizontal 7.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((6f - 1f) / divisions),
                        y = size.height * ((5f - 1f) / divisions)
                    ),
                    end = Offset(
                        x = size.width * ((6.9f - 1f) / divisions),
                        y = size.height * ((6f - 1f) / divisions)
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // STATUS line 04: from 6 / 3 to vertical 6.9 / horizontal 3.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((6f - 1f) / divisions),
                        y = size.height * ((3f - 1f) / divisions)
                    ),
                    end = Offset(
                        x = size.width * ((6.9f - 1f) / divisions),
                        y = size.height * ((3f - 1f) / divisions)
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // STATUS line 05: from vertical 3.1 / horizontal 6 to 4 / 5.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((3.1f - 1f) / divisions),
                        y = size.height * ((6f - 1f) / divisions)
                    ),
                    end = Offset(
                        x = size.width * ((4f - 1f) / divisions),
                        y = size.height * ((5f - 1f) / divisions)
                    ),
                    strokeWidth = 2.dp.toPx()
                )

                // STATUS line 06: from vertical 3.1 / horizontal 8 to 4 / 7.
                drawLine(
                    color = PipGreen,
                    start = Offset(
                        x = size.width * ((3.1f - 1f) / divisions),
                        y = size.height * ((8f - 1f) / divisions)
                    ),
                    end = Offset(
                        x = size.width * ((4.25f - 1f) / divisions),
                        y = size.height * ((6.5f - 1f) / divisions)
                    ),
                    strokeWidth = 2.dp.toPx()
                )

            }

        }

        // Coordinates are vertical line first, horizontal line second.
        StatusCoordinateLabel(
            value = statusDisplayValue(activeLoadout.primaryWeaponDisplayName()),
            verticalLine = 3,
            horizontalLine = 6,
            side = StatusLabelSide.Left
        )
        StatusCoordinateLabel(
            value = statusDisplayValue(activeLoadout.secondaryWeaponDisplayName()),
            verticalLine = 7,
            horizontalLine = 6,
            side = StatusLabelSide.Right
        )
        StatusCoordinateLabel(
            value = statusDisplayValue(activeLoadout.headgearProfile),
            verticalLine = 7,
            horizontalLine = 1,
            side = StatusLabelSide.Right
        )
        StatusCoordinateButton(
            onClick = onAccessoriesSelected,
            verticalLine = 3,
            horizontalLine = 4,
            side = StatusLabelSide.Left
        )
        StatusCoordinateLabel(
            value = statusDisplayValue(activeLoadout.frontPanelRole),
            verticalLine = 7,
            horizontalLine = 3,
            side = StatusLabelSide.Right
        )
        StatusCoordinateLabel(
            value = statusDisplayValue(activeLoadout.uniform),
            verticalLine = 3,
            horizontalLine = 8,
            side = StatusLabelSide.Left
        )

    }
}

private val STATUS_GRID_CONTENT_INSET = 30.dp
private const val STATUS_GRID_LINE_COUNT = 9

private enum class StatusLabelSide {
    Left,
    Right
}

@Composable
private fun StatusCoordinateLabel(
    value: String,
    verticalLine: Int,
    horizontalLine: Int,
    side: StatusLabelSide
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val labelWidth = 190.dp
        val labelHeight = 32.dp
        val gridWidth = (maxWidth - (STATUS_GRID_CONTENT_INSET * 2)).coerceAtLeast(0.dp)
        val gridHeight = (maxHeight - (STATUS_GRID_CONTENT_INSET * 2)).coerceAtLeast(0.dp)
        val lineX = STATUS_GRID_CONTENT_INSET +
            (gridWidth * ((verticalLine - 1) / (STATUS_GRID_LINE_COUNT - 1).toFloat()))
        val lineY = STATUS_GRID_CONTENT_INSET +
            (gridHeight * ((horizontalLine - 1) / (STATUS_GRID_LINE_COUNT - 1).toFloat()))
        val labelX = (if (side == StatusLabelSide.Left) lineX - labelWidth else lineX)
            .coerceIn(0.dp, (maxWidth - labelWidth).coerceAtLeast(0.dp))
        val labelY = (lineY - (labelHeight / 2)).coerceIn(
            0.dp,
            (maxHeight - labelHeight).coerceAtLeast(0.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = labelX, y = labelY)
                .width(labelWidth)
                .height(labelHeight),
            contentAlignment = if (side == StatusLabelSide.Left) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            }
        ) {
            StatusDiagramLabel(
                value = value,
                textAlign = if (side == StatusLabelSide.Left) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatusCoordinateButton(
    onClick: () -> Unit,
    verticalLine: Int,
    horizontalLine: Int,
    side: StatusLabelSide
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val labelWidth = 190.dp
        val labelHeight = 32.dp
        val gridWidth = (maxWidth - (STATUS_GRID_CONTENT_INSET * 2)).coerceAtLeast(0.dp)
        val gridHeight = (maxHeight - (STATUS_GRID_CONTENT_INSET * 2)).coerceAtLeast(0.dp)
        val lineX = STATUS_GRID_CONTENT_INSET +
            (gridWidth * ((verticalLine - 1) / (STATUS_GRID_LINE_COUNT - 1).toFloat()))
        val lineY = STATUS_GRID_CONTENT_INSET +
            (gridHeight * ((horizontalLine - 1) / (STATUS_GRID_LINE_COUNT - 1).toFloat()))
        val labelX = (if (side == StatusLabelSide.Left) lineX - labelWidth else lineX)
            .coerceIn(0.dp, (maxWidth - labelWidth).coerceAtLeast(0.dp))
        val labelY = (lineY - (labelHeight / 2)).coerceIn(
            0.dp,
            (maxHeight - labelHeight).coerceAtLeast(0.dp)
        )

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = labelX, y = labelY)
                .width(labelWidth)
                .height(labelHeight),
            contentAlignment = if (side == StatusLabelSide.Left) {
                Alignment.CenterEnd
            } else {
                Alignment.CenterStart
            }
        ) {
            StatusAccessoriesButton(
                onClick = onClick,
                textAlign = if (side == StatusLabelSide.Left) TextAlign.End else TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun StatusDiagramLabel(
    value: String,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier
) {
    Text(
        text = value,
        color = PipGreen,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = textAlign,
        modifier = modifier.widthIn(max = 230.dp)
    )
}

@Composable
private fun StatusAccessoriesButton(
    onClick: () -> Unit,
    textAlign: TextAlign = TextAlign.Start,
    modifier: Modifier = Modifier
) {
    Text(
        text = "ACCESORIES",
        color = PipGreen,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = textAlign,
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

        val accessories = (activeLoadout.accesories.map { it.displayName } + activeLoadout.customAccesories)
            .sorted()
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
                        text = "> $item",
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
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

private fun statusDisplayValue(value: String?): String =
    value?.trim().takeIf { !it.isNullOrEmpty() } ?: "N/A"

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
            text = PipSuriOsVersion,
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
