package com.suri.pipsurios.terrain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper

data class TerrainLocationFix(
    val point: GeoPoint,
    val accuracyMeters: Float,
    val timestampEpochMillis: Long = 0L,
    val provider: String? = null
)

class TerrainLocation(private val context: Context) {
    private val manager = context.getSystemService(LocationManager::class.java)
    private var listener: LocationListener? = null
    private var providers = emptyList<String>()
    private val currentLocationSignals = mutableListOf<CancellationSignal>()
    private var selectedFix: TerrainLocationFix? = null
    private var selectedFixReceivedAtEpochMillis = 0L
    fun hasPermission() = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun start(onFix: (TerrainLocationFix) -> Unit, onUnavailable: () -> Unit) {
        stop()
        if (!hasPermission()) return
        val locationManager = manager ?: run { onUnavailable(); return }
        providers = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                locationManager.getProvider(LocationManager.FUSED_PROVIDER) != null
            ) add(LocationManager.FUSED_PROVIDER)
            add(LocationManager.GPS_PROVIDER)
            add(LocationManager.NETWORK_PROVIDER)
        }.filter { provider ->
            runCatching { locationManager.isProviderEnabled(provider) }.getOrDefault(false)
        }.distinct()
        if (providers.isEmpty()) { onUnavailable(); return }
        val active = object : LocationListener {
            override fun onLocationChanged(location: Location) = emitIfUseful(location, onFix)
            override fun onProviderDisabled(provider: String) {
                if (providers.all { it == provider || !locationManager.isProviderEnabled(it) }) onUnavailable()
            }
            override fun onProviderEnabled(provider: String) = Unit
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }
        listener = active
        providers.forEach { provider ->
            runCatching {
                locationManager.requestLocationUpdates(provider, 2_000L, 0f, active, Looper.getMainLooper())
                locationManager.getLastKnownLocation(provider)?.let { location ->
                    emitIfUseful(location, onFix)
                }
                val signal = CancellationSignal()
                currentLocationSignals += signal
                locationManager.getCurrentLocation(provider, signal, context.mainExecutor) { location ->
                    if (location != null) emitIfUseful(location, onFix)
                }
            }
        }
    }
    fun stop() {
        currentLocationSignals.forEach(CancellationSignal::cancel)
        currentLocationSignals.clear()
        listener?.let { manager?.removeUpdates(it) }
        listener = null
        providers = emptyList()
        selectedFix = null
        selectedFixReceivedAtEpochMillis = 0L
    }

    private fun emitIfUseful(location: Location, onFix: (TerrainLocationFix) -> Unit) {
        if (!location.hasAccuracy() || !location.latitude.isFinite() || !location.longitude.isFinite()) return
        val fix = TerrainLocationFix(
            point = GeoPoint(location.latitude, location.longitude),
            accuracyMeters = location.accuracy,
            timestampEpochMillis = location.time,
            provider = location.provider
        )
        val now = System.currentTimeMillis()
        // A last-known fix from a previous session is not useful for a live
        // relative position and can be days old on Wear/Android devices.
        if (fix.timestampEpochMillis <= 0L || now - fix.timestampEpochMillis > MAX_ACCEPTED_FIX_AGE_MILLIS) return
        val current = selectedFix
        val currentAge = now - selectedFixReceivedAtEpochMillis
        val sameProvider = fix.provider.equals(current?.provider, ignoreCase = true)
        if (current != null && currentAge <= PROVIDER_SWITCH_AFTER_MILLIS &&
            !sameProvider && fix.accuracyMeters > current.accuracyMeters
        ) {
            // Keep the best recent provider instead of jumping between GPS,
            // fused and network coordinates on every callback.
            return
        }
        selectedFix = fix
        selectedFixReceivedAtEpochMillis = now
        onFix(fix)
    }

    companion object {
        private const val MAX_ACCEPTED_FIX_AGE_MILLIS = 60_000L
        private const val PROVIDER_SWITCH_AFTER_MILLIS = 15_000L
    }
}
