package com.suri.pipsurios.geiger

import kotlin.math.max
import kotlin.random.Random

enum class GeigerStatus {
    BACKGROUND,
    LOW,
    MODERATE,
    HIGH,
    CRITICAL
}

data class GeigerSnapshot(
    val level: Float,
    val needleLevel: Float,
    val status: GeigerStatus
)

class GeigerEngine(
    private val random: Random = Random.Default
) {
    private var level = 0f

    fun update(volumeUpPressed: Boolean, elapsedSeconds: Float): GeigerSnapshot {
        val rate = if (volumeUpPressed) RISE_PER_SECOND else -FALL_PER_SECOND
        level = (level + rate * elapsedSeconds).coerceIn(0f, 1f)

        val jitterRange = BASE_JITTER + level * LEVEL_JITTER
        val jitter = (random.nextFloat() * 2f - 1f) * jitterRange
        val needleLevel = max(0f, level + jitter).coerceAtMost(1f)

        return GeigerSnapshot(level, needleLevel, statusFor(level))
    }

    fun snapshot(): GeigerSnapshot = GeigerSnapshot(
        level = level,
        needleLevel = level,
        status = statusFor(level)
    )

    companion object {
        const val RISE_PER_SECOND = 0.18f
        const val FALL_PER_SECOND = 0.16f
        private const val BASE_JITTER = 0.008f
        private const val LEVEL_JITTER = 0.025f

        fun statusFor(level: Float): GeigerStatus = when {
            level < 0.12f -> GeigerStatus.BACKGROUND
            level < 0.30f -> GeigerStatus.LOW
            level < 0.55f -> GeigerStatus.MODERATE
            level < 0.80f -> GeigerStatus.HIGH
            else -> GeigerStatus.CRITICAL
        }
    }
}
