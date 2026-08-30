package com.suri.pipsurios.prs

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.suri.probeprotocol.ProbeProtocol

/** Receives persisted PROBE telemetry even when P.R.S. was temporarily closed. */
class ProbeDataLayerService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type != DataEvent.TYPE_CHANGED) return@forEach
            val item = event.dataItem
            if (!item.uri.path.orEmpty().startsWith(ProbeProtocol.TELEMETRY_PATH_PREFIX)) return@forEach
            item.data?.let(ProbeProtocol::decode)?.let(ProbeTelemetryStore::publish)
            Wearable.getDataClient(this).deleteDataItems(item.uri)
        }
    }
}
