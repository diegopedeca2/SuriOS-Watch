package com.suri.pipsurios.geiger

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Short, original Geiger-style crackle: one detection is a small cluster of
 * irregular micro-discharges, rather than one isolated click. Several
 * variants avoid the artificial effect of repeating exactly the same burst.
 */
class RadsClickSound {
    private val variantRandom = Random.Default
    private val tracks = Array(CLICK_VARIANTS) { variant -> createTrack(variant) }

    fun play() {
        val audio = tracks[variantRandom.nextInt(tracks.size)]
        if (audio == null) return
        runCatching {
            if (audio.playState != AudioTrack.PLAYSTATE_STOPPED) {
                audio.stop()
            }
            audio.reloadStaticData()
            audio.play()
        }
    }

    fun stop() {
        tracks.forEach { audio ->
            if (audio != null) {
                runCatching { audio.stop() }
            }
        }
    }

    fun release() {
        tracks.forEach { audio ->
            if (audio != null) {
                runCatching { audio.release() }
            }
        }
    }

    private fun createTrack(variant: Int): AudioTrack? {
        val samples = createClickSamples(variant)
        var newTrack: AudioTrack? = null
        return try {
            newTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setTransferMode(AudioTrack.MODE_STATIC)
                .setBufferSizeInBytes(samples.size * BYTES_PER_SAMPLE)
                .build()

            val written = newTrack.write(samples, 0, samples.size)
            if (written != samples.size) {
                newTrack.release()
                null
            } else {
                newTrack
            }
        } catch (_: RuntimeException) {
            newTrack?.release()
            null
        }
    }

    private fun createClickSamples(variant: Int): ShortArray {
        val samples = ShortArray((CLICK_DURATION_SECONDS * SAMPLE_RATE).toInt())
        val random = Random(RANDOM_SEED + variant)
        val pitch = 2_450.0 + (variant - 2) * 90.0
        val gain = 0.84 + (variant % 3) * 0.035
        val hitCount = 7 + (variant % 4)
        val hitTimes = DoubleArray(hitCount)
        val hitPitches = DoubleArray(hitCount)
        val hitGains = DoubleArray(hitCount)
        var nextHitTime = 0.001 + random.nextDouble() * 0.005
        for (hit in 0 until hitCount) {
            hitTimes[hit] = nextHitTime
            hitPitches[hit] = pitch * (0.84 + random.nextDouble() * 0.34)
            hitGains[hit] = 0.42 + random.nextDouble() * 0.36
            // Alternate between tight clusters and longer gaps. This makes
            // the burst feel like an irregular scrape instead of a metronome.
            val clusteredGap = random.nextDouble() < 0.34
            val gap = if (clusteredGap) {
                0.0015 + random.nextDouble() * 0.0055
            } else {
                0.009 + random.nextDouble() * 0.021
            }
            nextHitTime += gap
        }
        for (index in samples.indices) {
            val time = index.toDouble() / SAMPLE_RATE
            var crackle = 0.0
            for (hit in hitTimes.indices) {
                val sinceHit = time - hitTimes[hit]
                if (sinceHit >= 0.0) {
                    // The first part is the rough scrape; the second is the
                    // short metallic body left by the detector circuit.
                    val roughEnvelope = exp(-sinceHit * 1_100.0)
                    val metalEnvelope = exp(-sinceHit * 235.0)
                    val rough =
                        (random.nextDouble() * 2.0 - 1.0) *
                            hitGains[hit] * 0.42 * roughEnvelope
                    val metal =
                        sin(2.0 * PI * hitPitches[hit] * sinceHit) *
                            hitGains[hit] * 0.28 * metalEnvelope
                    crackle += rough + metal
                }
            }
            // A low-level, quickly fading texture glues the micro-hits into a
            // single rough burst instead of a row of clean beeps.
            val textureEnvelope = exp(-time * 72.0)
            val texture = (random.nextDouble() * 2.0 - 1.0) * 0.13 * textureEnvelope
            val sample = (crackle + texture) * gain
            samples[index] = (sample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
        }
        return samples
    }

    private companion object {
        const val SAMPLE_RATE = 44_100
        const val BYTES_PER_SAMPLE = 2
        const val CLICK_DURATION_SECONDS = 0.105
        const val CLICK_VARIANTS = 18
        const val RANDOM_SEED = 0x52414453
    }
}
