package com.suri.pipsurios

import com.suri.pipsurios.ui.skin.SkinCatalog
import com.suri.pipsurios.ui.skin.SkinId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SkinCatalogTest {
    @Test fun catalogContainsTheSixOrderedVisibleSkins() {
        assertEquals(
            listOf(
                "BROTHERHOOD OF STEEL",
                "SALAMANDER",
                "IRON HAND",
                "ADEPTUS MECHANICUS",
                "NECRON",
                "MANDALORIAN"
            ),
            SkinCatalog.all.map { it.displayName }
        )
    }

    @Test fun onlyBrotherhoodUsesTheExistingApplicationExperience() {
        assertTrue(SkinId.BROTHERHOOD_OF_STEEL.implemented)
        SkinCatalog.all.filterNot { it == SkinId.BROTHERHOOD_OF_STEEL }
            .forEach { assertFalse(it.implemented) }
    }

}
