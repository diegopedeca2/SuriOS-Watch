package com.suri.pipsurios.sonar

enum class ProximityCategory {
    VERY_CLOSE,
    CLOSE,
    MEDIUM,
    FAR
}

enum class ContactState {
    BACKGROUND,
    NEW
}

data class BleObservation(
    val temporaryId: String,
    val rssi: Int,
    val observedAt: Long
)

data class SonarContact(
    val temporaryId: String,
    val currentRssi: Int,
    val smoothedRssi: Float,
    val firstSeen: Long,
    val lastSeen: Long,
    val state: ContactState,
    val proximity: ProximityCategory,
    val visualAngleDegrees: Float
)

data class SonarSnapshot(
    val contacts: List<SonarContact>,
    val isCalibrating: Boolean,
    val hasBaseline: Boolean
) {
    val newContactCount: Int get() = contacts.count { it.state == ContactState.NEW }
    fun contactCount(proximity: ProximityCategory): Int = contacts.count { it.proximity == proximity }
}
