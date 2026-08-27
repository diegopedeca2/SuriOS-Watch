package com.suri.surioswatch.remoteprobe

import android.content.Context
import java.io.File

class RemoteProbeLocalStore(context: Context) {
    private val file = File(context.filesDir, "remote-probe/observations.ndjson")

    @Synchronized
    fun append(observation: RemoteProbeObservation) {
        file.parentFile?.mkdirs()
        file.appendText(RemoteProbeWire.encodeLocalLog(observation) + "\n", Charsets.UTF_8)
    }
}
