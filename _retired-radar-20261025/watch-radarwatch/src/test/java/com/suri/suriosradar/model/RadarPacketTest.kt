package com.suri.suriosradar.model

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
        assertEquals(RadarTarget(1, 1, 1234, -2000), packet.targets[0])
        assertEquals(TargetState.RETAINED, packet.targets[1].state)
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

    @Test
    fun encodesAndDecodesTheSamePacket() {
        val original = RadarPacket(
            protocolVersion = 1,
            sequence = 7,
            targets = listOf(
                RadarTarget(1, 1, -1000, 0),
                RadarTarget(2, 3, 1000, 6000),
                RadarTarget(3, 0, 0, 0)
            )
        )

        assertEquals(original, RadarPacketCodec.decode(RadarPacketCodec.encode(original)))
    }
}
