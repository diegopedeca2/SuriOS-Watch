package com.suri.surioswatch.remoteprobe

data class RemoteProbeObservation(
    val timestampEpochMillis: Long,
    val deviceIdentifier: String,
    val rssi: Int,
    val deviceName: String? = null,
    val advertisingDataHex: String? = null,
    val deviceType: Int? = null
)

enum class WatchProbeLink {
    DISCONNECTED,
    SEARCHING,
    CONNECTED,
    ERROR,
    STOPPED
}

enum class WatchBleScanStatus {
    STOPPED,
    SCANNING,
    PERMISSION_REQUIRED,
    BLUETOOTH_OFF,
    UNSUPPORTED,
    ERROR
}

object RemoteProbeState {
    @Volatile var scanStatus: WatchBleScanStatus = WatchBleScanStatus.STOPPED
    @Volatile var link: WatchProbeLink = WatchProbeLink.STOPPED
    @Volatile var contactCount: Int = 0
    @Volatile var lastError: String? = null

    private val contacts = mutableMapOf<String, Long>()

    @Synchronized
    fun observe(identifier: String, nowEpochMillis: Long) {
        contacts[identifier] = nowEpochMillis
        expireLocked(nowEpochMillis)
        contactCount = contacts.size
    }

    @Synchronized
    fun expire(nowEpochMillis: Long) {
        expireLocked(nowEpochMillis)
        contactCount = contacts.size
    }

    @Synchronized
    fun reset() {
        contacts.clear()
        contactCount = 0
        scanStatus = WatchBleScanStatus.STOPPED
        link = WatchProbeLink.STOPPED
        lastError = null
    }

    private fun expireLocked(nowEpochMillis: Long) {
        contacts.entries.removeIf { nowEpochMillis - it.value > CONTACT_EXPIRY_MILLIS }
    }

    private const val CONTACT_EXPIRY_MILLIS = 15_000L
}
