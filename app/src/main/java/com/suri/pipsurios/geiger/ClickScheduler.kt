package com.suri.pipsurios.geiger

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.suri.pipsurios.R
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlin.random.Random

class ClickScheduler(
    context: Context,
    private val random: Random = Random.Default
) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var loaded = false
    private var activeStreamId = 0
    private val soundId: Int

    init {
        soundPool.setOnLoadCompleteListener { _, _, status ->
            if (status == 0) loaded = true
        }
        soundId = soundPool.load(context, R.raw.geiger_click, 1)
    }

    suspend fun run(levelProvider: () -> Float) {
        while (currentCoroutineContext().isActive) {
            val level = levelProvider().coerceIn(0f, 1f)
            if (level <= STOP_LEVEL) {
                stop()
                return
            }
            delay(intervalMillis(level, random.nextFloat()))
            currentCoroutineContext().ensureActive()
            val levelAtPlayback = levelProvider().coerceIn(0f, 1f)
            if (levelAtPlayback <= STOP_LEVEL) {
                stop()
                return
            }
            if (loaded) {
                if (activeStreamId != 0) soundPool.stop(activeStreamId)
                activeStreamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
    }

    fun stop() {
        if (activeStreamId != 0) soundPool.stop(activeStreamId)
        activeStreamId = 0
    }

    fun release() {
        stop()
        soundPool.release()
        loaded = false
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
        private const val STOP_LEVEL = 0.005f
    }
}
