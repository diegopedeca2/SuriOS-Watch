package com.suri.pipsurios.sonar

class ContactTracker {
    private val contacts = linkedMapOf<String, SonarContact>()
    private val baselineIds = mutableSetOf<String>()
    private var calibrating = false
    private var baselineReady = false

    fun observe(observation: BleObservation): SonarContact {
        val previous = contacts[observation.temporaryId]
        val smoothed = previous?.let {
            RssiFilter.smooth(it.smoothedRssi, observation.rssi)
        } ?: observation.rssi.toFloat()

        if (calibrating) baselineIds += observation.temporaryId

        val state = when {
            calibrating -> ContactState.BACKGROUND
            !baselineReady -> ContactState.BACKGROUND
            observation.temporaryId in baselineIds -> ContactState.BACKGROUND
            else -> ContactState.NEW
        }

        val contact = SonarContact(
            temporaryId = observation.temporaryId,
            currentRssi = observation.rssi,
            smoothedRssi = smoothed,
            firstSeen = previous?.firstSeen ?: observation.observedAt,
            lastSeen = observation.observedAt,
            state = state,
            proximity = ProximityClassifier.classify(smoothed),
            visualAngleDegrees = previous?.visualAngleDegrees
                ?: stableAngleFor(observation.temporaryId)
        )
        contacts[observation.temporaryId] = contact
        return contact
    }

    fun startCalibration() {
        contacts.clear()
        baselineIds.clear()
        baselineReady = false
        calibrating = true
    }

    fun finishCalibration() {
        calibrating = false
        baselineReady = true
        contacts.replaceAll { id, contact ->
            contact.copy(
                state = if (id in baselineIds) ContactState.BACKGROUND else ContactState.NEW
            )
        }
    }

    fun expire(now: Long): List<SonarContact> {
        val expired = contacts.values.filter { contact ->
            now - contact.lastSeen > SonarTuning.CONTACT_EXPIRY_MILLIS
        }
        contacts.keys.removeAll(expired.mapTo(mutableSetOf()) { it.temporaryId })
        return expired
    }

    fun snapshot(): SonarSnapshot = SonarSnapshot(
        contacts = contacts.values.sortedBy { it.temporaryId },
        isCalibrating = calibrating,
        hasBaseline = baselineReady
    )

    private fun stableAngleFor(temporaryId: String): Float {
        val normalizedHash = temporaryId.hashCode().toLong() and 0x7fffffffL
        return (normalizedHash % 36000L) / 100f
    }
}
