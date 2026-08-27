package com.suri.surioswatch.remoteprobe

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.URL
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/** Discovers the A56 gateway on the local Wi-Fi and sends short JSON batches. */
class RemoteProbeClient(context: Context) {
    private val appContext = context.applicationContext
    private val nsdManager = appContext.getSystemService(NsdManager::class.java)
    private val wifiManager = appContext.getSystemService(WifiManager::class.java)
    private val queue = ConcurrentLinkedQueue<RemoteProbeObservation>()
    private var scheduler: ScheduledExecutorService? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null
    private var host: InetAddress? = null
    private var port = RemoteProbeWire.PORT
    private var multicastLock: WifiManager.MulticastLock? = null

    @Volatile
    private var running = false

    fun start() {
        if (running) return
        running = true
        RemoteProbeState.link = WatchProbeLink.SEARCHING
        scheduler = Executors.newSingleThreadScheduledExecutor()
        acquireMulticastLock()
        startDiscovery()
        scheduler?.scheduleAtFixedRate(::flushOrHeartbeat, 1L, 2L, TimeUnit.SECONDS)
    }

    fun enqueue(observation: RemoteProbeObservation) {
        if (running) queue.add(observation)
    }

    fun stop() {
        running = false
        discoveryListener?.let { listener -> runCatching { nsdManager?.stopServiceDiscovery(listener) } }
        discoveryListener = null
        host = null
        scheduler?.shutdownNow()
        scheduler = null
        releaseMulticastLock()
        queue.clear()
        RemoteProbeState.link = WatchProbeLink.STOPPED
    }

    private fun startDiscovery() {
        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                RemoteProbeState.link = WatchProbeLink.SEARCHING
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType == RemoteProbeWire.SERVICE_TYPE ||
                    serviceInfo.serviceType == RemoteProbeWire.SERVICE_TYPE.removeSuffix(".")) {
                    runCatching { nsdManager?.resolveService(serviceInfo, resolutionListener) }
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                if (host?.hostAddress == serviceInfo.host?.hostAddress) {
                    host = null
                    RemoteProbeState.link = WatchProbeLink.DISCONNECTED
                }
            }

            override fun onDiscoveryStopped(serviceType: String) = Unit
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                RemoteProbeState.link = WatchProbeLink.ERROR
                runCatching { nsdManager?.stopServiceDiscovery(this) }
            }
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                RemoteProbeState.link = WatchProbeLink.ERROR
            }
        }
        discoveryListener = listener
        runCatching {
            nsdManager?.discoverServices(
                RemoteProbeWire.SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                listener
            )
        }.onFailure {
            RemoteProbeState.link = WatchProbeLink.ERROR
        }
    }

    private val resolutionListener = object : NsdManager.ResolveListener {
        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
            host = serviceInfo.host
            port = serviceInfo.port
            RemoteProbeState.link = WatchProbeLink.DISCONNECTED
            if (!sendRequest("GET", RemoteProbeWire.HELLO_PATH, null)) markTransportFailure()
        }

        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
            RemoteProbeState.link = WatchProbeLink.DISCONNECTED
        }
    }

    private fun flushOrHeartbeat() {
        if (!running) return
        // Keep the broadcast fallback active: some Wear OS builds resolve NSD to
        // an unusable address while the local TCP gateway itself is reachable.
        discoverGatewayByBroadcast()
        if (host == null) return
        val batch = ArrayList<RemoteProbeObservation>(MAX_BATCH_SIZE)
        repeat(MAX_BATCH_SIZE) { queue.poll()?.let(batch::add) }
        if (batch.isEmpty()) {
            if (!sendRequest("GET", RemoteProbeWire.HEARTBEAT_PATH, null)) markTransportFailure()
            return
        }
        if (!sendRequest("POST", RemoteProbeWire.OBSERVATIONS_PATH, RemoteProbeWire.encodeBatch(batch))) {
            batch.forEach(queue::add)
            markTransportFailure()
        }
    }

    private fun discoverGatewayByBroadcast() {
        if (!running) return
        val request = RemoteProbeWire.DISCOVERY_REQUEST.toByteArray(Charsets.US_ASCII)
        runCatching {
            DatagramSocket().use { socket ->
                socket.broadcast = true
                socket.soTimeout = DISCOVERY_TIMEOUT_MILLIS
                socket.send(
                    DatagramPacket(
                        request,
                        request.size,
                        InetAddress.getByName("255.255.255.255"),
                        RemoteProbeWire.DISCOVERY_PORT
                    )
                )
                val responseBytes = ByteArray(128)
                val response = DatagramPacket(responseBytes, responseBytes.size)
                socket.receive(response)
                val message = String(response.data, 0, response.length, Charsets.US_ASCII)
                if (message.startsWith(RemoteProbeWire.DISCOVERY_RESPONSE)) {
                    host = response.address
                    port = message.substringAfter('|', RemoteProbeWire.PORT.toString()).toIntOrNull()
                        ?: RemoteProbeWire.PORT
                }
            }
        }
    }

    private fun markTransportFailure() {
        host = null
        RemoteProbeState.link = WatchProbeLink.SEARCHING
    }

    private fun sendRequest(method: String, path: String, body: String?): Boolean {
        val address = host ?: return false
        val hostText = address.hostAddress?.let { if (it.contains(':')) "[$it]" else it } ?: return false
        val connection = runCatching {
            (URL("http://$hostText:$port$path").openConnection() as HttpURLConnection).apply {
                requestMethod = method
                connectTimeout = REQUEST_TIMEOUT_MILLIS
                readTimeout = REQUEST_TIMEOUT_MILLIS
                useCaches = false
                setRequestProperty("Connection", "close")
                if (body != null) {
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                    outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
                }
            }
        }.getOrNull() ?: return false
        return try {
            val successful = connection.responseCode in 200..299
            connection.disconnect()
            if (successful) RemoteProbeState.link = WatchProbeLink.CONNECTED
            else RemoteProbeState.link = WatchProbeLink.DISCONNECTED
            successful
        } catch (_: Exception) {
            connection.disconnect()
            RemoteProbeState.link = WatchProbeLink.DISCONNECTED
            false
        }
    }

    private fun acquireMulticastLock() {
        multicastLock = runCatching {
            wifiManager?.createMulticastLock("PIP-SuriOS-RemoteProbe")?.apply {
                setReferenceCounted(false)
                acquire()
            }
        }.getOrNull()
    }

    private fun releaseMulticastLock() {
        multicastLock?.let { lock -> runCatching { if (lock.isHeld) lock.release() } }
        multicastLock = null
    }

    companion object {
        private const val MAX_BATCH_SIZE = 100
        private const val REQUEST_TIMEOUT_MILLIS = 1_500
        private const val DISCOVERY_TIMEOUT_MILLIS = 700
    }
}
