package com.suri.pipsurios.ui.screens

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private const val CIVTAK_PACKAGE = "com.atakmap.app.civ"

private enum class CivTakStatus(val text: String) {
    Checking("CHECKING CivTAK..."),
    Launching("LAUNCHING"),
    NotFound("NOT FOUND")
}

@Composable
fun CivTakLoadingScreen(
    onFinished: () -> Unit,
    onExternalLaunch: () -> Unit
) {
    val context = LocalContext.current
    var status by remember { mutableStateOf(CivTakStatus.Checking) }

    LaunchedEffect(Unit) {
        withFrameNanos { }
        val launchIntent = context.packageManager.safeLaunchIntent(CIVTAK_PACKAGE)

        if (launchIntent != null) {
            status = CivTakStatus.Launching
            delay(1_500)
            onExternalLaunch()
            context.startActivity(launchIntent)
        } else {
            status = CivTakStatus.NotFound
            delay(1_500)
            onFinished()
        }
    }

    ExternalLaunchStatus(text = status.text)
}

@Composable
internal fun ExternalLaunchStatus(text: String) {
    TerminalScreen {
        LoadingGlitchText(
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp,
            text = text
        )
    }
}

internal fun PackageManager.safeLaunchIntent(packageName: String): Intent? {
    return try {
        getApplicationInfo(packageName, 0)
        getLaunchIntentForPackage(packageName)?.takeIf { intent ->
            intent.resolveActivity(this) != null
        }
    } catch (_: PackageManager.NameNotFoundException) {
        null
    }
}
