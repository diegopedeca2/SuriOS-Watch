package com.suri.surioswatch.probe

import java.util.concurrent.CopyOnWriteArraySet

/**
 * Runtime health shared by the visible PROBE activity and the headless
 * acquisition service. It is intentionally process-local; the phone remains
 * the owner of the persisted telemetry/session state.
 */
data class ProbeRuntimeSnapshot(
    val state: String = "IDLE",
    val message: String? = null
)

object ProbeRuntimeState {
    @Volatile
    private var current = ProbeRuntimeSnapshot()
    private val listeners = CopyOnWriteArraySet<(ProbeRuntimeSnapshot) -> Unit>()

    fun snapshot(): ProbeRuntimeSnapshot = current

    fun observe(listener: (ProbeRuntimeSnapshot) -> Unit): () -> Unit {
        listeners += listener
        listener(current)
        return { listeners -= listener }
    }

    fun update(state: String, message: String?) {
        val next = ProbeRuntimeSnapshot(state = state, message = message)
        current = next
        listeners.forEach { it(next) }
    }
}
