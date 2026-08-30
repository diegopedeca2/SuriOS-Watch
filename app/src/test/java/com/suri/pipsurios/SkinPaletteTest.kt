package com.suri.pipsurios

import androidx.compose.ui.graphics.Color
import com.suri.pipsurios.ui.skin.SkinId
import com.suri.pipsurios.ui.skin.SkinSession
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.SkinPalettes
import org.junit.Assert.assertEquals
import org.junit.Test

class SkinPaletteTest {
    @Test
    fun necronPaletteUsesBlackstoneTealAndBronzeRoles() {
        assertEquals(Color(0xFF000000), SkinPalettes.necron.background)
        assertEquals(Color(0xFF9DFFE9), SkinPalettes.necron.primary)
        assertEquals(Color(0xFF48BFAF), SkinPalettes.necron.secondary)
        assertEquals(Color(0xFFE7B86A), SkinPalettes.necron.amber)
    }

    @Test
    fun activeTokensFollowTheSelectedSkinAndCanReturnToBrotherhood() {
        try {
            SkinSession.activeSkin = SkinId.NECRON
            assertEquals(Color(0xFF9DFFE9), PipGreen)
            assertEquals(Color(0xFF48BFAF), PipGreenDim)
            assertEquals(Color(0xFFE7B86A), PipAmber)
            assertEquals(Color(0xFF000000), PipBlack)
        } finally {
            SkinSession.activeSkin = SkinId.BROTHERHOOD_OF_STEEL
        }
    }
}
