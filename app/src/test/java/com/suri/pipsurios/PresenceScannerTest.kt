package com.suri.pipsurios

import com.suri.pipsurios.sonar.BleObservation
import com.suri.pipsurios.sonar.PresenceAssessment
import com.suri.pipsurios.sonar.PresenceScanPhase
import com.suri.pipsurios.sonar.PresenceScannerSession
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PresenceScannerTest {
    @Test
    fun needsReferenceAndCompletedDoorScan() {
        val session = PresenceScannerSession()
        session.startDoor(0L)
        assertEquals(PresenceScanPhase.IDLE, session.snapshot(0L).phase)
        assertEquals(PresenceAssessment.NOT_READY, session.snapshot(0L).assessment)
    }

    @Test
    fun stableNewDevicesProduceProbableDeviceSignal() {
        val session = PresenceScannerSession()
        session.startReference(0L)
        observe(session, "background", -60, 100L)
        session.finishReference(8_000L)
        session.startDoor(8_000L)
        repeat(4) { observe(session, "new-a", -68, 8_100L + it) }
        repeat(4) { observe(session, "new-b", -80, 8_200L + it) }
        session.finishDoor(20_000L)

        val result = session.snapshot(20_000L)
        assertEquals(PresenceAssessment.PROBABLE_DEVICE_SIGNAL, result.assessment)
        assertEquals(2, result.newDeviceCount)
        assertEquals(2, result.stableNewDeviceCount)
        assertTrue(result.signalIndex > 50)
    }

    @Test
    fun onlyReferenceDevicesProduceNoDeviceSignal() {
        val session = PresenceScannerSession()
        session.startReference(0L)
        observe(session, "background", -55, 100L)
        session.finishReference(8_000L)
        session.startDoor(8_000L)
        repeat(5) { observe(session, "background", -57, 8_100L + it) }
        session.finishDoor(20_000L)

        val result = session.snapshot(20_000L)
        assertEquals(PresenceAssessment.NO_DEVICE_SIGNAL, result.assessment)
        assertEquals(0, result.newDeviceCount)
    }

    @Test
    fun transientNewDeviceProducesPossibleSignal() {
        val session = PresenceScannerSession()
        session.startReference(0L)
        session.finishReference(8_000L)
        session.startDoor(8_000L)
        observe(session, "new", -85, 8_100L)
        session.finishDoor(20_000L)

        val result = session.snapshot(20_000L)
        assertEquals(PresenceAssessment.POSSIBLE_DEVICE_SIGNAL, result.assessment)
        assertEquals(1, result.newDeviceCount)
        assertEquals(10, result.signalIndex)
    }

    @Test
    fun closePassFiltersWeakReadingsAndWidePassMarksNewPoints() {
        val session = PresenceScannerSession()
        session.startReference(0L)
        observe(session, "close", -58, 100L)
        observe(session, "far", -82, 200L)
        session.finishReference(6_000L)
        assertEquals(1, session.snapshot(6_000L).referenceDeviceCount)

        session.startDoor(6_000L)
        observe(session, "close", -60, 6_100L)
        observe(session, "far", -82, 6_200L)
        session.finishDoor(16_000L)

        val result = session.snapshot(16_000L)
        assertEquals(1, result.newDeviceCount)
        assertEquals(2, result.signalPoints.size)
        assertTrue(result.signalPoints.single { it.isNew }.rssi <= -80)
    }

    private fun observe(session: PresenceScannerSession, id: String, rssi: Int, time: Long) {
        session.observe(BleObservation(id, rssi, time))
    }
}
