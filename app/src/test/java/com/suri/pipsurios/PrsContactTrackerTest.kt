package com.suri.pipsurios

import com.suri.pipsurios.prs.BleObservation
import com.suri.pipsurios.prs.PrsContactTracker
import com.suri.pipsurios.prs.PrsProximityBand
import com.suri.pipsurios.prs.PrsTrend
import com.suri.pipsurios.prs.PrsTuningConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PrsContactTrackerTest {
    private val tuning = PrsTuningConfig(
        evaluationIntervalMillis = 1_000L,
        smoothingAlpha = 1f,
        historyWindowSize = 5,
        minimumSamplesForTrend = 3,
        minimumTrendDurationMillis = 2_000L,
        minimumSignificantVariationDb = 4f,
        stableVariationDb = 1.5f,
        hysteresisDb = 1f,
        trendConfirmationEvaluations = 2,
        stableConfirmationEvaluations = 2,
        contactExpiryMillis = 8_000L,
        nearMinimumRssi = -75f,
        mediumMinimumRssi = -88f
    )

    @Test
    fun rawObservationDoesNotImmediatelyChangeProcessedSignal() {
        val tracker = PrsContactTracker(tuning)
        tracker.observe(observation("target", -90, 0L))
        tracker.evaluate(0L, force = true)
        val before = tracker.snapshot().contacts.single()

        tracker.observe(observation("target", -70, 1_000L))
        val pending = tracker.snapshot().contacts.single()

        assertEquals(-90f, before.processed.smoothedRssi)
        assertEquals(-90f, pending.processed.smoothedRssi)
        assertEquals(1, pending.processed.history.size)
    }

    @Test
    fun sustainedIncreaseRequiresWindowAndConfirmation() {
        val tracker = PrsContactTracker(tuning)
        listOf(-90, -84, -78).forEachIndexed { index, rssi ->
            val time = index * 1_000L
            tracker.observe(observation("target", rssi, time))
            tracker.evaluate(time, force = true)
        }

        assertEquals(PrsTrend.INSUFFICIENT_DATA, tracker.snapshot().contacts.single().inference.trend)

        tracker.observe(observation("target", -72, 3_000L))
        tracker.evaluate(3_000L, force = true)
        assertEquals(PrsTrend.APPROACHING, tracker.snapshot().contacts.single().inference.trend)
    }

    @Test
    fun secondContactUsesIdentifierAndUnknownDisplayLabel() {
        val tracker = PrsContactTracker(tuning)
        val id = tracker.observe(
            BleObservation(
                temporaryId = "CONTACT-001",
                deviceIdentifier = "aa:bb:cc:dd:ee:ff",
                rssi = -80,
                observedAt = 0L,
                deviceName = " "
            )
        )
        tracker.evaluate(0L, force = true)
        val contact = tracker.snapshot().contacts.single()

        assertEquals("AA:BB:CC:DD:EE:FF", id)
        assertEquals("AA:BB:CC:DD:EE:FF", contact.contactId)
        assertEquals("UNKNOWN 01", contact.displayName)
        assertEquals(PrsProximityBand.MEDIUM, contact.inference.proximity)
        assertEquals(1f, contact.inference.densityCloud.azimuthCoverage)
    }

    @Test
    fun expiryRemovesStaleContact() {
        val tracker = PrsContactTracker(tuning)
        tracker.observe(observation("target", -70, 0L))
        tracker.evaluate(0L, force = true)

        assertTrue(tracker.expire(8_001L).contains("TARGET"))
        assertTrue(tracker.snapshot().contacts.isEmpty())
    }

    @Test
    fun movementAwayIsReportedAfterConfirmedDecrease() {
        val tracker = PrsContactTracker(tuning)
        listOf(-60, -60, -60, -60).forEachIndexed { index, rssi ->
            val time = index * 1_000L
            tracker.observe(observation("target", rssi, time))
            tracker.evaluate(time, force = true)
        }
        listOf(-70, -80, -90, -95).forEachIndexed { index, rssi ->
            val time = (index + 4) * 1_000L
            tracker.observe(observation("target", rssi, time))
            tracker.evaluate(time, force = true)
        }

        assertEquals(PrsTrend.MOVING_AWAY, tracker.snapshot().contacts.single().inference.trend)
    }

    private fun observation(id: String, rssi: Int, time: Long) = BleObservation(
        temporaryId = id,
        deviceIdentifier = id,
        rssi = rssi,
        observedAt = time,
        observedAtEpochMillis = time
    )
}
