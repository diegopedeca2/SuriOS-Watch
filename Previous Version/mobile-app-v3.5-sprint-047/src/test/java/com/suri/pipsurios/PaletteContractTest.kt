package com.suri.pipsurios

import androidx.compose.ui.graphics.Color
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGray
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenBright
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import org.junit.Assert.assertEquals
import org.junit.Test

class PaletteContractTest {
    @Test
    fun paletteMatchesEdlValues() {
        assertEquals(Color(0xFF000000), PipBlack)
        assertEquals(Color(0xFF66FF66), PipGreen)
        assertEquals(Color(0xFF3FAF5A), PipGreenDim)
        assertEquals(Color(0xFFFFC857), PipAmber)
        assertEquals(Color(0xFFFF4D4D), PipRed)
    }

    @Test
    fun auxiliaryPaletteMatchesEdlValues() {
        assertEquals(Color(0xFF66FF99), PipGreenBright)
        assertEquals(Color(0xFF33AAFF), PipBlue)
        assertEquals(Color(0xFF5A5A5A), PipGray)
    }
}
