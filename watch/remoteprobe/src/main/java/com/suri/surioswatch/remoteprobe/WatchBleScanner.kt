package com.suri.surioswatch.remoteprobe

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import java.util.Locale

class WatchBleScanner(private val context: Context) {
    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private var callback: ScanCallback? = null

    fun hasRequiredPermissions(): Boolean =
        context.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun start(
        onObservation: (RemoteProbeObservation) -> Unit,
        onStatus: (WatchBleScanStatus) -> Unit
    ): WatchBleScanStatus {
        stop()
        if (!hasRequiredPermissions()) return WatchBleScanStatus.PERMISSION_REQUIRED
        val adapter = bluetoothManager?.adapter ?: return WatchBleScanStatus.UNSUPPORTED
        if (!adapter.isEnabled) return WatchBleScanStatus.BLUETOOTH_OFF
        val scanner = adapter.bluetoothLeScanner ?: return WatchBleScanStatus.UNSUPPORTED

        val scanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val identifier = runCatching { result.device.address.uppercase(Locale.US) }
                    .getOrDefault("UNKNOWN")
                val now = System.currentTimeMillis()
                onObservation(
                    RemoteProbeObservation(
                        timestampEpochMillis = now,
                        deviceIdentifier = identifier,
                        rssi = result.rssi,
                        deviceName = result.scanRecord?.deviceName,
                        advertisingDataHex = result.scanRecord?.bytes?.toHexString(),
                        deviceType = runCatching { result.device.type }.getOrNull()
                    )
                )
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>) {
                results.forEach { onScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, it) }
            }

            override fun onScanFailed(errorCode: Int) {
                callback = null
                onStatus(WatchBleScanStatus.ERROR)
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
            WatchBleScanStatus.SCANNING
        } catch (_: SecurityException) {
            callback = null
            WatchBleScanStatus.PERMISSION_REQUIRED
        } catch (_: IllegalStateException) {
            callback = null
            WatchBleScanStatus.ERROR
        }
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        val activeCallback = callback ?: return
        callback = null
        runCatching { bluetoothManager?.adapter?.bluetoothLeScanner?.stopScan(activeCallback) }
    }
}

private fun ByteArray.toHexString(): String =
    joinToString(separator = "") { byte -> "%02X".format(Locale.US, byte.toInt() and 0xFF) }
