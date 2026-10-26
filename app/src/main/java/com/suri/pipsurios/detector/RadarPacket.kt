package com.suri.pipsurios.detector

import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class RadarTargetState {
    INACTIVE,
    MOVING,
    RETAINED,
    UNKNOWN
}

data class RadarTarget(
    val slot: Int,
    val flags: Int,
    val xMm: Int,
    val yMm: Int
) {
    val state: RadarTargetState
        get() = when (flags) {
            0 -> RadarTargetState.INACTIVE
            1 -> RadarTargetState.MOVING
            3 -> RadarTargetState.RETAINED
            else -> RadarTargetState.UNKNOWN
        }

    val visible: Boolean
        get() = state == RadarTargetState.MOVING || state == RadarTargetState.RETAINED
}

data class RadarPacket(
    val protocolVersion: Int,
    val sequence: Int,
    val targets: List<RadarTarget>
)

object RadarPacketCodec {
    const val PACKET_SIZE = 17
    const val TARGET_COUNT = 3

    fun decode(bytes: ByteArray): RadarPacket {
        require(bytes.size == PACKET_SIZE) {
            "Radar packet must contain exactly $PACKET_SIZE bytes, got ${bytes.size}"
        }

        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val version = buffer.get().toInt() and 0xFF
        val sequence = buffer.get().toInt() and 0xFF
        val targets = (1..TARGET_COUNT).map { slot ->
            RadarTarget(
                slot = slot,
                flags = buffer.get().toInt() and 0xFF,
                xMm = buffer.short.toInt(),
                yMm = buffer.short.toInt()
            )
        }
        return RadarPacket(version, sequence, targets)
    }
}

data class SequenceObservation(
    val accepted: Boolean,
    val missingPackets: Int = 0,
    val duplicate: Boolean = false,
    val outOfOrder: Boolean = false
)

class SequenceTracker {
    private var lastSequence: Int? = null

    fun reset() {
        lastSequence = null
    }

    fun observe(sequence: Int): SequenceObservation {
        val current = sequence and 0xFF
        val previous = lastSequence
        if (previous == null) {
            lastSequence = current
            return SequenceObservation(accepted = true)
        }

        val delta = (current - previous + 256) % 256
        return when {
            delta == 0 -> SequenceObservation(accepted = false, duplicate = true)
            delta in 1..127 -> {
                lastSequence = current
                SequenceObservation(accepted = true, missingPackets = delta - 1)
            }
            else -> SequenceObservation(accepted = false, outOfOrder = true)
        }
    }
}
