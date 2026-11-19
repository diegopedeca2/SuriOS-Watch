package com.suri.suriosradar.v2

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class RadarV2Target(
    val slot: Int,
    val active: Boolean,
    val xMm: Int,
    val yMm: Int,
    val speedMmPerSecond: Int,
    val resolutionMm: Int
) {
    val distanceMm: Float
        get() = kotlin.math.hypot(xMm.toFloat(), yMm.toFloat())

    val moving: Boolean
        get() = active && speedMmPerSecond != 0
}

data class RadarV2Packet(
    val protocolVersion: Int,
    val sequence: Int,
    val targets: List<RadarV2Target>
) {
    val targetCount: Int
        get() = targets.count { it.active }

    val movingTargetCount: Int
        get() = targets.count { it.moving }

    val stillTargetCount: Int
        get() = targetCount - movingTargetCount
}

object RadarV2PacketCodec {
    const val PACKET_SIZE = 29
    const val TARGET_COUNT = 3
    private const val TARGET_SIZE = 9

    fun decode(bytes: ByteArray): RadarV2Packet {
        require(bytes.size == PACKET_SIZE) {
            "Radar v2 packet must contain exactly $PACKET_SIZE bytes, got ${bytes.size}"
        }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val version = buffer.get().toInt() and 0xFF
        require(version == 2) { "Unsupported Radar v2 protocol version: $version" }
        val sequence = buffer.get().toInt() and 0xFF
        val targets = (1..TARGET_COUNT).map { slot ->
            val active = (buffer.get().toInt() and 0xFF) != 0
            val x = buffer.short.toInt()
            val y = buffer.short.toInt()
            val speed = buffer.short.toInt()
            val resolution = buffer.short.toInt() and 0xFFFF
            RadarV2Target(slot, active, x, y, speed, resolution)
        }
        return RadarV2Packet(version, sequence, targets)
    }
}
