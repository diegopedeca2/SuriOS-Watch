package com.suri.pipsurios.geiger

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.suri.pipsurios.R
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
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
            delay(intervalMillis(level, random.nextFloat()))
            if (loaded) {
                if (activeStreamId != 0) soundPool.stop(activeStreamId)
                activeStreamId = soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
        }
    }

    fun release() {
        if (activeStreamId != 0) soundPool.stop(activeStreamId)
        soundPool.release()
        activeStreamId = 0
        loaded = false
    }

    companion object {
        fun intervalMillis(level: Float, randomUnit: Float): Long {
            val base = when (GeigerEngine.statusFor(level)) {
                GeigerStatus.BACKGROUND -> 1_600L
                GeigerStatus.LOW -> 900L
                GeigerStatus.MODERATE -> 520L
                GeigerStatus.HIGH -> 260L
                GeigerStatus.CRITICAL -> 120L
            }
            val variation = ((randomUnit.coerceIn(0f, 1f) * 2f - 1f) * base * 0.18f).toLong()
            return (base + variation).coerceAtLeast(80L)
        }
    }
}
