package com.suri.pipsurios.geiger

import kotlin.math.acos
import kotlin.math.abs

enum class RadsMode { MANUAL, SENSOR }

object RadsInclination {
    fun levelFromRotationMatrix(matrix: FloatArray): Float {
        if (matrix.size < 9) return 0f
        val horizontalAngle = Math.toDegrees(acos(abs(matrix[8].coerceIn(-1f, 1f)).toDouble())).toFloat()
        return ((45f - horizontalAngle) / 45f).coerceIn(0f, 1f)
    }

    fun smooth(previous: Float, measurement: Float, alpha: Float = 0.16f): Float =
        previous + alpha.coerceIn(0f, 1f) * (measurement.coerceIn(0f, 1f) - previous)
}
