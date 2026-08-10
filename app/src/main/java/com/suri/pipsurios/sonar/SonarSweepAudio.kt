package com.suri.pipsurios.sonar

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.suri.pipsurios.R

class SonarSweepAudio(context: Context) {
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val loadedSounds = mutableSetOf<Int>()
    private var activeStreamId = 0
    private val sweepSoundId: Int
    private val backgroundContactSoundId: Int
    private val newContactSoundId: Int

    init {
        soundPool.setOnLoadCompleteListener { _, sampleId, status ->
            if (status == 0) loadedSounds += sampleId
        }
        sweepSoundId = soundPool.load(context, R.raw.sonar_pulse, 1)
        backgroundContactSoundId = soundPool.load(context, R.raw.sonar_contact_background, 1)
        newContactSoundId = soundPool.load(context, R.raw.sonar_contact_new, 1)
    }

    fun playSweepPulse() {
        play(sweepSoundId, volume = 0.42f)
    }

    fun playBackgroundContact() {
        play(backgroundContactSoundId, volume = 0.34f)
    }

    fun playNewContact() {
        play(newContactSoundId, volume = 0.68f)
    }

    private fun play(soundId: Int, volume: Float) {
        if (soundId !in loadedSounds) return
        stop()
        activeStreamId = soundPool.play(soundId, volume, volume, 1, 0, 1f)
    }

    fun stop() {
        if (activeStreamId != 0) soundPool.stop(activeStreamId)
        activeStreamId = 0
    }

    fun release() {
        stop()
        soundPool.release()
        loadedSounds.clear()
    }
}
