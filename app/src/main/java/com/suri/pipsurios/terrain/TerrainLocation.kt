package com.suri.pipsurios.terrain

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

data class TerrainLocationFix(val point: GeoPoint, val accuracyMeters: Float)

class TerrainLocation(private val context: Context) {
    private val manager = context.getSystemService(LocationManager::class.java)
    private var listener: LocationListener? = null
    fun hasPermission() = context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    fun start(onFix: (TerrainLocationFix) -> Unit, onUnavailable: () -> Unit) {
        stop()
        if (!hasPermission()) return
        val provider = when {
            manager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true -> LocationManager.GPS_PROVIDER
            manager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true -> LocationManager.NETWORK_PROVIDER
            else -> { onUnavailable(); return }
        }
        val active = object : LocationListener {
            override fun onLocationChanged(location: Location) = onFix(TerrainLocationFix(GeoPoint(location.latitude, location.longitude), location.accuracy))
            override fun onProviderDisabled(provider: String) = onUnavailable()
            override fun onProviderEnabled(provider: String) = Unit
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }
        listener = active
        manager?.requestLocationUpdates(provider, 4_000L, 2f, active)
    }
    fun stop() { listener?.let { manager?.removeUpdates(it) }; listener = null }
}
