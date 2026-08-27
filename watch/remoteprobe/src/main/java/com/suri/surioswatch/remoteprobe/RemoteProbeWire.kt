package com.suri.surioswatch.remoteprobe

import org.json.JSONArray
import org.json.JSONObject

object RemoteProbeWire {
    const val SERVICE_TYPE = "_pipprs._tcp."
    const val PORT = 28_771
    const val DISCOVERY_PORT = 28_772
    const val DISCOVERY_REQUEST = "PIP-SuriOS-REMOTE-PROBE-DISCOVER"
    const val DISCOVERY_RESPONSE = "PIP-SuriOS-REMOTE-PROBE-GATEWAY"
    const val HELLO_PATH = "/prs/remote-probe/hello"
    const val HEARTBEAT_PATH = "/prs/remote-probe/heartbeat"
    const val OBSERVATIONS_PATH = "/prs/remote-probe/observations"

    fun encodeBatch(observations: List<RemoteProbeObservation>): String =
        JSONArray().apply {
            observations.forEach { observation ->
                put(JSONObject().apply {
                    put("timestampEpochMillis", observation.timestampEpochMillis)
                    put("deviceIdentifier", observation.deviceIdentifier)
                    put("rssi", observation.rssi)
                    putOpt("deviceName", observation.deviceName)
                    putOpt("advertisingDataHex", observation.advertisingDataHex)
                    observation.deviceType?.let { put("deviceType", it) }
                })
            }
        }.toString()

    fun encodeLocalLog(observation: RemoteProbeObservation): String =
        JSONObject().apply {
            put("node", "PROBE")
            put("timestampEpochMillis", observation.timestampEpochMillis)
            put("deviceIdentifier", observation.deviceIdentifier)
            put("rssi", observation.rssi)
            putOpt("deviceName", observation.deviceName)
            putOpt("advertisingDataHex", observation.advertisingDataHex)
            observation.deviceType?.let { put("deviceType", it) }
        }.toString()
}
