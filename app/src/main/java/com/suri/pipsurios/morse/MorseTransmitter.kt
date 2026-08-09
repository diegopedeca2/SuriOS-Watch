package com.suri.pipsurios.morse

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import kotlinx.coroutines.delay

const val MORSE_TIME_UNIT_MS = 200L

class MorseTransmitter(context: Context) {
    private val cameraManager = context.getSystemService(CameraManager::class.java)
    private val cameraId = runCatching {
        cameraManager.cameraIdList.firstOrNull { id ->
            cameraManager.getCameraCharacteristics(id)
                .get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
        }
    }.getOrNull()

    val isAvailable: Boolean get() = cameraId != null

    suspend fun transmit(encodedMessage: String) {
        val id = cameraId ?: return
        val words = encodedMessage.split("__")
        try {
            words.forEachIndexed { wordIndex, word ->
                val letters = word.split('_')
                letters.forEachIndexed { letterIndex, letter ->
                    letter.forEachIndexed { symbolIndex, symbol ->
                        setTorch(id, true)
                        delay(MORSE_TIME_UNIT_MS * if (symbol == '-') 3 else 1)
                        setTorch(id, false)
                        if (symbolIndex < letter.lastIndex) delay(MORSE_TIME_UNIT_MS)
                    }
                    if (letterIndex < letters.lastIndex) delay(MORSE_TIME_UNIT_MS * 3)
                }
                if (wordIndex < words.lastIndex) delay(MORSE_TIME_UNIT_MS * 7)
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
