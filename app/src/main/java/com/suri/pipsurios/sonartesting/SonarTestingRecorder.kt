package com.suri.pipsurios.sonartesting

import com.suri.pipsurios.sonar.BleObservation
import com.suri.pipsurios.sonar.SonarContact

class SonarTestingRecorder(private val sink: (CalibrationRecord) -> Unit) {
    private var active: CalibrationSample? = null
    private var scanCount = 0
    private var contactLost = false

    fun start(sample: CalibrationSample) {
        check(active == null) { "A sample is already active" }
        active = sample
        scanCount = 0
        contactLost = false
    }

    fun observe(observation: BleObservation, contact: SonarContact, timestampEpochMillis: Long) {
        val sample = active ?: return
        if (observation.temporaryId != sample.temporaryContactId) return
        scanCount++
        if (contactLost) {
            write(sample, timestampEpochMillis, CalibrationEvent.CONTACT_RECOVERED, contact = contact)
            contactLost = false
        }
        write(sample, timestampEpochMillis, CalibrationEvent.OBSERVATION, observation, contact)
    }

    fun contactExpired(contact: SonarContact, timestampEpochMillis: Long) {
        val sample = active ?: return
        if (contact.temporaryId != sample.temporaryContactId || contactLost) return
        contactLost = true
        write(sample, timestampEpochMillis, CalibrationEvent.CONTACT_LOST, contact = contact)
    }

    fun complete(timestampEpochMillis: Long): CalibrationSample? {
        val sample = active ?: return null
        write(sample, timestampEpochMillis, CalibrationEvent.SAMPLE_COMPLETE)
        active = null
        return sample
    }

    fun cancel() { active = null }
    fun isActive(): Boolean = active != null

    private fun write(
        sample: CalibrationSample,
        timestamp: Long,
        event: CalibrationEvent,
        observation: BleObservation? = null,
        contact: SonarContact? = null
    ) {
        sink(
            CalibrationRecord(
                sessionId = sample.sessionId,
                sampleId = sample.sampleId,
                testType = sample.type,
                target = sample.target.label,
                positionId = sample.position?.id,
                northMeters = sample.position?.northMeters,
                eastMeters = sample.position?.eastMeters,
                condition = sample.position?.condition,
                timestampEpochMillis = timestamp,
                elapsedMillis = (timestamp - sample.startedAtEpochMillis).coerceAtLeast(0),
                rawRssi = observation?.rssi,
                smoothedRssi = contact?.smoothedRssi,
                category = contact?.proximity,
                state = contact?.state,
                // ContactTracker uses elapsed realtime while exported timestamps use wall time.
                // Do not manufacture an age by mixing the two clock domains.
                contactAgeMillis = null,
                scanCount = scanCount,
                event = event,
                notes = sample.notes
            )
        )
    }
}
