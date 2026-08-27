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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay

private val homeModules = listOf(
    "SET-UP",
    "CURRENT GEAR",
    "INVENTORY",
    "STATUS",
    "DATA",
    "TOOLS"
)

@Composable
fun LoadingScreen(onFinished: () -> Unit) {
    var visibleLineCount by remember { mutableIntStateOf(1) }
    var readyModuleCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        delay(1_500)
        visibleLineCount++
        delay(1_500)
        homeModules.forEachIndexed { index, _ ->
            visibleLineCount++
            delay(750)
            readyModuleCount = index + 1
            delay(250)
        }
        visibleLineCount++
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
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LoadingLine(text = "LOADING...")
            if (visibleLineCount >= 2) {
                LoadingLine(text = "LOG-IN ID: SURI-14 VERIFIED")
            }
            homeModules.forEachIndexed { index, module ->
                if (visibleLineCount >= index + 3) {
                    val readySuffix = if (readyModuleCount > index) " READY" else ""
                    LoadingLine(text = "LOADING $module.....$readySuffix")
                }
            }
            if (visibleLineCount >= homeModules.size + 3) {
                LoadingLine(text = "SYSTEM READY")
            }
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
