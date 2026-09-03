package com.suri.pipsurios.geiger

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlin.random.Random

class ClickScheduler(
    private val random: Random = Random.Default
) {
    private val clickSound = RadsClickSound()
    private var released = false

    suspend fun run(levelProvider: () -> Float) {
        var playImmediately = true
        while (currentCoroutineContext().isActive && !released) {
            val level = levelProvider().coerceIn(0f, 1f)
            if (level <= STOP_LEVEL) {
                stop()
                playImmediately = true
                delay(INACTIVE_POLL_MS)
                continue
            }
            if (!playImmediately) {
                delay(intervalMillis(level, random.nextFloat()))
                currentCoroutineContext().ensureActive()
            } else {
                playImmediately = false
            }
            val levelAtPlayback = levelProvider().coerceIn(0f, 1f)
            if (levelAtPlayback <= STOP_LEVEL) {
                playImmediately = true
                continue
            }
            clickSound.play()
        }
    }

    fun stop() {
        clickSound.stop()
    }

    fun release() {
        released = true
        stop()
        clickSound.release()
    }

    companion object {
        fun intervalMillis(level: Float, randomUnit: Float): Long {
            val normalized = level.coerceIn(0f, 1f)
            val base = MAX_INTERVAL_MS - ((MAX_INTERVAL_MS - MIN_INTERVAL_MS) * normalized).toLong()
            val variation = ((randomUnit.coerceIn(0f, 1f) * 2f - 1f) * base * VARIATION_FRACTION).toLong()
            return (base + variation).coerceAtLeast(MIN_INTERVAL_MS)
        }

        const val MAX_INTERVAL_MS = 1_600L
        const val MIN_INTERVAL_MS = 120L
        const val VARIATION_FRACTION = 0.12f
        private const val INACTIVE_POLL_MS = 100L
        private const val STOP_LEVEL = 0.005f
    }
}
