package com.suri.pipsurios

import com.suri.pipsurios.geiger.RadsClickSound
import org.junit.Assert.assertEquals
import org.junit.Test

class RadsIntensityTest {
    @Test
    fun mapsNormalizedLevelToVisibleMeterLevel() {
        assertEquals(0, RadsClickSound.meterLevel(0f))
        assertEquals(3, RadsClickSound.meterLevel(0.3f))
        assertEquals(6, RadsClickSound.meterLevel(0.6f))
        assertEquals(10, RadsClickSound.meterLevel(1f))
    }

    @Test
    fun keepsLevelZeroSilent() {
        assertEquals(emptyList<Int>(), RadsClickSound.audioLayersForMeterLevel(0))
    }

    @Test
    fun selectsOneLayerBetweenTransitions() {
        assertEquals(listOf(0), RadsClickSound.audioLayersForMeterLevel(1))
        assertEquals(listOf(0), RadsClickSound.audioLayersForMeterLevel(2))
        assertEquals(listOf(1), RadsClickSound.audioLayersForMeterLevel(4))
        assertEquals(listOf(1), RadsClickSound.audioLayersForMeterLevel(5))
        assertEquals(listOf(2), RadsClickSound.audioLayersForMeterLevel(7))
        assertEquals(listOf(2), RadsClickSound.audioLayersForMeterLevel(10))
    }

    @Test
    fun overlapsAdjacentLayersAtTransitionLevels() {
        assertEquals(listOf(0, 1), RadsClickSound.audioLayersForMeterLevel(3))
        assertEquals(listOf(1, 2), RadsClickSound.audioLayersForMeterLevel(6))
    }
}
