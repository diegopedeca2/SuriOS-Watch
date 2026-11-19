package com.suri.suriosradar.v2

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder

class RadarV2PacketTest {
    @Test
    fun decodesEspHomeRawTargetFields() {
        val bytes = ByteBuffer.allocate(RadarV2PacketCodec.PACKET_SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .put(2)
            .put(254.toByte())
            .put(1).putShort((-1234).toShort()).putShort(3000.toShort()).putShort((-250).toShort()).putShort(180.toShort())
            .put(0).putShort(0).putShort(0).putShort(0).putShort(0)
            .put(1).putShort(400.toShort()).putShort(5000.toShort()).putShort(0).putShort(220.toShort())
            .array()

        val packet = RadarV2PacketCodec.decode(bytes)

        assertEquals(2, packet.protocolVersion)
        assertEquals(254, packet.sequence)
        assertEquals(-1234, packet.targets[0].xMm)
        assertEquals(3000, packet.targets[0].yMm)
        assertEquals(-250, packet.targets[0].speedMmPerSecond)
        assertTrue(packet.targets[0].moving)
        assertEquals(2, packet.targetCount)
        assertEquals(1, packet.movingTargetCount)
        assertEquals(1, packet.stillTargetCount)
    }

    @Test(expected = IllegalArgumentException::class)
    fun rejectsWrongVersion() {
        RadarV2PacketCodec.decode(ByteArray(RadarV2PacketCodec.PACKET_SIZE).also { it[0] = 1 })
    }
}
