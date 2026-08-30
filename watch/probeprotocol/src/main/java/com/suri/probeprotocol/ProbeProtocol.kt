package com.suri.probeprotocol

import java.nio.charset.StandardCharsets
import java.util.Base64

/** Wire contract shared by the phone-side P.R.S. and the executable Watch PROBE. */
object ProbeProtocol {
    const val VERSION = "1"
    const val CONTROL_PATH = "/suri/probe/control"
    const val TELEMETRY_PATH_PREFIX = "/suri/probe/telemetry/"

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

    fun encodeControl(control: Control): ByteArray = encode(
        "PROBE",
        VERSION,
        "CONTROL",
        control.command.name,
        control.phoneNodeId,
        control.sessionId
    )

    fun encodeLocation(sample: LocationSample): ByteArray = encode(
        "PROBE",
        VERSION,
        "LOCATION",
        sample.probeId,
        sample.sequence.toString(),
        sample.timestampEpochMillis.toString(),
        sample.latitude.toString(),
        sample.longitude.toString(),
        sample.accuracyMeters.toString(),
        encoded(sample.provider),
        sample.batteryPercent?.toString().orEmpty()
    )

    fun encodeBle(sample: BleSample): ByteArray = encode(
        "PROBE",
        VERSION,
        "BLE",
        sample.probeId,
        sample.sequence.toString(),
        sample.timestampEpochMillis.toString(),
        encoded(sample.temporaryId),
        encoded(sample.deviceIdentifier),
        encoded(sample.deviceName),
        sample.rssi.toString(),
        encoded(sample.advertisingDataHex),
        sample.deviceType?.toString().orEmpty()
    )

    fun encodeStatus(status: Status): ByteArray = encode(
        "PROBE",
        VERSION,
        "STATUS",
        status.probeId,
        encoded(status.state),
        status.timestampEpochMillis.toString(),
        status.batteryPercent?.toString().orEmpty(),
        encoded(status.message)
    )

    fun decode(bytes: ByteArray): Packet? = runCatching {
        val fields = String(bytes, StandardCharsets.UTF_8).split('|')
        if (fields.size < 3 || fields[0] != "PROBE" || fields[1] != VERSION) return null
        when (fields[2]) {
            "CONTROL" -> Packet.ControlMessage(
                Control(
                    command = Command.valueOf(fields[3]),
                    phoneNodeId = fields[4],
                    sessionId = fields[5]
                )
            )
            "LOCATION" -> Packet.Location(
                LocationSample(
                    probeId = fields[3],
                    sequence = fields[4].toLong(),
                    timestampEpochMillis = fields[5].toLong(),
                    latitude = fields[6].toDouble(),
                    longitude = fields[7].toDouble(),
                    accuracyMeters = fields[8].toFloat(),
                    provider = decoded(fields[9]),
                    batteryPercent = fields[10].toNullableInt()
                )
            )
            "BLE" -> Packet.Ble(
                BleSample(
                    probeId = fields[3],
                    sequence = fields[4].toLong(),
                    timestampEpochMillis = fields[5].toLong(),
                    temporaryId = decoded(fields[6]).orEmpty(),
                    deviceIdentifier = decoded(fields[7]).orEmpty(),
                    deviceName = decoded(fields[8]),
                    rssi = fields[9].toInt(),
                    advertisingDataHex = decoded(fields[10]),
                    deviceType = fields[11].toNullableInt()
                )
            )
            "STATUS" -> Packet.StatusUpdate(
                Status(
                    probeId = fields[3],
                    state = decoded(fields[4]).orEmpty(),
                    timestampEpochMillis = fields[5].toLong(),
                    batteryPercent = fields[6].toNullableInt(),
                    message = decoded(fields[7])
                )
            )
            else -> null
        }
    }.getOrNull()

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
