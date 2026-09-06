package com.suri.pipsurios

import com.suri.pipsurios.prs.BleObservation
import com.suri.pipsurios.prs.DensityCloud
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsInference
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.prs.PrsProcessedSignal
import com.suri.pipsurios.prs.PrsProximityBand
import com.suri.pipsurios.prs.PrsTargetAreaEstimator
import com.suri.pipsurios.prs.PrsTargetSide
import com.suri.pipsurios.prs.PrsTrend
import com.suri.pipsurios.prs.RssiHistoryPoint
import com.suri.pipsurios.terrain.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrsTargetAreaEstimatorTest {
    @Test
    fun strongerSignalAfterClockwiseTurnVotesRightAndBuildsTargetArea() {
        val estimator = PrsTargetAreaEstimator()
        val observerPoint = GeoPoint(40.0, -3.0)

        estimator.update(
            contact = contact(rssi = -82, time = 0L),
            measurementPoint = observerPoint,
            headingDegrees = 0f,
            gpsAccuracyMeters = 8f
        )
        val estimate = estimator.update(
            contact = contact(rssi = -72, time = 3_000L),
            measurementPoint = observerPoint,
            headingDegrees = 30f,
            gpsAccuracyMeters = 8f
        )

        assertEquals(PrsTargetSide.RIGHT, estimate.side)
        assertEquals(2, estimate.sampleCount)
        assertNotNull(estimate.center)
        assertTrue(estimate.center!!.longitude > observerPoint.longitude)
        assertTrue(estimate.radiusMeters in 0f..450f)
    }

    @Test
    fun sameEvaluatedReadingIsNotCountedTwice() {
        val estimator = PrsTargetAreaEstimator()
        val observerPoint = GeoPoint(40.0, -3.0)
        val reading = contact(rssi = -82, time = 0L)

        val first = estimator.update(reading, observerPoint, 0f, 8f)
        val duplicate = estimator.update(reading, observerPoint, 0f, 8f)

        assertEquals(1, first.sampleCount)
        assertEquals(1, duplicate.sampleCount)
    }

    private fun contact(rssi: Int, time: Long): PrsContactSnapshot {
        val observation = BleObservation(
            temporaryId = "TARGET",
            deviceIdentifier = "TARGET",
            rssi = rssi,
            observedAt = time,
            observedAtEpochMillis = time
        )
        return PrsContactSnapshot(
            contactId = "TARGET",
            displayName = "TARGET",
            source = PrsObservationSource.A56,
            measured = observation,
            firstSeenElapsedMillis = 0L,
            sampleCount = 1,
            processed = PrsProcessedSignal(
                smoothedRssi = rssi.toFloat(),
                meanRssi = rssi.toFloat(),
                variationDb = 0f,
                history = listOf(
                    RssiHistoryPoint(
                        observedAtElapsedMillis = time,
                        observedAtEpochMillis = time,
                        rawRssi = rssi,
                        smoothedRssi = rssi.toFloat(),
                        variationFromPreviousDb = null
                    )
                )
            ),
            inference = PrsInference(
                trend = PrsTrend.STABLE,
                proximity = PrsProximityBand.MEDIUM,
                explanation = "TEST",
                densityCloud = DensityCloud(0.5f, 0.2f, 0.5f)
            )
        )
    }
}
