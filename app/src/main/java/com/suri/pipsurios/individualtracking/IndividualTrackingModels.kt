package com.suri.pipsurios.individualtracking

import com.suri.pipsurios.prs.BleObservation
import com.suri.pipsurios.prs.PrsDeviceRuleType
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.prs.PrsSavedDevice
import java.util.Locale

/** The in-memory hand-off between TARGET and TRACKER. No data is persisted. */
data class IndividualTrackingSelection(
    val mapId: String,
    val target: IndividualTrackingTarget
)

data class IndividualTrackingTarget(
    val contactId: String,
    val deviceIdentifier: String,
    val displayName: String,
    val source: PrsObservationSource,
    val knownRule: PrsSavedDevice? = null
) {
    /**
     * Keeps a selected target usable when a known BLE name is the stable rule
     * and Android rotates the device address between sessions.
     */
    fun matches(observation: BleObservation): Boolean {
        if (observation.source != source) return false
        if (observation.deviceIdentifier.equals(deviceIdentifier, ignoreCase = true)) return true
        return knownRule?.type == PrsDeviceRuleType.ADVERTISED_NAME &&
            normalizeName(observation.deviceName) == knownRule.value
    }

    private fun normalizeName(value: String?): String? = value
        ?.filterNot(Char::isISOControl)
        ?.trim()
        ?.replace(Regex("\\s+"), " ")
        ?.takeIf { it.isNotEmpty() }
        ?.uppercase(Locale.US)
}
