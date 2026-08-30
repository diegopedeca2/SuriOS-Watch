package com.suri.pipsurios.prs

import com.suri.probeprotocol.ProbeProtocol

enum class PrsOperatingMode(
    val displayName: String,
    val subtitle: String,
    val localScannerEnabled: Boolean,
    val probeEnabled: Boolean,
    val command: ProbeProtocol.Command?
) {
    LOCAL_SCAN(
        displayName = "LOCAL SCAN",
        subtitle = "A56 ONLY // LOCAL PROSPECTION",
        localScannerEnabled = true,
        probeEnabled = false,
        command = null
    ),
    SCAN_PROBE(
        displayName = "SCAN + PROBE",
        subtitle = "A56 + WATCH 2 // MOBILE PROBE",
        localScannerEnabled = true,
        probeEnabled = true,
        command = ProbeProtocol.Command.START_RECON
    )
}

data class PrsProbeLocation(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val timestampEpochMillis: Long,
    val provider: String?,
    val batteryPercent: Int?
) {
    companion object {
        fun from(sample: ProbeProtocol.LocationSample) = PrsProbeLocation(
            latitude = sample.latitude,
            longitude = sample.longitude,
            accuracyMeters = sample.accuracyMeters,
            timestampEpochMillis = sample.timestampEpochMillis,
            provider = sample.provider,
            batteryPercent = sample.batteryPercent
        )
    }
}

data class PrsProbeNodeSnapshot(
    val probeId: String = "WATCH-2",
    val state: String = "DISCONNECTED",
    val location: PrsProbeLocation? = null,
    val lastSeenEpochMillis: Long = 0L,
    val observationCount: Int = 0,
    val batteryPercent: Int? = null,
    val message: String? = null
)

/** Position of a measured probe node inside the phone-centred display surface. */
data class PrsGridProbe(
    val label: String,
    val xFraction: Float,
    val yFraction: Float,
    val accuracyMeters: Float,
    val state: String,
    val observationCount: Int
)
