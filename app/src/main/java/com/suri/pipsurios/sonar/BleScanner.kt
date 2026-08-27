package com.suri.pipsurios.sonar

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.SystemClock
import java.util.Locale

enum class BleScanStatus {
    IDLE,
    SCANNING,
    PERMISSION_REQUIRED,
    BLUETOOTH_OFF,
    UNSUPPORTED,
    ERROR
}

class BleScanner(private val context: Context) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val sessionIds = mutableMapOf<String, String>()
    private var nextSessionId = 1
    private var callback: ScanCallback? = null

    fun hasRequiredPermissions(): Boolean =
        context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun start(
        onObservation: (BleObservation) -> Unit,
        onStatusChanged: (BleScanStatus) -> Unit
    ): BleScanStatus {
        stop()
        if (!hasRequiredPermissions()) return BleScanStatus.PERMISSION_REQUIRED

        val adapter = bluetoothManager?.adapter ?: return BleScanStatus.UNSUPPORTED
        if (!adapter.isEnabled) return BleScanStatus.BLUETOOTH_OFF
        val scanner = adapter.bluetoothLeScanner ?: return BleScanStatus.UNSUPPORTED

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val temporaryId = sessionIds.getOrPut(result.device.address) {
                    "CONTACT-${nextSessionId++.toString().padStart(3, '0')}"
                }
                onObservation(
                    BleObservation(
                        temporaryId = temporaryId,
                        rssi = result.rssi,
                        observedAt = SystemClock.elapsedRealtime(),
                        deviceIdentifier = result.device.address.uppercase(Locale.US),
                        deviceName = result.scanRecord?.deviceName,
                        advertisingDataHex = result.scanRecord?.bytes?.toHexString(),
                        deviceType = runCatching { result.device.type }.getOrNull(),
                        observedAtEpochMillis = System.currentTimeMillis()
                    )
                )
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, it) }
            }

            override fun onScanFailed(errorCode: Int) {
                callback = null
                onStatusChanged(BleScanStatus.ERROR)
            }
        }

        return try {
            callback = scanCallback
            scanner.startScan(
                emptyList(),
                ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                    .build(),
                scanCallback
            )
            BleScanStatus.SCANNING
        } catch (_: SecurityException) {
            callback = null
            BleScanStatus.PERMISSION_REQUIRED
        } catch (_: IllegalStateException) {
            callback = null
            BleScanStatus.ERROR
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        val activeCallback = callback ?: return
        callback = null
        try {
            bluetoothManager?.adapter?.bluetoothLeScanner?.stopScan(activeCallback)
        } catch (_: SecurityException) {
            // Permission may be revoked while the screen is active.
        } catch (_: IllegalStateException) {
            // Bluetooth may be turned off while scanning.
        }
    }

    fun releaseSession() {
        stop()
        sessionIds.clear()
        nextSessionId = 1
    }
}

private fun ByteArray.toHexString(): String =
    joinToString(separator = "") { byte -> "%02X".format(Locale.US, byte.toInt() and 0xFF) }
