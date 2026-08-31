package com.suri.probeprotocol

import java.nio.charset.StandardCharsets
import java.util.Base64

/** Wire contract shared by the phone-side P.R.S. and the executable Watch PROBE. */
object ProbeProtocol {
    const val VERSION = "1"
    const val CONTROL_PATH = "/suri/probe/control"
    const val TELEMETRY_PATH_PREFIX = "/suri/probe/telemetry/"
    const val LOCATION_PATH = TELEMETRY_PATH_PREFIX + "location"
    const val BLE_PATH = TELEMETRY_PATH_PREFIX + "ble"
    const val STATUS_PATH = TELEMETRY_PATH_PREFIX + "status"

    const val TIMESTAMP_TOLERANCE_MILLIS = 5 * 60 * 1_000L

    enum class Command {
        START_RECON,
        START_SENTRY,
        STOP,
        PING
    }

    data class Control(
        val command: Command,
        val phoneNodeId: String,
        val sessionId: String
    )

    data class LocationSample(
        val probeId: String,
        val sessionId: String,
        val sequence: Long,
        val timestampEpochMillis: Long,
        val latitude: Double,
        val longitude: Double,
        val accuracyMeters: Float,
        val provider: String?,
        val batteryPercent: Int?
    )

    data class BleSample(
        val probeId: String,
        val sessionId: String,
        val sequence: Long,
        val timestampEpochMillis: Long,
        val temporaryId: String,
        val deviceIdentifier: String,
        val deviceName: String?,
        val rssi: Int,
        val advertisingDataHex: String?,
        val deviceType: Int?
    )

    data class Status(
        val probeId: String,
        val sessionId: String,
        val state: String,
        val timestampEpochMillis: Long,
        val batteryPercent: Int?,
        val message: String?
    )

    sealed interface Packet {
        data class ControlMessage(val control: Control) : Packet
        data class Location(val sample: LocationSample) : Packet
        data class Ble(val sample: BleSample) : Packet
        data class StatusUpdate(val status: Status) : Packet
    }

    fun encodeControl(control: Control): ByteArray {
        control.validate()
        return encode(
            "PROBE",
            VERSION,
            "CONTROL",
            control.command.name,
            encoded(control.phoneNodeId),
            encoded(control.sessionId)
        )
    }

    fun encodeLocation(sample: LocationSample): ByteArray {
        sample.validate()
        return encode(
        "PROBE",
        VERSION,
        "LOCATION",
        sample.probeId,
        encoded(sample.sessionId),
        sample.sequence.toString(),
        sample.timestampEpochMillis.toString(),
        sample.latitude.toString(),
        sample.longitude.toString(),
        sample.accuracyMeters.toString(),
        encoded(sample.provider),
        sample.batteryPercent?.toString().orEmpty()
        )
    }

    fun encodeBle(sample: BleSample): ByteArray {
        sample.validate()
        return encode(
        "PROBE",
        VERSION,
        "BLE",
        sample.probeId,
        encoded(sample.sessionId),
        sample.sequence.toString(),
        sample.timestampEpochMillis.toString(),
        encoded(sample.temporaryId),
        encoded(sample.deviceIdentifier),
        encoded(sample.deviceName),
        sample.rssi.toString(),
        encoded(sample.advertisingDataHex),
        sample.deviceType?.toString().orEmpty()
        )
    }

    fun encodeStatus(status: Status): ByteArray {
        status.validate()
        return encode(
        "PROBE",
        VERSION,
        "STATUS",
        status.probeId,
        encoded(status.sessionId),
        encoded(status.state),
        status.timestampEpochMillis.toString(),
        status.batteryPercent?.toString().orEmpty(),
        encoded(status.message)
        )
    }

