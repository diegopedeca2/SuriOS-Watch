package com.suri.pipsurios

import com.suri.pipsurios.sonar.BleObservation
import com.suri.pipsurios.sonar.ContactState
import com.suri.pipsurios.sonar.ContactTracker
import com.suri.pipsurios.sonar.ProximityCategory
import com.suri.pipsurios.sonar.ProximityClassifier
import com.suri.pipsurios.sonar.RssiFilter
import com.suri.pipsurios.sonar.SonarTuning
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProximitySonarTest {
    @Test
    fun rssiSmoothingMovesPartwayTowardNewReading() {
        val smoothed = RssiFilter.smooth(previous = -80f, current = -40, alpha = 0.25f)

        assertEquals(-70f, smoothed)
        assertTrue(smoothed > -80f)
        assertTrue(smoothed < -40f)
    }

    @Test
    fun proximityThresholdsCoverAllCategories() {
        assertEquals(ProximityCategory.VERY_CLOSE, ProximityClassifier.classify(-55f))
        assertEquals(ProximityCategory.CLOSE, ProximityClassifier.classify(-56f))
        assertEquals(ProximityCategory.MEDIUM, ProximityClassifier.classify(-68f))
        assertEquals(ProximityCategory.FAR, ProximityClassifier.classify(-79f))
    }

    @Test
    fun createsAndMaintainsContactWithStableIdentityAndAngle() {
        val tracker = ContactTracker()
        val first = tracker.observe(BleObservation("TEMP-A", -72, 1_000L))
        val updated = tracker.observe(BleObservation("TEMP-A", -60, 2_000L))

        assertEquals(1, tracker.snapshot().contacts.size)
        assertEquals(1_000L, updated.firstSeen)
        assertEquals(2_000L, updated.lastSeen)
        assertEquals(-60, updated.currentRssi)
        assertNotEquals(updated.currentRssi.toFloat(), updated.smoothedRssi)
        assertEquals(first.visualAngleDegrees, updated.visualAngleDegrees)
    }

    @Test
    fun expiresOnlyContactsPastConfiguredTimeout() {
        val tracker = ContactTracker()
        tracker.observe(BleObservation("OLD", -70, 1_000L))
        tracker.observe(BleObservation("CURRENT", -70, 5_000L))

        tracker.expire(1_000L + SonarTuning.CONTACT_EXPIRY_MILLIS + 1L)

        assertEquals(listOf("CURRENT"), tracker.snapshot().contacts.map { it.temporaryId })
    }

    @Test
    fun calibrationBuildsBaselineAndLaterContactIsNew() {
        val tracker = ContactTracker()
        tracker.startCalibration()
        val baseline = tracker.observe(BleObservation("KNOWN", -60, 1_000L))
        tracker.finishCalibration()
        val knownAgain = tracker.observe(BleObservation("KNOWN", -62, 2_000L))
        val newcomer = tracker.observe(BleObservation("NEWCOMER", -65, 2_000L))

        assertEquals(ContactState.BACKGROUND, baseline.state)
        assertEquals(ContactState.BACKGROUND, knownAgain.state)
        assertEquals(ContactState.NEW, newcomer.state)
        assertEquals(1, tracker.snapshot().newContactCount)
    }
}
