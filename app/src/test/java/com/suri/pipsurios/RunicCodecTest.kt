package com.suri.pipsurios

import com.suri.pipsurios.runic.RunicCodec
import org.junit.Assert.assertEquals
import org.junit.Test

class RunicCodecTest {
    @Test fun encodesLatinTextUsingElderFuthark() {
        assertEquals("ᛊᚢᚱᛁᛟᛊ", RunicCodec.toRunes("SURIOS"))
    }

    @Test fun decodesRunesToLatinText() {
        assertEquals("SURIOS", RunicCodec.toLatin("ᛊᚢᚱᛁᛟᛊ"))
    }

    @Test fun preservesSpacesPunctuationAndThornDigraph() {
        assertEquals("ᚦᛖᛊᛏ!", RunicCodec.toRunes("thést!"))
        assertEquals("TH EST!", RunicCodec.toLatin("ᚦ ᛖᛊᛏ!"))
    }
}
