package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import kotlinx.coroutines.delay

@Composable
fun CommsLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "LOADING...",
            color = PipGreen,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
