package com.suri.pipsurios

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.state.ComplementCatalog
import com.suri.pipsurios.ui.state.ComplementRole
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ComplementCatalogTest {
    @Test
    fun everyImplementedWeaponHasAComplementDefinition() {
        val implemented = listOf(
            ComplementRole.SNIPER to InventoryItem.L96,
            ComplementRole.SNIPER to InventoryItem.LEVAR_15,
            ComplementRole.ASSAULT to InventoryItem.MCX,
            ComplementRole.ASSAULT to InventoryItem.APC_9K,
            ComplementRole.HANDGUN to InventoryItem.DESERT_EAGLE,
            ComplementRole.HANDGUN to InventoryItem.AAP_01C,
            ComplementRole.DEMOLITION to InventoryItem.MGL,
            ComplementRole.DEMOLITION to InventoryItem.VOLCANO,
            ComplementRole.ACCESORIES to InventoryItem.DETON_A,
            ComplementRole.ACCESORIES to InventoryItem.THUNDER_B
        )

        implemented.forEach { (role, item) ->
            assertNotNull("Missing $role/$item", ComplementCatalog.forSelection(role, item))
        }
    }

    @Test
    fun accessoryVolcanoDoesNotReuseDemolitionVolcanoDefinition() {
        assertNotNull(ComplementCatalog.forSelection(ComplementRole.DEMOLITION, InventoryItem.VOLCANO))
        assertNull(ComplementCatalog.forSelection(ComplementRole.ACCESORIES, InventoryItem.VOLCANO))
    }

    @Test
    fun reminderUsesPrimarySecondaryAndAccessories() {
        val lines = ComplementCatalog.reminderLines(
            LoadoutConfiguration(
                primaryRole = PrimaryWeaponRole.SNIPER,
                primaryWeapon = InventoryItem.L96,
                secondaryType = "HANDGUN",
                secondaryWeapon = InventoryItem.AAP_01C,
                accesories = setOf(InventoryItem.DETON_A, InventoryItem.THUNDER_B)
            )
        )

        assertTrue("BBs: 0,40" in lines)
        assertTrue("BBs: 0,30 - NORMAL + TRACER" in lines)
        assertTrue("HOLSTER: SPECIFIC" in lines)
        assertTrue("FLASHLIGHT" in lines)
        assertTrue("9mm CARTRIDGES" in lines)
        assertTrue("CO2 VIALS" in lines)
        assertTrue("CASINGS" in lines)
    }

    @Test
    fun reminderRemovesExactDuplicateComplements() {
        val lines = ComplementCatalog.reminderLines(
            LoadoutConfiguration(
                primaryRole = PrimaryWeaponRole.ASSAULT,
                primaryWeapon = InventoryItem.MCX,
                secondaryType = "DEMOLITION",
                secondaryWeapon = InventoryItem.MGL
            )
        )

        assertEquals(1, lines.count { it == "SLING: 1 POINT" })
        assertEquals(1, lines.count { it == "DBAL A2" })
    }

    @Test
    fun demolitionWeaponsUseAmmoInsteadOfLegacyGrenadesLine() {
        listOf(InventoryItem.MGL, InventoryItem.VOLCANO).forEach { weapon ->
            val lines = ComplementCatalog.reminderLines(
                LoadoutConfiguration(
                    primaryRole = PrimaryWeaponRole.DEMOLITION,
                    primaryWeapon = weapon
                )
            )

            assertTrue("AMMO: 40mm GRENADES x11" in lines)
            assertTrue(lines.none { it == "GRENADES: 11" })
        }
    }
}
