package com.suri.suriosradar.v2

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suri.suriosradar.RadarDisplayState
import com.suri.suriosradar.ble.BleConnectionState
import com.suri.suriosradar.ble.BleStatus
import com.suri.suriosradar.ble.RadarBleClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class RadarV2UiState(
    val displayState: RadarDisplayState = RadarDisplayState.DISCONNECTED,
    val connected: Boolean = false,
    val targets: List<RadarV2Target> = emptyList(),
    val heldTargets: List<RadarV2Target> = emptyList(),
    val targetCount: Int = 0,
    val movingTargetCount: Int = 0,
    val stillTargetCount: Int = 0,
    val presence: Boolean = false,
    val lastSequence: Int? = null,
    val lastFrameAtMillis: Long? = null,
    val lastFrameAgeMillis: Long? = null,
    val receivedFrames: Int = 0,
    val lostFrames: Int = 0,
    val error: String? = null,
    val permissionsGranted: Boolean = false
)

class RadarV2ViewModel(application: Application) : AndroidViewModel(application) {
    private val bleClient = RadarBleClient(
        application,
        RadarBleClient.V2_DATA_CHARACTERISTIC_UUID
    )
    private val sequenceTracker = SequenceTrackerV2()
    private val _uiState = MutableStateFlow(RadarV2UiState())
    private val lastActiveAt = LongArray(RadarV2PacketCodec.TARGET_COUNT)
    private val lastKnownTargets = arrayOfNulls<RadarV2Target>(RadarV2PacketCodec.TARGET_COUNT)
    private var presenceLastSeen = 0L
    private var expiryJob: Job? = null

    val uiState: StateFlow<RadarV2UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bleClient.status.collect(::onBleStatus)
        }
        viewModelScope.launch {
            bleClient.packets.collect(::onPacketBytes)
        }
        expiryJob = viewModelScope.launch {
            while (isActive) {
                expireEspHomeState(System.currentTimeMillis())
                delay(100L)
            }
        }
    }

    fun setPermissionsGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionsGranted = granted) }
    }

    fun start() {
        if (_uiState.value.permissionsGranted) {
            bleClient.start()
        } else {
            _uiState.update { it.copy(displayState = RadarDisplayState.ERROR, error = "PERMISSIONS_REQUIRED") }
        }
    }

    fun stop() {
        bleClient.stop()
        sequenceTracker.reset()
        presenceLastSeen = 0L
        lastActiveAt.fill(0L)
        lastKnownTargets.fill(null)
        _uiState.update { it.copy(displayState = RadarDisplayState.DISCONNECTED, connected = false) }
    }

    private fun onBleStatus(status: BleStatus) {
        _uiState.update { state ->
            state.copy(
                displayState = status.toDisplayState(),
                connected = status.state == BleConnectionState.CONNECTED,
                error = status.detail.takeUnless { status.state != BleConnectionState.ERROR }
            )
        }
    }

    private fun onPacketBytes(bytes: ByteArray) {
        val packet = runCatching { RadarV2PacketCodec.decode(bytes) }.getOrElse { error ->
            _uiState.update { it.copy(error = error.message ?: "INVALID_PACKET") }
            return
        }
        val observation = sequenceTracker.observe(packet.sequence)
        if (!observation.accepted) return

        val now = System.currentTimeMillis()
        val activeTargets = packet.targets.filter { it.active }
        activeTargets.forEach { target ->
            lastActiveAt[target.slot - 1] = now
            lastKnownTargets[target.slot - 1] = target
        }
        if (activeTargets.isNotEmpty()) presenceLastSeen = now

        _uiState.update { state ->
            state.copy(
                displayState = RadarDisplayState.RECEIVING,
                connected = true,
                targets = packet.targets,
                heldTargets = lastKnownTargets.filterNotNull(),
                targetCount = packet.targetCount,
                movingTargetCount = packet.movingTargetCount,
                stillTargetCount = packet.stillTargetCount,
                presence = activeTargets.isNotEmpty() || now - presenceLastSeen < PRESENCE_TIMEOUT_MILLIS,
                lastSequence = packet.sequence,
                lastFrameAtMillis = now,
                lastFrameAgeMillis = 0L,
                receivedFrames = state.receivedFrames + 1,
                lostFrames = state.lostFrames + observation.missingPackets,
                error = null
            )
        }
    }

    private fun expireEspHomeState(now: Long) {
        _uiState.update { state ->
            val held = lastKnownTargets.mapIndexedNotNull { index, target ->
                target?.takeIf { now - lastActiveAt[index] <= COORDINATE_TIMEOUT_MILLIS }
            }
            state.copy(
                heldTargets = held,
                presence = presenceLastSeen != 0L && now - presenceLastSeen < PRESENCE_TIMEOUT_MILLIS,
                lastFrameAgeMillis = state.lastFrameAtMillis?.let { now - it }
            )
        }
    }

    override fun onCleared() {
        expiryJob?.cancel()
        bleClient.close()
    }

    private fun BleStatus.toDisplayState(): RadarDisplayState = when (state) {
        BleConnectionState.DISCONNECTED -> RadarDisplayState.DISCONNECTED
        BleConnectionState.SCANNING -> RadarDisplayState.SCANNING
        BleConnectionState.CONNECTING -> RadarDisplayState.CONNECTING
        BleConnectionState.CONNECTED -> RadarDisplayState.CONNECTED
        BleConnectionState.ERROR -> RadarDisplayState.ERROR
    }

    private companion object {
        const val COORDINATE_TIMEOUT_MILLIS = 1_000L
        const val PRESENCE_TIMEOUT_MILLIS = 5_000L
    }
}

private class SequenceTrackerV2 {
    private var last: Int? = null

    fun reset() {
        last = null
    }

    fun observe(sequence: Int): SequenceObservationV2 {
        val current = sequence and 0xFF
        val previous = last
        if (previous == null) {
            last = current
            return SequenceObservationV2(true)
        }
        val delta = (current - previous + 256) % 256
        return when {
            delta == 0 -> SequenceObservationV2(false)
            delta in 1..127 -> {
                last = current
                SequenceObservationV2(true, delta - 1)
            }
            else -> SequenceObservationV2(false)
        }
    }
}

private data class SequenceObservationV2(
    val accepted: Boolean,
    val missingPackets: Int = 0
)
