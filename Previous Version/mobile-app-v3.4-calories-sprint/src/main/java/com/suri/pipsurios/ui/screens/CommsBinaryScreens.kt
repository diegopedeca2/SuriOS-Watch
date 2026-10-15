package com.suri.pipsurios.ui.screens

import com.suri.pipsurios.PipSuriOsVersion
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

@Composable
fun BinaryModeSelectionScreen(
    onTextToBinarySelected: () -> Unit,
    onBinaryToTextSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            "COMMS // BINARY TERMINAL",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        Column(
            Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            BinaryMenuOption("> TEXT > BINARY", onTextToBinarySelected)
            BinaryMenuOption("> BINARY > TEXT", onBinaryToTextSelected)
        }
        BinaryTerminalFooter(onBack)
    }
}

@Composable
fun BinaryToTextInputScreen(
    input: String,
    onInputChanged: (String) -> Unit,
    onConvert: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            "COMMS // BINARY TERMINAL",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(24.dp)
        )
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("BINARY > TEXT", color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Text("INPUT // 8-BIT BYTES", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text(if (input.isEmpty()) "_" else input, color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                BinaryButton("0") { onInputChanged(input + "0") }
                BinaryButton("1") { onInputChanged(input + "1") }
                BinaryButton("SPACE") { onInputChanged(input + " ") }
                BinaryButton("CONVERT", enabled = input.isNotEmpty(), onClick = onConvert)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                BinaryButton("DELETE", enabled = input.isNotEmpty()) {
                    onInputChanged(input.dropLast(1))
                }
                BinaryButton("CLEAR", enabled = input.isNotEmpty()) {
                    onInputChanged("")
                }
            }
        }
        BinaryTerminalFooter(onBack)
    }
}

@Composable
fun BinaryToTextOutputScreen(output: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            "COMMS // BINARY TERMINAL",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(24.dp)
        )
        Column(
            Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text("OUTPUT", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text(output.ifEmpty { "_" }, color = PipGreen, fontSize = 26.sp, fontFamily = FontFamily.Monospace)
        }
        BinaryTerminalFooter(onBack)
    }
}

@Composable
private fun BinaryMenuOption(label: String, onClick: () -> Unit) {
    Text(
        label,
        color = PipGreen,
        fontSize = 24.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.clickable(onClick = onClick).padding(8.dp)
    )
}

@Composable
private fun BinaryButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Text(
        "[ $label ]",
        color = if (enabled) PipGreen else PipGreenDim,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .border(1.dp, if (enabled) PipGreen else PipGreenDim)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    )
}

@Composable
private fun BinaryTerminalFooter(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text(
            "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding().padding(24.dp).clickable(onClick = onBack)
        )
        Text(
            PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}
