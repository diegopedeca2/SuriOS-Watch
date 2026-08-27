package com.suri.pipsurios.remoteprobe

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Handler
import android.os.Looper
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/** Local-only HTTP gateway hosted by the A56 while the P.R.S. screen is open. */
class RemoteProbeGateway(
    context: Context,
    private val onProbeObservations: (List<RemoteProbeObservation>) -> Unit
) {
    private val nsdManager = context.getSystemService(NsdManager::class.java)
    private val mainHandler = Handler(Looper.getMainLooper())
    private var executor: ExecutorService? = null
    private var serverSocket: ServerSocket? = null
    private var discoverySocket: DatagramSocket? = null
    private var registrationListener: NsdManager.RegistrationListener? = null

    @Volatile
    private var running = false

    @Volatile
    private var state = RemoteProbeLink.STOPPED

    @Volatile
    private var lastPacketAtEpochMillis = 0L

    fun start() {
        if (running) return
        running = true
        state = RemoteProbeLink.STARTING
        executor = Executors.newCachedThreadPool()
        executor?.execute {
            try {
                val socket = ServerSocket().apply {
                    reuseAddress = true
                    bind(InetSocketAddress(RemoteProbeProtocol.PORT))
                }
                serverSocket = socket
                startDiscoveryResponder()
                registerService()
                state = RemoteProbeLink.LISTENING
                while (running) {
                    val client = socket.accept()
                    executor?.execute { handleClient(client) }
                }
            } catch (_: Exception) {
                if (running) state = RemoteProbeLink.ERROR
            }
        }
    }

    fun status(nowEpochMillis: Long = System.currentTimeMillis()): RemoteProbeLink {
        if (!running) return RemoteProbeLink.STOPPED
        if (lastPacketAtEpochMillis > 0L && nowEpochMillis - lastPacketAtEpochMillis <= LINK_TIMEOUT_MILLIS) {
            return RemoteProbeLink.CONNECTED
        }
        return state
    }

    fun stop() {
        if (!running && state == RemoteProbeLink.STOPPED) return
        running = false
        serverSocket?.close()
        serverSocket = null
        discoverySocket?.close()
        discoverySocket = null
        registrationListener?.let { listener ->
            mainHandler.post {
                runCatching { nsdManager?.unregisterService(listener) }
            }
        }
        registrationListener = null
        executor?.shutdownNow()
        executor = null
        lastPacketAtEpochMillis = 0L
        state = RemoteProbeLink.STOPPED
    }

    private fun startDiscoveryResponder() {
        val socket = DatagramSocket(null).apply {
            reuseAddress = true
            bind(InetSocketAddress(RemoteProbeProtocol.DISCOVERY_PORT))
            soTimeout = DISCOVERY_SOCKET_TIMEOUT_MILLIS
        }
        discoverySocket = socket
        executor?.execute {
            val buffer = ByteArray(128)
            while (running) {
                try {
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket.receive(packet)
                    val request = String(packet.data, 0, packet.length, StandardCharsets.US_ASCII)
                    if (request != RemoteProbeProtocol.DISCOVERY_REQUEST) continue
                    val response = "${RemoteProbeProtocol.DISCOVERY_RESPONSE}|${RemoteProbeProtocol.PORT}"
                        .toByteArray(StandardCharsets.US_ASCII)
                    socket.send(DatagramPacket(response, response.size, packet.address, packet.port))
                } catch (_: SocketTimeoutException) {
                    // Re-check running so stop() can close the responder promptly.
                } catch (_: Exception) {
                    if (running) state = RemoteProbeLink.ERROR
                    break
                }
            }
        }
    }

    private fun registerService() {
        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(serviceInfo: NsdServiceInfo) = Unit
            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                if (running) state = RemoteProbeLink.ERROR
            }
            override fun onServiceUnregistered(serviceInfo: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) = Unit
        }
        registrationListener = listener
        mainHandler.post {
            if (!running) return@post
            runCatching {
                nsdManager?.registerService(
                    NsdServiceInfo().apply {
                        serviceName = RemoteProbeProtocol.SERVICE_NAME
                        serviceType = RemoteProbeProtocol.SERVICE_TYPE
                        port = RemoteProbeProtocol.PORT
                    },
                    NsdManager.PROTOCOL_DNS_SD,
                    listener
                )
            }.onFailure {
                if (running) state = RemoteProbeLink.ERROR
            }
        }
    }

    private fun handleClient(socket: Socket) {
        socket.use { client ->
            runCatching {
                client.soTimeout = CLIENT_TIMEOUT_MILLIS
                val input = client.getInputStream()
                val requestLine = input.readAsciiLine() ?: return
                val headers = mutableMapOf<String, String>()
                while (true) {
                    val line = input.readAsciiLine() ?: return
                    if (line.isEmpty()) break
                    val separator = line.indexOf(':')
                    if (separator > 0) {
                        headers[line.substring(0, separator).trim().lowercase()] =
                            line.substring(separator + 1).trim()
                    }
                }
                val method = requestLine.substringBefore(' ')
                val path = requestLine.substringAfter(' ', "").substringBefore(' ')
                    .substringBefore('?')
                val contentLength = headers["content-length"]?.toIntOrNull()?.coerceAtLeast(0) ?: 0
                val body = input.readBytesExact(contentLength).toString(StandardCharsets.UTF_8)
                when {
                    method == "GET" && path == RemoteProbeProtocol.HELLO_PATH -> {
                        markConnected()
                        writeResponse(client, 200, "{\"ok\":true}")
                    }
                    method == "GET" && path == RemoteProbeProtocol.HEARTBEAT_PATH -> {
                        markConnected()
                        writeResponse(client, 200, "{\"ok\":true}")
                    }
                    method == "POST" && path == RemoteProbeProtocol.OBSERVATIONS_PATH -> {
                        val observations = RemoteProbeProtocol.decodeBatch(body)
                        markConnected()
                        onProbeObservations(observations)
                        writeResponse(client, 200, "{\"accepted\":${observations.size}}")
                    }
                    else -> writeResponse(client, 404, "{\"ok\":false}")
                }
            }
        }
    }

    private fun markConnected() {
        lastPacketAtEpochMillis = System.currentTimeMillis()
    }

    private fun writeResponse(socket: Socket, statusCode: Int, body: String) {
        val bytes = body.toByteArray(StandardCharsets.UTF_8)
        val response = buildString {
            append("HTTP/1.1 ").append(statusCode).append(if (statusCode == 200) " OK" else " ERROR").append("\r\n")
            append("Content-Type: application/json\r\n")
            append("Content-Length: ").append(bytes.size).append("\r\n")
            append("Connection: close\r\n\r\n")
        }.toByteArray(StandardCharsets.US_ASCII)
        socket.getOutputStream().use { output ->
            output.write(response)
            output.write(bytes)
            output.flush()
        }
    }

    companion object {
        private const val CLIENT_TIMEOUT_MILLIS = 2_500
        private const val LINK_TIMEOUT_MILLIS = 7_000L
        private const val DISCOVERY_SOCKET_TIMEOUT_MILLIS = 1_000
    }
}

private fun InputStream.readAsciiLine(): String? {
    val bytes = ByteArrayOutputStream()
    while (true) {
        val value = read()
        if (value < 0) return if (bytes.size() == 0) null else bytes.toString(Charsets.US_ASCII.name())
        if (value == '\n'.code) break
        if (value != '\r'.code) bytes.write(value)
    }
    return bytes.toString(Charsets.US_ASCII.name())
}

private fun InputStream.readBytesExact(length: Int): ByteArray {
    if (length == 0) return ByteArray(0)
    val result = ByteArray(length)
    var offset = 0
    while (offset < length) {
        val read = read(result, offset, length - offset)
        if (read < 0) return result.copyOf(offset)
        offset += read
    }
    return result
}
