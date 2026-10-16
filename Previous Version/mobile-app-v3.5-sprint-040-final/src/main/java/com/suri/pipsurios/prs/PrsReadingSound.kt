package com.suri.pipsurios.prs

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/** Plays the short acoustic marker used for each completed P.R.S. reading. */
class PrsReadingSound(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private var soundId = 0
    private var loaded = false
    private var released = false

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (sampleId == soundId && status == 0) loaded = true
        }
        soundId = runCatching {
            context.assets.openFd(AUDIO_ASSET_PATH).use { descriptor ->
                soundPool.load(descriptor, 1)
            }
        }.getOrDefault(0)
    }

    fun play() {
        if (!released && loaded && soundId != 0) {
            soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        if (released) return
        released = true
        soundPool.release()
    }

    companion object {
        const val AUDIO_ASSET_PATH = "sounds/sonar.mp3"
    }
}
