package com.suri.suriosradar.mini

import android.app.Application
import android.util.Log
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

data class RadarMiniUiState(
    val displayState: RadarDisplayState = RadarDisplayState.DISCONNECTED,
    val connected: Boolean = false,
    val packet: RadarMiniPacket? = null,
    val rangeMm: Int = 6000,
    val mirrored: Boolean = false,
    val demo: Boolean = false,
    val lastSequence: Int? = null,
    val lastFrameAtMillis: Long? = null,
    val lastFrameAgeMillis: Long? = null,
    val receivedFrames: Int = 0,
    val lostFrames: Int = 0,
    val error: String? = null,
    val permissionsGranted: Boolean = false
)

class RadarMiniViewModel(application: Application) : AndroidViewModel(application) {
    private val bleClient = RadarBleClient(
        application,
        RadarBleClient.V2_DATA_CHARACTERISTIC_UUID
    )
    private val sequenceTracker = MiniSequenceTracker()
    private val _uiState = MutableStateFlow(RadarMiniUiState())
    private var ageJob: Job? = null

    val uiState: StateFlow<RadarMiniUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bleClient.status.collect(::onBleStatus)
        }
        viewModelScope.launch {
            bleClient.packets.collect(::onPacketBytes)
        }
        ageJob = viewModelScope.launch {
            while (isActive) {
                updateFrameAge()
                delay(100L)
            }
        }
    }

    fun setPermissionsGranted(granted: Boolean) {
        _uiState.update { it.copy(permissionsGranted = granted) }
    }

    fun start() {
        if (_uiState.value.permissionsGranted) {
            Log.i("RadarMini", "starting BLE scan")
            _uiState.update { it.copy(displayState = RadarDisplayState.SCANNING, error = null) }
            bleClient.start()
        } else {
            Log.w("RadarMini", "BLE permissions are missing")
            _uiState.update {
                it.copy(displayState = RadarDisplayState.ERROR, error = "PERMISSIONS_REQUIRED")
            }
        }
    }

    fun stop() {
        bleClient.stop()
        sequenceTracker.reset()
        _uiState.update {
            it.copy(
                displayState = RadarDisplayState.DISCONNECTED,
                connected = false,
                packet = null,
                lastFrameAtMillis = null,
                lastFrameAgeMillis = null
            )
        }
    }

    fun toggleRange() {
        val nextRange = if (_uiState.value.rangeMm >= 6000) 3000 else 6000
        bleClient.writeControlByte(if (nextRange == 6000) 0x10 else 0x11)
        _uiState.update { it.copy(rangeMm = nextRange) }
    }

    fun toggleMirror() {
        _uiState.update { it.copy(mirrored = !it.mirrored) }
    }

    fun toggleDemo() {
        _uiState.update { it.copy(demo = !it.demo) }
    }

    private fun onBleStatus(status: BleStatus) {
        Log.i("RadarMini", "BLE status=${status.state} detail=${status.detail}")
        _uiState.update { state ->
            state.copy(
                displayState = status.toDisplayState(),
                connected = status.state == BleConnectionState.CONNECTED,
                error = status.detail.takeUnless { status.state != BleConnectionState.ERROR }
            )
        }
    }

    private fun onPacketBytes(bytes: ByteArray) {
        val packet = runCatching { RadarMiniPacketCodec.decode(bytes) }.getOrElse { error ->
            _uiState.update { it.copy(error = error.message ?: "INVALID_PACKET") }
            return
        }
        val observation = sequenceTracker.observe(packet.sequence)
        if (!observation.accepted) return
        val now = System.currentTimeMillis()
        Log.i(
            "RadarMiniData",
            "seq=${packet.sequence} targets=${packet.rawTargetCount} " +
                packet.targets.joinToString { target ->
                    "t${target.slot}=${target.xMm},${target.yMm},${target.state}"
                }
        )
        _uiState.update { state ->
            state.copy(
                displayState = RadarDisplayState.RECEIVING,
                connected = true,
                packet = packet,
                lastSequence = packet.sequence,
                lastFrameAtMillis = now,
                lastFrameAgeMillis = 0L,
                receivedFrames = state.receivedFrames + 1,
                lostFrames = state.lostFrames + observation.missingPackets,
                error = null
            )
        }
    }

    private fun updateFrameAge() {
        val now = System.currentTimeMillis()
        _uiState.update { state ->
            state.copy(lastFrameAgeMillis = state.lastFrameAtMillis?.let { now - it })
        }
    }

    override fun onCleared() {
        ageJob?.cancel()
        bleClient.close()
    }

    private fun BleStatus.toDisplayState(): RadarDisplayState = when (state) {
        BleConnectionState.DISCONNECTED -> RadarDisplayState.DISCONNECTED
        BleConnectionState.SCANNING -> RadarDisplayState.SCANNING
        BleConnectionState.CONNECTING -> RadarDisplayState.CONNECTING
        BleConnectionState.CONNECTED -> RadarDisplayState.CONNECTED
        BleConnectionState.ERROR -> RadarDisplayState.ERROR
    }
}

private class MiniSequenceTracker {
    private var last: Int? = null

    fun reset() {
        last = null
    }

    fun observe(sequence: Int): MiniSequenceObservation {
        val current = sequence and 0xFF
        val previous = last
        if (previous == null) {
            last = current
            return MiniSequenceObservation(true)
        }
        val delta = (current - previous + 256) % 256
        return when {
            delta == 0 -> MiniSequenceObservation(false)
            delta in 1..127 -> {
                last = current
                MiniSequenceObservation(true, delta - 1)
            }
            else -> MiniSequenceObservation(false)
        }
    }
}

private data class MiniSequenceObservation(
    val accepted: Boolean,
    val missingPackets: Int = 0
)
