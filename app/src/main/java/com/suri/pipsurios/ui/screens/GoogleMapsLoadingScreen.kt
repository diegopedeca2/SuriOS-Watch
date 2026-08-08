package com.suri.pipsurios.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

private const val GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps"

private enum class GoogleMapsStatus(val text: String) {
    Checking("CHECKING GOOGLE MAPS..."),
    Launching("LAUNCHING"),
    NotFound("NOT FOUND")
}

@Composable
fun GoogleMapsLoadingScreen(onExternalLaunch: () -> Unit) {
    val context = LocalContext.current
    var status by remember { mutableStateOf(GoogleMapsStatus.Checking) }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        val launchIntent = context.packageManager.safeLaunchIntent(GOOGLE_MAPS_PACKAGE)

        if (launchIntent != null) {
            status = GoogleMapsStatus.Launching
            delay(1_500)
            onExternalLaunch()
            context.startActivity(launchIntent)
        } else {
            status = GoogleMapsStatus.NotFound
        }
    }

    ExternalLaunchStatus(text = status.text)
}
