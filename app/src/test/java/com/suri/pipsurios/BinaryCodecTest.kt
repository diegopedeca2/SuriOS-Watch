package com.suri.pipsurios

import com.suri.pipsurios.binary.BinaryCodec
import org.junit.Assert.assertEquals
import org.junit.Test

class BinaryCodecTest {
    @Test
    fun encodesTextAsEightBitBytes() {
        assertEquals("01010011 01010101 01010010 01001001", BinaryCodec.encode("SURI"))
    }

    @Test
    fun preservesSpacesAsAnEightBitByte() {
        assertEquals(
            "01000001 00100000 01000010",
            BinaryCodec.encode("A B")
        )
    }

    @Test
    fun decodesEightBitBytes() {
        assertEquals("SURI", BinaryCodec.decode("01010011 01010101 01010010 01001001"))
    }

    @Test
    fun decodesContinuousBitsAndShowsInvalidBytes() {
        assertEquals("A?", BinaryCodec.decode("010000010101"))
    }
}
