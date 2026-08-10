package com.suri.pipsurios

import com.suri.pipsurios.geiger.ClickScheduler
import com.suri.pipsurios.geiger.GeigerEngine
import com.suri.pipsurios.geiger.GeigerStatus
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GeigerEngineTest {
    @Test
    fun startsAtMinimumInBackground() {
        val snapshot = GeigerEngine(Random(1)).snapshot()

        assertEquals(0f, snapshot.level)
        assertEquals(0f, snapshot.needleLevel)
        assertEquals(GeigerStatus.BACKGROUND, snapshot.status)
    }

    @Test
    fun holdingVolumeUpRaisesLevelProgressively() {
        val engine = GeigerEngine(Random(1))
        val first = engine.update(volumeUpPressed = true, elapsedSeconds = 0.1f)
        val second = engine.update(volumeUpPressed = true, elapsedSeconds = 0.1f)

        assertTrue(first.level > 0f)
        assertTrue(second.level > first.level)
        assertTrue(second.level < 1f)
    }

    @Test
    fun releasingVolumeUpReturnsGraduallyToBackground() {
        val engine = GeigerEngine(Random(1))
        repeat(30) { engine.update(volumeUpPressed = true, elapsedSeconds = 0.1f) }
        val raised = engine.snapshot().level
        val firstFall = engine.update(volumeUpPressed = false, elapsedSeconds = 0.1f)

        assertTrue(firstFall.level in 0f..<raised)
        repeat(100) { engine.update(volumeUpPressed = false, elapsedSeconds = 0.1f) }
        assertEquals(GeigerStatus.BACKGROUND, engine.snapshot().status)
        assertEquals(0f, engine.snapshot().level)
    }

    @Test
    fun statusThresholdsCoverEveryDisplayState() {
        assertEquals(GeigerStatus.BACKGROUND, GeigerEngine.statusFor(0f))
        assertEquals(GeigerStatus.LOW, GeigerEngine.statusFor(0.12f))
        assertEquals(GeigerStatus.MODERATE, GeigerEngine.statusFor(0.30f))
        assertEquals(GeigerStatus.HIGH, GeigerEngine.statusFor(0.55f))
        assertEquals(GeigerStatus.CRITICAL, GeigerEngine.statusFor(0.80f))
    }

    @Test
    fun clickRateGetsFasterWithoutBecomingConcurrent() {
        val background = ClickScheduler.intervalMillis(0f, 0.5f)
        val critical = ClickScheduler.intervalMillis(1f, 0.5f)

        assertTrue(background > critical)
        assertTrue(critical >= 80L)
    }
}
