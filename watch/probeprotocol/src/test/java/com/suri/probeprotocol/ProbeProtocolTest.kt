package com.suri.probeprotocol

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProbeProtocolTest {
    @Test
    fun controlRoundTripPreservesCommandAndNode() {
        val original = ProbeProtocol.Control(
            command = ProbeProtocol.Command.START_SENTRY,
            phoneNodeId = "phone-node-01",
            sessionId = "PRS-123"
        )

        val decoded = ProbeProtocol.decode(ProbeProtocol.encodeControl(original))
            as ProbeProtocol.Packet.ControlMessage

        assertEquals(original, decoded.control)
    }

    @Test
    fun locationAndBleRoundTripsPreservePayload() {
        val location = ProbeProtocol.LocationSample(
            probeId = "WATCH-2",
            sequence = 7L,
            timestampEpochMillis = 123456789L,
            latitude = 40.3521,
            longitude = -3.4221,
            accuracyMeters = 4.5f,
            provider = "fused|gps",
            batteryPercent = 82
        )
        val ble = ProbeProtocol.BleSample(
            probeId = "WATCH-2",
            sequence = 8L,
            timestampEpochMillis = 123456790L,
            temporaryId = "WATCH-AA",
            deviceIdentifier = "AA:BB:CC:DD:EE:FF",
            deviceName = "Target | 03",
            rssi = -81,
            advertisingDataHex = "0102FF",
            deviceType = 2
        )

        val decodedLocation = ProbeProtocol.decode(ProbeProtocol.encodeLocation(location))
            as ProbeProtocol.Packet.Location
        val decodedBle = ProbeProtocol.decode(ProbeProtocol.encodeBle(ble))
            as ProbeProtocol.Packet.Ble

        assertEquals(location, decodedLocation.sample)
        assertEquals(ble, decodedBle.sample)
        assertTrue(decodedBle.sample.deviceName!!.contains("Target"))
    }
}
