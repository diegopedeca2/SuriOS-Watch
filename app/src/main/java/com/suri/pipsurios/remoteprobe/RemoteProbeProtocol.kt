package com.suri.pipsurios.remoteprobe

import org.json.JSONArray
import org.json.JSONObject

/** Small JSON wire format shared by the A56 gateway and the Wear app. */
object RemoteProbeProtocol {
    const val SERVICE_TYPE = "_pipprs._tcp."
    const val SERVICE_NAME = "PIP-SuriOS-REMOTE-PROBE"
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

    fun decodeBatch(payload: String): List<RemoteProbeObservation> {
        val array = JSONArray(payload)
        return buildList {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                val identifier = item.optString("deviceIdentifier").trim()
                if (identifier.isEmpty() || !item.has("timestampEpochMillis") || !item.has("rssi")) continue
                add(
                    RemoteProbeObservation(
                        node = RemoteProbeNode.PROBE,
                        timestampEpochMillis = item.optLong("timestampEpochMillis"),
                        deviceIdentifier = identifier,
                        rssi = item.optInt("rssi"),
                        deviceName = item.optNullableString("deviceName"),
                        advertisingDataHex = item.optNullableString("advertisingDataHex"),
                        deviceType = item.takeIf { it.has("deviceType") }?.optInt("deviceType")
                    )
                )
            }
        }
    }
}

private fun JSONObject.optNullableString(key: String): String? =
    if (!has(key) || isNull(key)) null else optString(key).takeIf { it.isNotEmpty() }
