package com.suri.pipsurios

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadoutApplyStateTest {
    @Test
    fun applyStateTracksDraftAndActiveEqualityAcrossChangesAndReapply() {
        var active = LoadoutConfiguration()
        var draft = active.copy(
            primaryRole = PrimaryWeaponRole.ASSAULT,
            primaryWeapon = InventoryItem.MCX
        )

        assertFalse(draft == active)

        active = draft.copy(accesories = draft.accesories.toSet())
        assertTrue(draft == active)

        draft = draft.copy(accesories = setOf(InventoryItem.DETON_A))
        assertFalse(draft == active)

        active = draft.copy(accesories = draft.accesories.toSet())
        assertTrue(draft == active)
    }

    @Test
    fun uniformParticipatesInDraftApplyEquality() {
        var active = LoadoutConfiguration()
        var draft = active.copy(uniform = "MCBCK - SUMMER")

        assertFalse(draft == active)
        active = draft.copy(accesories = draft.accesories.toSet())
        assertTrue(draft == active)
        assertTrue(active.uniform == "MCBCK - SUMMER")

        draft = draft.copy(uniform = "DESERT")
        assertFalse(draft == active)
    }
}
