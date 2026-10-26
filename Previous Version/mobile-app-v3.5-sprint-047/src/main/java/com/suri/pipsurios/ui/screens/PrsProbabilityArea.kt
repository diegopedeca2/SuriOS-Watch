package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.prs.PrsTargetAreaEstimate
import com.suri.pipsurios.terrain.TerrainViewportTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.pow

/** Draws only the current target hypothesis as light, dashed map hatching. */
@Composable
fun PrsProbabilityArea(
    estimate: PrsTargetAreaEstimate,
    mapTransform: TerrainViewportTransform?,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val target = estimate.center ?: return@Canvas
            val transform = mapTransform ?: return@Canvas
            if (size.width <= 0f || size.height <= 0f) return@Canvas

            val screenPoint = transform.geoToScreen(target)
            val pixelsPerMeter = pixelsPerMeter(target.latitude, transform.zoom)
            val radius = (estimate.radiusMeters * pixelsPerMeter)
                .coerceAtLeast(12f)
                .coerceAtMost(max(size.width, size.height) * 1.5f)
            val centre = Offset(screenPoint.first, screenPoint.second)
            val bounds = Rect(
                left = centre.x - radius,
                top = centre.y - radius,
                right = centre.x + radius,
                bottom = centre.y + radius
            )
            val areaPath = Path().apply { addOval(bounds) }
            val dash = PathEffect.dashPathEffect(
                floatArrayOf(4.dp.toPx(), 10.dp.toPx()),
                phase = 0f
            )
            val stripeSpacing = max(6.dp.toPx(), radius * 0.04f)

            clipPath(areaPath) {
                var y = bounds.top - radius
                while (y <= bounds.bottom + radius) {
                    drawLine(
                        color = androidx.compose.ui.graphics.Color(0x66FF3B30),
                        start = Offset(bounds.left - radius, y),
                        end = Offset(bounds.right + radius, y + radius * 1.6f),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = dash
                    )
                    y += stripeSpacing
                }
            }
        }
    }
}

private fun pixelsPerMeter(latitude: Double, zoom: Float): Float {
    val worldPixels = 256.0 * 2.0.pow(zoom.toDouble())
    val metresPerPixel = 2.0 * PI * EARTH_RADIUS_METERS *
        cos(latitude * PI / 180.0).coerceAtLeast(0.01) / worldPixels
    return (1.0 / metresPerPixel).toFloat()
}

private const val EARTH_RADIUS_METERS = 6_371_000.0
