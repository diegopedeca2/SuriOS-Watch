package com.suri.pipsurios

import com.suri.pipsurios.prs.DensityCloud
import com.suri.pipsurios.ui.screens.probabilityFogDensity
import org.junit.Assert.assertTrue
import org.junit.Test

class ProbabilityFogTest {
    @Test
    fun likelyAreaKeepsMoreFogThanLowProbabilityArea() {
        val cloud = DensityCloud(
            radialCenterFraction = 0.5f,
            radialSpreadFraction = 0.1f,
            confidence = 0.72f
        )

        assertTrue(
            probabilityFogDensity(0.5f, cloud) >
                probabilityFogDensity(1.2f, cloud)
        )
    }

    @Test
    fun noContactKeepsTheMapCovered() {
        assertTrue(probabilityFogDensity(0.5f, null) > 0.8f)
    }
}
