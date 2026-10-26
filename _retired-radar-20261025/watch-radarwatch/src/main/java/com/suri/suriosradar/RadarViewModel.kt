package com.suri.suriosradar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.suri.suriosradar.ble.BleConnectionState
import com.suri.suriosradar.ble.BleStatus
import com.suri.suriosradar.ble.RadarBleClient
import com.suri.suriosradar.model.RadarPacket
import com.suri.suriosradar.model.RadarPacketCodec
import com.suri.suriosradar.model.RadarTarget
import com.suri.suriosradar.model.SequenceTracker
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sin

enum class RadarDataMode {
    REAL_BLE,
    SIMULATED
}

enum class RadarDisplayState {
    DISCONNECTED,
    SCANNING,
    CONNECTING,
    CONNECTED,
    RECEIVING,
    ERROR
}

data class RadarUiState(
    val mode: RadarDataMode? = null,
    val displayState: RadarDisplayState = RadarDisplayState.DISCONNECTED,
    val targets: List<RadarTarget> = emptyList(),
    val protocolVersion: Int? = null,
    val lastSequence: Int? = null,
    val lastPacketAtMillis: Long? = null,
    val lastPacketAgeMillis: Long? = null,
    val receivedPackets: Int = 0,
    val lostPackets: Int = 0,
    val outOfOrderPackets: Int = 0,
    val permissionsGranted: Boolean = false,
    val ecoMode: Boolean = false,
    val error: String? = null
)

class RadarViewModel(application: Application) : AndroidViewModel(application) {
    private val bleClient = RadarBleClient(application)
    private val sequenceTracker = SequenceTracker()
    private val _uiState = MutableStateFlow(RadarUiState())
    private var simulationJob: Job? = null
    private var simulationSequence = 0
    private var simulationTime = 0f

    val uiState: StateFlow<RadarUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bleClient.status.collectLatest(::onBleStatus)
        }
        viewModelScope.launch {
            bleClient.packets.collectLatest(::onPacketBytes)
        }
        viewModelScope.launch {
            while (isActive) {
                val now = System.currentTimeMillis()
                _uiState.update { state ->
                    val age = state.lastPacketAtMillis?.let { now - it }
                    val displayState = when {
                        state.mode == null -> RadarDisplayState.DISCONNECTED
                        state.mode == RadarDataMode.SIMULATED -> RadarDisplayState.RECEIVING
                        state.displayState == RadarDisplayState.RECEIVING && (age ?: Long.MAX_VALUE) > RECEIVING_TIMEOUT_MILLIS -> RadarDisplayState.CONNECTED
                        else -> state.displayState
                    }
                    state.copy(lastPacketAgeMillis = age, displayState = displayState)
                }
                delay(250L)
            }
        }
    }

    fun setPermissionsGranted(granted: Boolean) {
        val permissionsWereGranted = _uiState.value.permissionsGranted
        _uiState.update { it.copy(permissionsGranted = granted) }
        if (granted && !permissionsWereGranted && _uiState.value.mode == RadarDataMode.REAL_BLE) {
            bleClient.start()
        }
    }

    fun clearMode() {
        sequenceTracker.reset()
        simulationJob?.cancel()
        simulationJob = null
        bleClient.stop()
        _uiState.update { state ->
            RadarUiState(permissionsGranted = state.permissionsGranted)
        }
    }

    fun setEcoMode(enabled: Boolean) {
        if (bleClient.setEcoMode(enabled)) {
            _uiState.update { it.copy(ecoMode = enabled, error = null) }
        } else {
            _uiState.update { it.copy(error = "CONTROL_NOT_AVAILABLE") }
        }
    }

    fun setMode(mode: RadarDataMode) {
        sequenceTracker.reset()
        simulationJob?.cancel()
        simulationJob = null
        _uiState.update {
            it.copy(
                mode = mode,
                displayState = if (mode == RadarDataMode.SIMULATED) {
                    RadarDisplayState.RECEIVING
                } else {
                    RadarDisplayState.DISCONNECTED
                },
                targets = emptyList(),
                lastSequence = null,
                lastPacketAtMillis = null,
                lastPacketAgeMillis = null,
                receivedPackets = 0,
                lostPackets = 0,
                outOfOrderPackets = 0,
                ecoMode = false,
                error = null
            )
        }

        if (mode == RadarDataMode.REAL_BLE) {
            bleClient.stop()
            if (_uiState.value.permissionsGranted) {
                bleClient.start()
            } else {
                _uiState.update { it.copy(displayState = RadarDisplayState.ERROR, error = "PERMISSIONS_REQUIRED") }
            }
        } else {
            bleClient.stop()
            startSimulation()
        }
    }

    private fun startSimulation() {
        simulationSequence = 0
        simulationTime = 0f
        simulationJob = viewModelScope.launch {
            while (isActive) {
                val packet = simulatedPacket(simulationTime)
                onPacketBytes(RadarPacketCodec.encode(packet))
                simulationTime += 0.25f
                delay(SIMULATION_INTERVAL_MILLIS)
            }
        }
    }

    private fun simulatedPacket(time: Float): RadarPacket {
        val sequence = simulationSequence++ and 0xFF
        return RadarPacket(
            protocolVersion = 1,
            sequence = sequence,
            targets = listOf(
                RadarTarget(1, 1, (sin(time) * 650f).toInt(), 1800 + (sin(time * 0.7f) * 350f).toInt()),
                RadarTarget(2, 1, (sin(time * 0.65f + 1f) * 500f).toInt(), 3200 + (sin(time * 0.5f) * 650f).toInt()),
                RadarTarget(3, if ((sequence / 8) % 2 == 0) 3 else 0, -450, 4800)
            )
        )
    }

    private fun onBleStatus(status: BleStatus) {
        if (_uiState.value.mode != RadarDataMode.REAL_BLE) return
        _uiState.update { state ->
            state.copy(
                displayState = status.toDisplayState(),
                error = status.detail.takeUnless { status.state != BleConnectionState.ERROR }
            )
        }
    }

    private fun onPacketBytes(bytes: ByteArray) {
        val packet = runCatching { RadarPacketCodec.decode(bytes) }.getOrElse { error ->
            _uiState.update { it.copy(error = error.message ?: "INVALID_PACKET") }
            return
        }
        val sequence = sequenceTracker.observe(packet.sequence)
        if (!sequence.accepted) {
            _uiState.update {
                it.copy(
                    outOfOrderPackets = it.outOfOrderPackets + if (sequence.outOfOrder) 1 else 0,
                    error = if (sequence.duplicate) "DUPLICATE_SEQUENCE" else "OUT_OF_ORDER_SEQUENCE"
                )
            }
            return
        }

        val now = System.currentTimeMillis()
        _uiState.update { state ->
            state.copy(
                targets = packet.targets,
                protocolVersion = packet.protocolVersion,
                lastSequence = packet.sequence,
                lastPacketAtMillis = now,
                lastPacketAgeMillis = 0L,
                receivedPackets = state.receivedPackets + 1,
                lostPackets = state.lostPackets + sequence.missingPackets,
                displayState = RadarDisplayState.RECEIVING,
                error = null
            )
        }
    }

    override fun onCleared() {
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
        const val SIMULATION_INTERVAL_MILLIS = 750L
        const val RECEIVING_TIMEOUT_MILLIS = 3_000L
    }
}
