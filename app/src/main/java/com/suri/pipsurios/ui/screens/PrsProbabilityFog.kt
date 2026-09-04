package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.prs.DensityCloud
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsGridProbe
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max

/**
 * Map overlay for TRACKER. It replaces the old circular grid with a soft,
 * irregular fog field. Areas with lower inferred probability receive less
 * fog, so the map becomes visible there as the signal model narrows.
 *
 * BLE still provides no bearing. The fog therefore represents a relative
 * probability field around the A56 (or the probe node), not a precise target
 * coordinate.
 */
@Composable
fun PrsProbabilityFog(
    contact: PrsContactSnapshot?,
    modifier: Modifier = Modifier,
    probeNodes: List<PrsGridProbe> = emptyList()
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (size.width <= 0f || size.height <= 0f) return@Canvas

            val origin = if (contact?.source == PrsObservationSource.PROBE_WATCH_2) {
                probeNodes.firstOrNull()?.let { probe ->
                    Offset(size.width * probe.xFraction, size.height * probe.yFraction)
                }
            } else {
                null
            } ?: Offset(size.width / 2f, size.height / 2f)

            val columns = 18
            val rows = 12
            val cellWidth = size.width / columns
            val cellHeight = size.height / rows
            val puffRadius = max(cellWidth, cellHeight) * 0.92f
            val maxRadius = hypot(size.width, size.height) * 0.52f

            // Keep a light veil over the map; the variable puffs above it form
            // the irregular cloud instead of a geometric circular grid.
            drawRect(PipBlack.copy(alpha = if (contact == null) 0.38f else 0.10f))
            repeat(rows) { row ->
                repeat(columns) { column ->
                    val centre = Offset(
                        x = (column + 0.5f) * cellWidth,
                        y = (row + 0.5f) * cellHeight
                    )
                    val distanceFraction = hypot(
                        centre.x - origin.x,
                        centre.y - origin.y
                    ) / maxRadius
                    val density = probabilityFogDensity(
                        distanceFraction = distanceFraction,
                        cloud = contact?.inference?.densityCloud
                    )
                    val texture = 0.82f + fogNoise(column, row) * 0.28f
                    drawCircle(
                        color = PipBlack.copy(alpha = (density * 0.72f * texture).coerceIn(0f, 0.82f)),
                        radius = puffRadius,
                        center = centre
                    )
                }
            }

            // The phone position remains explicit and is drawn above the fog.
            drawCircle(PipGreen.copy(alpha = 0.20f), radius = 12.dp.toPx(), center = Offset(size.width / 2f, size.height / 2f))
            drawCircle(PipGreen, radius = 5.dp.toPx(), center = Offset(size.width / 2f, size.height / 2f))

            probeNodes.forEach { probe ->
                val point = Offset(size.width * probe.xFraction, size.height * probe.yFraction)
                val colour = if (probe.state == "ACTIVE") PipBlue else PipAmber
                drawCircle(colour.copy(alpha = 0.24f), radius = 12.dp.toPx(), center = point)
                drawCircle(colour, radius = 4.dp.toPx(), center = point)
                drawLine(colour, Offset(point.x - 9.dp.toPx(), point.y), Offset(point.x + 9.dp.toPx(), point.y), strokeWidth = 1.5.dp.toPx())
                drawLine(colour, Offset(point.x, point.y - 9.dp.toPx()), Offset(point.x, point.y + 9.dp.toPx()), strokeWidth = 1.5.dp.toPx())
            }
        }
    }
}

/**
 * Returns fog density from 0 (clear) to 1 (dense fog). The likelihood is a
 * soft radial band because the current BLE model has no direction. Confidence
 * reduces the uncertainty floor, allowing low-probability areas to clear.
 */
internal fun probabilityFogDensity(distanceFraction: Float, cloud: DensityCloud?): Float {
    if (cloud == null) return 0.92f
    val spread = cloud.radialSpreadFraction.coerceAtLeast(0.08f)
    val delta = (distanceFraction - cloud.radialCenterFraction) / spread
    val likelihood = exp(-0.5f * delta * delta).coerceIn(0f, 1f)
    val confidence = cloud.confidence.coerceIn(0f, 1f)
    val uncertaintyFloor = 0.06f + (1f - confidence) * 0.34f
    val evidenceWeight = 0.34f + confidence * 0.66f
    return (uncertaintyFloor + likelihood * evidenceWeight).coerceIn(0f, 1f)
}

private fun fogNoise(column: Int, row: Int): Float {
    var value = column * 374761393 + row * 668265263
    value = (value xor (value ushr 13)) * 1274126177
    value = value xor (value ushr 16)
    return (value and 0x7FFFFFFF) / 2147483647f
}
