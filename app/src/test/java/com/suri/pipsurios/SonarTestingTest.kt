package com.suri.pipsurios

import com.suri.pipsurios.sonar.BleObservation
import com.suri.pipsurios.sonar.ContactState
import com.suri.pipsurios.sonar.ContactTracker
import com.suri.pipsurios.sonar.ProximityCategory
import com.suri.pipsurios.sonar.SonarTuning
import com.suri.pipsurios.sonartesting.CalibrationCsv
import com.suri.pipsurios.sonartesting.CalibrationEvent
import com.suri.pipsurios.sonartesting.CalibrationPositions
import com.suri.pipsurios.sonartesting.CalibrationRecord
import com.suri.pipsurios.sonartesting.CalibrationSample
import com.suri.pipsurios.sonartesting.CalibrationTarget
import com.suri.pipsurios.sonartesting.CalibrationTestType
import com.suri.pipsurios.sonartesting.SonarTestingRecorder
import com.suri.pipsurios.sonartesting.SonarTestingRepository
import com.suri.pipsurios.sonartesting.SonarTestingTuning
import com.suri.pipsurios.sonartesting.TestingNodeMode
import com.suri.pipsurios.sonartesting.ManualCalibrationPosition
import com.suri.pipsurios.sonartesting.IdentificationResult
import com.suri.pipsurios.sonartesting.SonarIdentificationTuning
import com.suri.pipsurios.sonartesting.SonarTargetIdentifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.file.Files

class SonarTestingTest {
    @Test fun manualPositionsNormalizeEveryDirectionAndAllowZero() {
        assertEquals("N5 / E3", ManualCalibrationPosition(northMeters=5,eastMeters=3).display())
        assertEquals("S10 / O5", ManualCalibrationPosition(southMeters=10,westMeters=5).display())
        assertEquals("N0 / E0", ManualCalibrationPosition().display())
        assertEquals(-10, ManualCalibrationPosition(southMeters=10).signedNorthMeters)
        assertEquals(-5, ManualCalibrationPosition(westMeters=5).signedEastMeters)
    }

    @Test fun manualPositionsRejectOppositeDirectionsOnSameAxis() {
        assertFalse(ManualCalibrationPosition(northMeters=1,southMeters=1).isValid)
        assertFalse(ManualCalibrationPosition(eastMeters=1,westMeters=1).isValid)
        assertTrue(ManualCalibrationPosition(northMeters=1,westMeters=1).isValid)
    }

    @Test fun identificationSelectsOnlyClearlyDominantCandidate() {
        val result = SonarTargetIdentifier.identify(
            mapOf("near" to listOf(-35,-36,-34,-35), "background" to listOf(-62,-60,-61)),
            baselineIds=setOf("background")
        ) as IdentificationResult.Dominant
        assertEquals("near", result.candidate.temporaryId)
        assertTrue(result.candidate.newDuringWindow)
    }

    @Test fun identificationReturnsAtMostThreeCandidatesWhenAmbiguous() {
        val result = SonarTargetIdentifier.identify(
            mapOf("a" to listOf(-42,-43),"b" to listOf(-44,-43),"c" to listOf(-45,-44),"d" to listOf(-46,-45)),
            baselineIds=emptySet()
        ) as IdentificationResult.Ambiguous
        assertEquals(SonarIdentificationTuning.MAX_AMBIGUOUS_CANDIDATES,result.candidates.size)
    }

    @Test fun identificationDoesNotInventCandidateWithoutStableObservations() {
        assertEquals(IdentificationResult.None,SonarTargetIdentifier.identify(mapOf("single" to listOf(-30)),emptySet()))
    }
    @Test fun sessionIdsArePersistentAndCollisionSafe() {
        val root = Files.createTempDirectory("sonar-testing").toFile()
        val first = SonarTestingRepository(root).createSession(1).sessionId
        val second = SonarTestingRepository(root).createSession(2).sessionId
        assertEquals("CAL-001", first)
        assertEquals("CAL-002", second)
        assertEquals(second, SonarTestingRepository(root).readSession(second)?.sessionId)
    }

    @Test fun fixedPositionCatalogIsExactAndAllowsRepeatedSelection() {
        assertEquals((1..10).map { "D$it" }, CalibrationPositions.all.map { it.id })
        assertEquals(CalibrationPositions.all[0], listOf(CalibrationPositions.all[0], CalibrationPositions.all[0])[1])
        assertEquals(-3, CalibrationPositions.all[2].eastMeters)
        assertEquals(-10, CalibrationPositions.all[7].northMeters)
    }

