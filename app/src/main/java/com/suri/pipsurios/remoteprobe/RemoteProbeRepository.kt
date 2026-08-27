package com.suri.pipsurios.remoteprobe

import android.content.Context
import java.io.File

/** Stores raw observations locally so the Saturday test can be inspected afterwards. */
class RemoteProbeRepository(private val root: File) {
    @Synchronized
    fun createSession(nowEpochMillis: Long = System.currentTimeMillis()): String {
        root.mkdirs()
        val existing = root.listFiles()?.mapNotNull { file ->
            SESSION_PATTERN.matchEntire(file.name)?.groupValues?.get(1)?.toIntOrNull()
        }?.maxOrNull() ?: 0
        val id = "RPR-${nowEpochMillis}-${(existing + 1).toString().padStart(3, '0')}"
        val sessionDir = File(root, id).apply { mkdirs() }
        File(sessionDir, "session.properties").writeText(
            "sessionId=$id\ncreatedAtEpochMillis=$nowEpochMillis\n",
            Charsets.UTF_8
        )
        File(sessionDir, OBSERVATIONS_FILE).writeText(CSV_HEADER, Charsets.UTF_8)
        return id
    }

    @Synchronized
    fun append(sessionId: String, observation: RemoteProbeObservation) {
        val file = sessionFile(sessionId)
        file.parentFile?.mkdirs()
        if (!file.exists()) file.writeText(CSV_HEADER, Charsets.UTF_8)
        file.appendText(
            listOf(
                observation.timestampEpochMillis.toString(),
                observation.node.name,
                observation.deviceIdentifier,
                observation.rssi.toString(),
                observation.deviceName,
                observation.advertisingDataHex,
                observation.deviceType?.toString()
            ).joinToString(",") { it.csvField() } + "\n",
            Charsets.UTF_8
        )
    }

    private fun sessionFile(sessionId: String): File {
        require(SESSION_PATTERN.matches(sessionId)) { "Invalid remote probe session ID" }
        return File(File(root, sessionId), OBSERVATIONS_FILE)
    }

    companion object {
        private const val OBSERVATIONS_FILE = "observations.csv"
        private const val CSV_HEADER =
            "timestamp_epoch_millis,node,device_identifier,rssi,device_name,advertising_data_hex,device_type\n"
        private val SESSION_PATTERN = Regex("^RPR-(\\d+)-\\d{3}$")

        fun from(context: Context): RemoteProbeRepository =
            RemoteProbeRepository(File(context.filesDir, "remote-probe"))
    }
}

private fun String?.csvField(): String =
    "\"${orEmpty().replace("\"", "\"\"")}\""