    fun decode(bytes: ByteArray): Packet? = runCatching {
        val fields = String(bytes, StandardCharsets.UTF_8).split('|')
        if (fields.size < 3 || fields[0] != "PROBE" || fields[1] != VERSION) return null
        when (fields[2]) {
            "CONTROL" -> {
                require(fields.size == 6)
                val control = Control(
                    command = Command.valueOf(fields[3]),
                    phoneNodeId = decoded(fields[4]).orEmpty(),
                    sessionId = decoded(fields[5]).orEmpty()
                )
                control.validate()
                Packet.ControlMessage(control)
            }
            "LOCATION" -> {
                require(fields.size == 12)
                val sample = LocationSample(
                    probeId = fields[3],
                    sessionId = decoded(fields[4]).orEmpty(),
                    sequence = fields[5].toLong(),
                    timestampEpochMillis = fields[6].toLong(),
                    latitude = fields[7].toDouble(),
                    longitude = fields[8].toDouble(),
                    accuracyMeters = fields[9].toFloat(),
                    provider = decoded(fields[10]),
                    batteryPercent = fields[11].toNullableInt()
                )
                sample.validate()
                Packet.Location(sample)
            }
            "BLE" -> {
                require(fields.size == 13)
                val sample = BleSample(
                    probeId = fields[3],
                    sessionId = decoded(fields[4]).orEmpty(),
                    sequence = fields[5].toLong(),
                    timestampEpochMillis = fields[6].toLong(),
                    temporaryId = decoded(fields[7]).orEmpty(),
                    deviceIdentifier = decoded(fields[8]).orEmpty(),
                    deviceName = decoded(fields[9]),
                    rssi = fields[10].toInt(),
                    advertisingDataHex = decoded(fields[11]),
                    deviceType = fields[12].toNullableInt()
                )
                sample.validate()
                Packet.Ble(sample)
            }
            "STATUS" -> {
                require(fields.size == 9)
                val status = Status(
                    probeId = fields[3],
                    sessionId = decoded(fields[4]).orEmpty(),
                    state = decoded(fields[5]).orEmpty(),
                    timestampEpochMillis = fields[6].toLong(),
                    batteryPercent = fields[7].toNullableInt(),
                    message = decoded(fields[8])
                )
                status.validate()
                Packet.StatusUpdate(status)
            }
            else -> null
        }
    }.getOrNull()

    private fun Control.validate() {
        require(phoneNodeId.isProtocolText(128))
        require(sessionId.isProtocolText(96))
    }

    private fun LocationSample.validate() {
        validateCommon(probeId, sessionId, sequence, timestampEpochMillis)
        require(latitude.isFinite() && latitude in -90.0..90.0)
        require(longitude.isFinite() && longitude in -180.0..180.0)
        require(accuracyMeters.isFinite() && accuracyMeters in 0f..100_000f)
        require(provider == null || provider.isProtocolText(64))
        require(batteryPercent == null || batteryPercent in 0..100)
    }

    private fun BleSample.validate() {
        validateCommon(probeId, sessionId, sequence, timestampEpochMillis)
        require(temporaryId.isProtocolText(128))
        require(deviceIdentifier.isProtocolText(128))
        require(deviceName == null || deviceName.isProtocolText(128))
        require(rssi in -127..0)
        require(advertisingDataHex == null || advertisingDataHex.isValidHex())
        require(deviceType == null || deviceType in 0..3)
    }

    private fun Status.validate() {
        require(probeId.isProtocolText(64))
        require(sessionId.isProtocolText(96))
        require(state.isProtocolText(32))
        require(timestampEpochMillis.isPlausibleTimestamp())
        require(batteryPercent == null || batteryPercent in 0..100)
        require(message == null || message.isProtocolText(256))
    }

    private fun validateCommon(probeId: String, sessionId: String, sequence: Long, timestamp: Long) {
        require(probeId.isProtocolText(64))
        require(sessionId.isProtocolText(96))
        require(sequence > 0L)
        require(timestamp.isPlausibleTimestamp())
    }

    private fun String.isProtocolText(maxLength: Int): Boolean =
        isNotBlank() && length <= maxLength && all { it.code in 0x20..0x7E }

    private fun String.isValidHex(): Boolean =
        length <= 1024 && length % 2 == 0 && all { it in '0'..'9' || it in 'A'..'F' || it in 'a'..'f' }

    private fun Long.isPlausibleTimestamp(): Boolean {
        if (this <= 0L) return false
        val now = System.currentTimeMillis()
        return this in (now - TIMESTAMP_TOLERANCE_MILLIS)..(now + TIMESTAMP_TOLERANCE_MILLIS)
    }

    private fun encode(vararg fields: String): ByteArray =
        fields.joinToString("|").toByteArray(StandardCharsets.UTF_8)

    private fun encoded(value: String?): String = value
        ?.toByteArray(StandardCharsets.UTF_8)
        ?.let { Base64.getUrlEncoder().withoutPadding().encodeToString(it) }
        .orEmpty()

    private fun decoded(value: String): String? = value
        .takeIf { it.isNotEmpty() }
        ?.let { String(Base64.getUrlDecoder().decode(it), StandardCharsets.UTF_8) }

    private fun String.toNullableInt(): Int? = takeIf { it.isNotEmpty() }?.toInt()
}
