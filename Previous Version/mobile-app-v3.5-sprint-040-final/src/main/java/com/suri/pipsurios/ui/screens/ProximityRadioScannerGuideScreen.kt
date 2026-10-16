package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.suri.pipsurios.ui.theme.PipBlack

/** Placeholder kept in the P.R.S. menu until the operating procedure is ready. */
@Composable
fun ProximityRadioScannerGuideScreen(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        PrsBackButton(
            onBack = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        )
    }
}
