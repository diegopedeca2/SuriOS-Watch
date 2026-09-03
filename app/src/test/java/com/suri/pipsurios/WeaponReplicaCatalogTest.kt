package com.suri.pipsurios

import com.suri.pipsurios.ui.screens.WeaponReplicaCatalog
import org.junit.Assert.assertEquals
import org.junit.Test

class WeaponReplicaCatalogTest {
    @Test
    fun primaryAndSecondaryCatalogsCombineRoleOrTypeWithModel() {
        assertEquals(
            listOf(
                "SNIPER - L96",
                "SNIPER - LevAR-15",
                "ASSAULT - MCX",
                "ASSAULT - APC-9K",
                "DEMOLITION - MGL",
                "DEMOLITION - VOLCANO"
            ),
            WeaponReplicaCatalog.primary.map { it.displayName }
        )
        assertEquals(
            listOf(
                "HANDGUN - DESERT EAGLE",
                "HANDGUN - AAP-01C",
                "DEMOLITION - MGL",
                "DEMOLITION - VOLCANO"
            ),
            WeaponReplicaCatalog.secondary.map { it.displayName }
        )
    }
}
