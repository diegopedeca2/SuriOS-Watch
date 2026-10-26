package com.suri.pipsurios

import com.suri.pipsurios.detector.RadarPacketCodec
import com.suri.pipsurios.detector.RadarTargetState
import com.suri.pipsurios.detector.SequenceTracker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RadarPacketTest {
    @Test
    fun decodesLittleEndianSignedCoordinatesAndFlags() {
        val bytes = byteArrayOf(
            1, 0xFE.toByte(),
            1, 0xD2.toByte(), 0x04, 0x30.toByte(), 0xF8.toByte(),
            3, 0xFA.toByte(), 0x00, 0x10, 0x27,
            0, 0, 0, 0, 0
        )

        val packet = RadarPacketCodec.decode(bytes)

        assertEquals(1, packet.protocolVersion)
        assertEquals(254, packet.sequence)
        assertEquals(1234, packet.targets[0].xMm)
        assertEquals(-2000, packet.targets[0].yMm)
        assertEquals(RadarTargetState.RETAINED, packet.targets[1].state)
        assertEquals(250, packet.targets[1].xMm)
        assertEquals(10000, packet.targets[1].yMm)
        assertFalse(packet.targets[2].visible)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsPacketsWithWrongLength() {
        RadarPacketCodec.decode(ByteArray(16))
    }

    @Test
    fun detectsMissingPacketsAndWrapAround() {
        val tracker = SequenceTracker()

        assertTrue(tracker.observe(254).accepted)
        assertEquals(1, tracker.observe(0).missingPackets)
        assertEquals(0, tracker.observe(1).missingPackets)
    }

    @Test
    fun detectsOldAndDuplicateSequences() {
        val tracker = SequenceTracker()

        tracker.observe(20)
        assertTrue(tracker.observe(20).duplicate)
        assertTrue(tracker.observe(19).outOfOrder)
    }
}
