package com.suri.pipsurios.terrain

import kotlin.math.hypot
import kotlin.math.log2
import kotlin.math.pow

data class TerrainTileCoverage(
    val zoom: Int,
    val minPixelX: Double,
    val maxPixelX: Double,
    val minPixelY: Double,
    val maxPixelY: Double
) {
    val widthPixels get() = maxPixelX - minPixelX
    val heightPixels get() = maxPixelY - minPixelY

    fun minimumDisplayZoom(viewportWidth: Int, viewportHeight: Int, configuredMinimum: Float, maximum: Float): Float {
        if (viewportWidth <= 0 || viewportHeight <= 0) return configuredMinimum
        val diagonal = hypot(viewportWidth.toDouble(), viewportHeight.toDouble())
        val nativeShortSide = minOf(widthPixels, heightPixels)
        return maxOf(configuredMinimum.toDouble(), zoom + log2(diagonal / nativeShortSide))
            .coerceAtMost(maximum.toDouble()).toFloat()
    }

    fun clampCenterForFullRotation(center: GeoPoint, displayZoom: Float, viewportWidth: Int, viewportHeight: Int): GeoPoint {
        if (viewportWidth <= 0 || viewportHeight <= 0) return center
        val scale = 2.0.pow(displayZoom.toDouble() - zoom)
        val nativeRadius = hypot(viewportWidth.toDouble(), viewportHeight.toDouble()) / 2.0 / scale
        val world = WebMercator.toWorldPixel(center, zoom)
        val x = clampAxis(world.x, minPixelX + nativeRadius, maxPixelX - nativeRadius)
        val y = clampAxis(world.y, minPixelY + nativeRadius, maxPixelY - nativeRadius)
        return WebMercator.fromWorldPixel(WorldPixel(x, y), zoom)
    }

    /**
     * Restricts a camera movement with one scalar for both axes. Independent axis
     * clamping changes the direction of a rotated screen gesture into a diagonal.
     */
    fun constrainCenterMovement(
        start: GeoPoint,
        requested: GeoPoint,
        displayZoom: Float,
        viewportWidth: Int,
        viewportHeight: Int
    ): GeoPoint {
        if (viewportWidth <= 0 || viewportHeight <= 0) return requested
        val scale = 2.0.pow(displayZoom.toDouble() - zoom)
        val radius = hypot(viewportWidth.toDouble(), viewportHeight.toDouble()) / 2.0 / scale
        val minX = minPixelX + radius
        val maxX = maxPixelX - radius
        val minY = minPixelY + radius
        val maxY = maxPixelY - radius
        if (minX > maxX || minY > maxY) return clampCenterForFullRotation(start, displayZoom, viewportWidth, viewportHeight)
        val from = WebMercator.toWorldPixel(start, zoom)
        val to = WebMercator.toWorldPixel(requested, zoom)
        val dx = to.x - from.x
        val dy = to.y - from.y
        var fraction = 1.0
        if (dx > 0.0) fraction = minOf(fraction, (maxX - from.x) / dx)
        if (dx < 0.0) fraction = minOf(fraction, (minX - from.x) / dx)
        if (dy > 0.0) fraction = minOf(fraction, (maxY - from.y) / dy)
        if (dy < 0.0) fraction = minOf(fraction, (minY - from.y) / dy)
        fraction = fraction.coerceIn(0.0, 1.0)
        return WebMercator.fromWorldPixel(WorldPixel(from.x + dx * fraction, from.y + dy * fraction), zoom)
    }

    private fun clampAxis(value: Double, minimum: Double, maximum: Double): Double =
        if (minimum <= maximum) value.coerceIn(minimum, maximum) else (minimum + maximum) / 2.0

    companion object {
        fun from(keys: Set<TileKey>, zoom: Int): TerrainTileCoverage? {
            val level = keys.filter { it.zoom == zoom }
            if (level.isEmpty()) return null
            return TerrainTileCoverage(
                zoom = zoom,
                minPixelX = level.minOf { it.x } * 256.0,
                maxPixelX = (level.maxOf { it.x } + 1) * 256.0,
                minPixelY = level.minOf { it.xyzY } * 256.0,
                maxPixelY = (level.maxOf { it.xyzY } + 1) * 256.0
            )
        }
    }
}
