package com.suri.pipsurios.binary

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

// 1 is a long pulse; 0 is a fast double pulse, so both patterns are distinct.
const val BINARY_ONE_FLASH_DURATION_MS = 550L
const val BINARY_ZERO_FLASH_DURATION_MS = 120L
const val BINARY_ZERO_FLASH_GAP_MS = 100L
const val BINARY_BIT_GAP_MS = 550L
const val BINARY_BYTE_GAP_MS = 1_000L

/** Sends binary bits through the rear camera light: 1 pulse for 1, 2 for 0. */
class BinaryTransmitter(context: Context) {
    private val appContext = context.applicationContext
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val cameraId = runCatching {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }.getOrNull()

    val hasFlash: Boolean get() = cameraId != null

    val isAvailable: Boolean
        get() = cameraId != null && ContextCompat.checkSelfPermission(
            appContext,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    suspend fun transmit(encodedMessage: String) {
        val id = cameraId ?: throw IllegalStateException("FLASH_UNAVAILABLE")
        check(isAvailable) { "CAMERA_PERMISSION_REQUIRED" }
        val bytes = encodedMessage.trim().split(Regex("\\s+")).filter(String::isNotEmpty)

        try {
            bytes.forEachIndexed { byteIndex, byte ->
                require(byte.length == 8 && byte.all { it == '0' || it == '1' }) {
                    "INVALID_BINARY"
                }
                byte.forEachIndexed { bitIndex, bit ->
                    val flashes = if (bit == '0') 2 else 1
                    repeat(flashes) { flashIndex ->
                        setTorch(id, true)
                        delay(
                            if (bit == '0') BINARY_ZERO_FLASH_DURATION_MS
                            else BINARY_ONE_FLASH_DURATION_MS
                        )
                        setTorch(id, false)
                        if (flashIndex < flashes - 1) delay(BINARY_ZERO_FLASH_GAP_MS)
                    }
                    if (bitIndex < byte.lastIndex) delay(BINARY_BIT_GAP_MS)
                }
                if (byteIndex < bytes.lastIndex) delay(BINARY_BYTE_GAP_MS)
            }
        } finally {
            turnOff()
        }
    }

    fun turnOff() {
        cameraId?.let { id -> runCatching { cameraManager.setTorchMode(id, false) } }
    }

    private fun setTorch(id: String, enabled: Boolean) {
        cameraManager.setTorchMode(id, enabled)
    }
}
