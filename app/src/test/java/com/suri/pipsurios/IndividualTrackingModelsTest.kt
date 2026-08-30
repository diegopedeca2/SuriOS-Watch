package com.suri.pipsurios

import com.suri.pipsurios.individualtracking.IndividualTrackingTarget
import com.suri.pipsurios.prs.BleObservation
import com.suri.pipsurios.prs.PrsDeviceRuleType
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.prs.PrsSavedDevice
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IndividualTrackingModelsTest {
    @Test
    fun selectedAddressMatchesOnlyTheA56Observation() {
        val target = target(deviceIdentifier = "AA:BB:CC:DD:EE:FF")

        assertTrue(target.matches(observation("AA:BB:CC:DD:EE:FF")))
        assertFalse(target.matches(observation("AA:BB:CC:DD:EE:00")))
        assertFalse(
            target.matches(
                observation(
                    identifier = "AA:BB:CC:DD:EE:FF",
                    source = PrsObservationSource.PROBE_WATCH_2
                )
            )
        )
    }

    @Test
    fun knownBleNameCanKeepMatchingWhenAddressRotates() {
        val target = target(
            deviceIdentifier = "AA:BB:CC:DD:EE:FF",
            knownRule = PrsSavedDevice(
                type = PrsDeviceRuleType.ADVERTISED_NAME,
                value = "FIELD BEACON",
                displayName = "Field beacon"
            )
        )

        assertTrue(
            target.matches(
                observation(
                    identifier = "11:22:33:44:55:66",
                    name = " field   beacon "
                )
            )
        )
    }

    @Test
    fun rotatingAddressWithoutKnownNameDoesNotSwitchTarget() {
        val target = target(deviceIdentifier = "AA:BB:CC:DD:EE:FF")

        assertFalse(
            target.matches(
                observation(
                    identifier = "11:22:33:44:55:66",
                    name = "FIELD BEACON"
                )
            )
        )
    }

    private fun target(
        deviceIdentifier: String,
        knownRule: PrsSavedDevice? = null
    ) = IndividualTrackingTarget(
        contactId = deviceIdentifier,
        deviceIdentifier = deviceIdentifier,
        displayName = "FIELD BEACON",
        source = PrsObservationSource.A56,
        knownRule = knownRule
    )

    private fun observation(
        identifier: String,
        name: String? = "FIELD BEACON",
        source: PrsObservationSource = PrsObservationSource.A56
    ) = BleObservation(
        temporaryId = identifier,
        deviceIdentifier = identifier,
        deviceName = name,
        rssi = -70,
        observedAt = 0L,
        source = source
    )
}
