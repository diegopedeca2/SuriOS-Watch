package com.suri.pipsurios.geiger

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.math.roundToInt

/**
 * Mixes the three RADS audio layers according to the visible 0..10 meter
 * level. The individual stream volumes stay fixed; the layers themselves
 * provide the change in character and intensity.
 */
class RadsClickSound(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(AUDIO_LAYER_COUNT)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIds = IntArray(AUDIO_LAYER_COUNT)
    private val loaded = BooleanArray(AUDIO_LAYER_COUNT)
    private val streamIds = IntArray(AUDIO_LAYER_COUNT)
    private var released = false

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            val layer = soundIds.indexOf(sampleId)
            if (layer >= 0 && status == 0) loaded[layer] = true
        }
        AUDIO_ASSET_PATHS.forEachIndexed { layer, path ->
            soundIds[layer] = runCatching {
                context.assets.openFd(path).use { descriptor ->
                    soundPool.load(descriptor, 1)
                }
            }.getOrDefault(0)
        }
    }

    /**
     * Keeps the correct layer(s) looping for the current normalized meter
     * level. At displayed levels 3 and 6 the adjacent layers overlap.
     */
    fun play(level: Float) {
        if (released) return
        val meterLevel = meterLevel(level)
        val desiredLayers = audioLayersForMeterLevel(meterLevel)
        if (desiredLayers.isEmpty()) {
            stop()
            return
        }

        for (layer in 0 until AUDIO_LAYER_COUNT) {
            val shouldPlay = layer in desiredLayers
            if (!shouldPlay && streamIds[layer] != 0) {
                runCatching { soundPool.stop(streamIds[layer]) }
                streamIds[layer] = 0
            }
            if (shouldPlay && streamIds[layer] == 0 && loaded[layer] && soundIds[layer] != 0) {
                // Keep each layer at a fixed volume. The sample selection and
                // overlap, rather than loudness, express the meter intensity.
                streamIds[layer] = soundPool.play(
                    soundIds[layer],
                    1.0f,
                    1.0f,
                    1,
                    -1,
                    1.0f
                )
            }
        }
    }

    fun stop() {
        streamIds.forEachIndexed { layer, streamId ->
            if (streamId != 0) {
                runCatching { soundPool.stop(streamId) }
                streamIds[layer] = 0
            }
        }
    }

    fun release() {
        if (released) return
        released = true
        stop()
        soundPool.release()
    }

    companion object {
        const val AUDIO_LAYER_COUNT = 3
        val AUDIO_ASSET_PATHS = arrayOf(
            "sounds/1.mp3",
            "sounds/2.mp3",
            "sounds/3.mp3"
        )

        /** Converts the internal 0..1 value to the visible integer level 0..10. */
        fun meterLevel(level: Float): Int =
            (level.coerceIn(0f, 1f) * 10f).roundToInt().coerceIn(0, 10)

        /** Returns zero-based audio layers; transition levels intentionally overlap. */
        fun audioLayersForMeterLevel(level: Int): List<Int> = when (level.coerceIn(0, 10)) {
            0 -> emptyList()
            1, 2 -> listOf(0)
            3 -> listOf(0, 1)
            4, 5 -> listOf(1)
            6 -> listOf(1, 2)
            else -> listOf(2)
        }
    }
}
