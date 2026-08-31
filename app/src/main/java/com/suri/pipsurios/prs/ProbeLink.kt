package com.suri.pipsurios.prs

import android.content.Context
import com.google.android.gms.wearable.Wearable
import com.suri.probeprotocol.ProbeProtocol

/** Phone-side command link. PROBE telemetry is accepted only for the active session/node. */
class ProbeLink(context: Context) {
    private val appContext = context.applicationContext
    private val nodeClient = Wearable.getNodeClient(appContext)
    private val messageClient = Wearable.getMessageClient(appContext)

    fun send(command: ProbeProtocol.Command, sessionId: String, onResult: (Boolean, String) -> Unit) {
        nodeClient.localNode
            .addOnSuccessListener { localNode ->
                nodeClient.connectedNodes
                    .addOnSuccessListener { nodes ->
                        val target = nodes
                            .filter { it.id != localNode.id }
                            .sortedByDescending { it.isNearby }
                            .firstOrNull()
                        if (target == null) {
                            onResult(false, "NO PROBE NODE CONNECTED")
                            return@addOnSuccessListener
                        }
                        if (command != ProbeProtocol.Command.STOP) {
                            ProbeTelemetryStore.expectProbe(target.id, sessionId)
                        }
                        val control = ProbeProtocol.Control(command, localNode.id, sessionId)
                        messageClient.sendMessage(target.id, ProbeProtocol.CONTROL_PATH, ProbeProtocol.encodeControl(control))
                            .addOnSuccessListener {
                                onResult(true, target.displayName)
                            }
                            .addOnFailureListener {
                                if (command != ProbeProtocol.Command.STOP) {
                                    ProbeTelemetryStore.clearExpectedProbe(target.id, sessionId)
                                }
                                onResult(false, formatFailure(it))
                            }
                    }
                    .addOnFailureListener { onResult(false, formatFailure(it)) }
            }
            .addOnFailureListener { onResult(false, formatFailure(it)) }
    }

    private fun formatFailure(error: Exception): String {
        val message = error.message.orEmpty()
        return if (message.contains("API_UNAVAILABLE", ignoreCase = true) || message.contains("Wearable.API", ignoreCase = true)) {
            "WEARABLE API UNAVAILABLE // PAIR WATCH 2 WITH WEAR OS"
        } else {
            message.ifBlank { "WEARABLE LINK FAILED" }
        }
    }
}
