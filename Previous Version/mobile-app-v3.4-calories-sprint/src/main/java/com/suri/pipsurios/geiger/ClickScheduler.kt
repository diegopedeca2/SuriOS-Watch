package com.suri.pipsurios.geiger

import android.content.Context
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive

class ClickScheduler(
    context: Context
) {
    private val clickSound = RadsClickSound(context)
    private var released = false

    suspend fun run(levelProvider: () -> Float) {
        while (currentCoroutineContext().isActive && !released) {
            val level = levelProvider().coerceIn(0f, 1f)
            if (level <= STOP_LEVEL) {
                stop()
                delay(INACTIVE_POLL_MS)
                continue
            }
            // The audio layers are continuous. Polling frequently keeps a
            // needle change and its layer change practically simultaneous.
            clickSound.play(level)
            delay(AUDIO_LEVEL_POLL_MS)
            currentCoroutineContext().ensureActive()
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
        private const val AUDIO_LEVEL_POLL_MS = 40L
        private const val INACTIVE_POLL_MS = 100L
        private const val STOP_LEVEL = 0.005f
    }
}
