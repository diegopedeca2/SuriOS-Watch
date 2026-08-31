package com.suri.pipsurios.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.prs.PrsOperatingMode
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

private enum class PrsOnlyPage {
    MENU,
    SCAN,
    GRID,
    DEVICES
}

/** Compact entry point for the cover-screen P.R.S. edition. */
@Composable
fun PrsOnlyApp() {
    var page by remember { mutableStateOf(PrsOnlyPage.MENU) }

    BackHandler(enabled = page == PrsOnlyPage.SCAN || page == PrsOnlyPage.GRID) {
        page = PrsOnlyPage.MENU
    }

    when (page) {
        PrsOnlyPage.MENU -> PrsOnlyMenuScreen(
            onScanSelected = { page = PrsOnlyPage.SCAN },
            onGridSelected = { page = PrsOnlyPage.GRID },
            onDevicesSelected = { page = PrsOnlyPage.DEVICES }
        )

        PrsOnlyPage.SCAN,
        PrsOnlyPage.GRID -> PrsTrackingScreen(
            mode = PrsOperatingMode.LOCAL_SCAN,
            compact = true,
            compactPage = if (page == PrsOnlyPage.SCAN) PrsCompactPage.SCAN else PrsCompactPage.GRID,
            onCompactPageSelected = { selectedPage ->
                page = when (selectedPage) {
                    PrsCompactPage.SCAN -> PrsOnlyPage.SCAN
                    PrsCompactPage.GRID -> PrsOnlyPage.GRID
                }
            },
            onCompactDevicesSelected = { page = PrsOnlyPage.DEVICES },
            onBack = { page = PrsOnlyPage.MENU }
        )

        PrsOnlyPage.DEVICES -> PrsDevicesScreen(
            compact = true,
            onBack = { page = PrsOnlyPage.MENU }
        )
    }
}

@Composable
private fun PrsOnlyMenuScreen(
    onScanSelected: () -> Unit,
    onGridSelected: () -> Unit,
    onDevicesSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
            .safeDrawingPadding()
    ) {
        Text(
            text = "P.R.S.",
            color = PipGreen,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "SELECT MENU",
                color = PipAmber,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            PrsOnlyMenuItem("> SCAN", "LIVE BLE CONTACTS", onScanSelected)
            PrsOnlyMenuItem("> GRID", "DENSITY DISPLAY", onGridSelected)
            PrsOnlyMenuItem("> DEVICES", "DEVICE RULES", onDevicesSelected)
        }
    }
}

@Composable
private fun PrsOnlyMenuItem(
    label: String,
    detail: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, PipGreenDim.copy(alpha = 0.72f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(label, color = PipGreen, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
        Text(detail, color = PipGreenDim, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
    }
}
