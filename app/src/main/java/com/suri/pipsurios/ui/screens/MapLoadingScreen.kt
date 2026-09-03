package com.suri.pipsurios.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun MapLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    TerminalScreen {
        LoadingGlitchText(
            modifier = Modifier.align(Alignment.Center),
            fontSize = 24.sp
        )
    }
}
