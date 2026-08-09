package com.suri.pipsurios

import com.suri.pipsurios.morse.MorseCodec
import org.junit.Assert.assertEquals
import org.junit.Test

class MorseCodecTest {
    @Test fun encodesLettersNumbersAndWords() {
        assertEquals("..._---_...__.-_....-", MorseCodec.encode("SOS A4"))
    }

    @Test fun decodesLetterAndWordSeparators() {
        assertEquals("SOS A4", MorseCodec.decode("..._---_...__.-_....-"))
    }

    @Test fun unknownMorseSymbolIsVisible() {
        assertEquals("?", MorseCodec.decode("........"))
    }
}
