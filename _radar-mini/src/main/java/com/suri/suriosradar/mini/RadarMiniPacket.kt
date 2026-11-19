package com.suri.suriosradar.mini

import java.nio.ByteBuffer
import java.nio.ByteOrder

data class RadarMiniTarget(
    val slot: Int,
    val state: Int,
    val xMm: Int,
    val yMm: Int,
    val speedMmPerSecond: Int,
    val resolutionMm: Int
) {
    val hasTarget: Boolean
        get() = state != STATE_NONE

    companion object {
        const val STATE_NONE = 0
        const val STATE_TRACKING = 1
    }
}

data class RadarMiniPacket(
    val sequence: Int,
    val targets: List<RadarMiniTarget>
) {
    val rawTargetCount: Int
        get() = targets.count { it.hasTarget }

    val hasTarget: Boolean
        get() = targets.any { it.hasTarget }
}

object RadarMiniPacketCodec {
    const val PACKET_SIZE = 29
    const val PROTOCOL_VERSION = 2

    fun decode(bytes: ByteArray): RadarMiniPacket {
        require(bytes.size == PACKET_SIZE) {
            "Mini Radar packet must contain exactly $PACKET_SIZE bytes, got ${bytes.size}"
        }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val version = buffer.get().toInt() and 0xFF
        require(version == PROTOCOL_VERSION) {
            "Unsupported Mini Radar packet version: $version"
        }
        val sequence = buffer.get().toInt() and 0xFF
        val targets = (1..TARGET_COUNT).map { slot ->
            RadarMiniTarget(
                slot = slot,
                state = buffer.get().toInt() and 0xFF,
                xMm = buffer.short.toInt(),
                yMm = buffer.short.toInt(),
                speedMmPerSecond = buffer.short.toInt(),
                resolutionMm = buffer.short.toInt() and 0xFFFF
            )
        }
        return RadarMiniPacket(
            sequence = sequence,
            targets = targets
        )
    }

    private const val TARGET_COUNT = 3
}
