package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    var loginVerifiedVisible by remember { mutableStateOf(false) }
    var systemReadyVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(1_500)
        loginVerifiedVisible = true
        delay(1_500)
        systemReadyVisible = true
        delay(1_500)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LoadingLine(text = "LOADING...")
            LoadingLine(
                text = "LOG-IN ID: SURI-14 VERIFIED",
                modifier = Modifier.alpha(if (loginVerifiedVisible) 1f else 0f)
            )
            LoadingLine(
                text = "SYSTEM READY",
                modifier = Modifier.alpha(if (systemReadyVisible) 1f else 0f)
            )
        }
    }
}

@Composable
private fun LoadingLine(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = PipGreenDim,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
    )
}
