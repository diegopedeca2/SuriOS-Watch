package com.suri.pipsurios

import com.suri.pipsurios.geiger.RadsInclination
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RadsV2Test {
    @Test fun horizontalIsCriticalAndFortyFiveDegreesIsReference() {
        val horizontal = floatArrayOf(1f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f)
        val fortyFive = floatArrayOf(1f, 0f, 0f, 0f, .7071067f, -.7071067f, 0f, .7071067f, .7071067f)
        assertEquals(1f, RadsInclination.levelFromRotationMatrix(horizontal), .001f)
        assertEquals(0f, RadsInclination.levelFromRotationMatrix(fortyFive), .001f)
    }

    @Test fun smoothingMovesTowardMeasurementWithoutJumping() {
        val result = RadsInclination.smooth(0f, 1f)
        assertTrue(result in 0f..1f)
        assertTrue(result < 1f)
    }
}
