package com.suri.pipsurios

import com.suri.pipsurios.data.OperatorField
import com.suri.pipsurios.data.OperatorProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class OperatorProfileTest {
    @Test
    fun profileNormalizesWhitespaceWithoutChangingFieldMeaning() {
        val normalized = OperatorProfile(
            id = " SURI-14 ",
            name = " Diego Pérez ",
            callsign = " SURI ",
            number = " 01 ",
            country = " Spain ",
            team = " PIP-SURI "
        ).normalized()

        assertEquals("SURI-14", normalized.id)
        assertEquals("Diego Pérez", normalized.name)
        assertEquals("SURI", normalized.callsign)
        assertEquals("01", normalized.number)
        assertEquals("Spain", normalized.country)
        assertEquals("PIP-SURI", normalized.team)
    }

    @Test
    fun fieldUpdatesRemainAvailableThroughTheSharedProfileModel() {
        val profile = OperatorProfile().update(OperatorField.ID, "SURI-14")
            .update(OperatorField.CALLSIGN, "SURI")

        assertEquals("SURI-14", profile.valueFor(OperatorField.ID))
        assertEquals("SURI", profile.valueFor(OperatorField.CALLSIGN))
    }
}