    @Test fun rawAndStableSmoothedValuesAreRecordedWithoutReconstruction() {
        val tracker = ContactTracker()
        val records = mutableListOf<CalibrationRecord>()
        val recorder = SonarTestingRecorder(records::add)
        recorder.start(sample())
        tracker.observe(BleObservation("CONTACT-001", -80, 1_000))
        val observation = BleObservation("CONTACT-001", -40, 2_000)
        val contact = tracker.observe(observation)
        recorder.observe(observation, contact, 12_000)
        assertEquals(-40, records.single().rawRssi)
        assertEquals(contact.smoothedRssi, records.single().smoothedRssi)
        assertEquals(contact.proximity, records.single().category)
        assertEquals(ContactState.BACKGROUND, records.single().state)
    }

    @Test fun completionLossRecoveryAndMovementAreExplicitEvents() {
        val records = mutableListOf<CalibrationRecord>()
        val recorder = SonarTestingRecorder(records::add)
        val tracker = ContactTracker()
        recorder.start(sample(type = CalibrationTestType.MOVEMENT))
        val firstObs = BleObservation("CONTACT-001", -70, 1_000)
        val contact = tracker.observe(firstObs)
        recorder.observe(firstObs, contact, 11_000)
        recorder.contactExpired(contact, 20_000)
        val recoveredObs = BleObservation("CONTACT-001", -68, 11_000)
        recorder.observe(recoveredObs, tracker.observe(recoveredObs), 21_000)
        recorder.complete(22_000)
        assertEquals(listOf(CalibrationEvent.OBSERVATION, CalibrationEvent.CONTACT_LOST,
            CalibrationEvent.CONTACT_RECOVERED, CalibrationEvent.OBSERVATION, CalibrationEvent.SAMPLE_COMPLETE), records.map { it.event })
        assertFalse(recorder.isActive())
    }

    @Test fun csvHasStableColumnsUtf8FriendlyContentAndEscapesNotes() {
        val record = record(notes = "pasó, \"persona\"\nobjetivo movido")
        val csv = CalibrationCsv.encode(listOf(record))
        assertEquals(23, CalibrationCsv.columns.size)
        assertTrue(csv.startsWith("session_id,sample_id,test_type"))
        assertTrue(csv.contains("\"pasó, \"\"persona\"\"\nobjetivo movido\""))
        assertTrue(csv.contains("-61"))
    }

    @Test fun dualNodeSampleKeepsProbeMetadataInCsv() {
        val record = record().copy(
            nodeMode = TestingNodeMode.A56_AND_WATCH,
            probeSessionId = "RPR-123-001",
            probeLink = "CONNECTED"
        )
        val csv = CalibrationCsv.encode(listOf(record))
        assertTrue(csv.contains("A56_AND_WATCH"))
        assertTrue(csv.contains("RPR-123-001"))
        assertTrue(csv.contains("CONNECTED"))
    }

    @Test fun repositorySeparatesMetadataAndObservationsAndExportsCsv() {
        val root = Files.createTempDirectory("sonar-testing-store").toFile()
        val repository = SonarTestingRepository(root)
        val session = repository.createSession(1)
        repository.append(record(sessionId = session.sessionId))
        assertTrue(root.resolve("CAL-001/session.properties").isFile)
        val export = repository.exportFile(session.sessionId)!!
        assertEquals("observations.csv", export.name)
        assertTrue(export.readText(Charsets.UTF_8).contains("OBSERVATION"))
    }

    @Test fun configuredStaticDurationAndStableExpiryRemainUnchanged() {
        assertEquals(30_000L, SonarTestingTuning.STATIC_SAMPLE_DURATION_MILLIS)
        val tracker = ContactTracker()
        tracker.observe(BleObservation("A", -70, 1_000))
        assertTrue(tracker.expire(1_000 + SonarTuning.CONTACT_EXPIRY_MILLIS).isEmpty())
        assertEquals("A", tracker.snapshot().contacts.single().temporaryId)
        assertEquals("A", tracker.expire(1_001 + SonarTuning.CONTACT_EXPIRY_MILLIS).single().temporaryId)
    }

    private fun sample(type: CalibrationTestType = CalibrationTestType.STATIC) = CalibrationSample(
        "CAL-001-S001", "CAL-001", type, CalibrationTarget.FLIP_6,
        if (type == CalibrationTestType.STATIC) CalibrationPositions.all.first() else null,
        "CONTACT-001", 10_000, ""
    )

    private fun record(sessionId: String = "CAL-001", notes: String = "") = CalibrationRecord(
        sessionId, "$sessionId-S001", CalibrationTestType.STATIC, "FLIP 6", "D1", 2, 0,
        "Vista despejada", 11_000, 1_000, -61, -63.4f, ProximityCategory.CLOSE,
        ContactState.NEW, 0, 1, CalibrationEvent.OBSERVATION, notes
    )
}
