package com.suri.pipsurios.ui.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

private enum class PrsOnlyPage {
    TRACKING,
    MENU,
    DEVICES
}

/** Compact entry point for the cover-screen P.R.S. edition. */
@Composable
fun PrsOnlyApp() {
    val context = LocalContext.current
    var page by remember { mutableStateOf(PrsOnlyPage.TRACKING) }

    when (page) {
        PrsOnlyPage.TRACKING -> PrsTrackingScreen(
            mode = PrsOperatingMode.LOCAL_SCAN,
            compact = true,
            onBack = { page = PrsOnlyPage.MENU }
        )

        PrsOnlyPage.MENU -> PrsOnlyMenuScreen(
            onLocalScanSelected = { page = PrsOnlyPage.TRACKING },
            onDevicesSelected = { page = PrsOnlyPage.DEVICES },
            onBack = { (context as? Activity)?.finish() }
        )

        PrsOnlyPage.DEVICES -> PrsDevicesScreen(
            compact = true,
            onBack = { page = PrsOnlyPage.MENU }
        )
    }
}

@Composable
private fun PrsOnlyMenuScreen(
    onLocalScanSelected: () -> Unit,
    onDevicesSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Column(
            modifier = Modifier
                .widthIn(max = 410.dp)
                .fillMaxWidth()
                .align(Alignment.Center)
            .padding(horizontal = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("P.R.S. / COVER", color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Text("MOTORCYCLE PROFILE // LOCAL BLE ONLY", color = PipAmber, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
            PrsOnlyMenuRow(
                label = "> SCAN",
                detail = "A56 BLE / RSSI DENSITY",
                onClick = onLocalScanSelected
            )
            PrsOnlyMenuRow(
                label = "> DEVICES",
                detail = "IDENTIFY / SAVED FILTERS",
                onClick = onDevicesSelected
            )
        }

        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
        )
    }
}

@Composable
private fun PrsOnlyMenuRow(
    label: String,
    detail: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = PipGreen, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
        Text(detail, color = PipGreenDim, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}
