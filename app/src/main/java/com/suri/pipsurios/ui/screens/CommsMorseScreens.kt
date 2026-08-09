package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun CommsModeSelectionScreen(
    onFrequenciesSelected: () -> Unit,
    onMorseSelected: () -> Unit,
    onBack: () -> Unit
) = TerminalSelectionScreen(
    title = "COMMS SELECT MODE",
    options = listOf("> FREQUENCIES" to onFrequenciesSelected, "> MORSE" to onMorseSelected),
    onBack = onBack
)

@Composable
fun MorseModeSelectionScreen(
    onTextToMorseSelected: () -> Unit,
    onMorseToTextSelected: () -> Unit,
    onBack: () -> Unit
) = TerminalSelectionScreen(
    title = "COMMS // MORSE TERMINAL",
    options = listOf("> TEXT > MORSE" to onTextToMorseSelected, "> MORSE > TEXT" to onMorseToTextSelected),
    onBack = onBack
)

@Composable
private fun TerminalSelectionScreen(
    title: String,
    options: List<Pair<String, () -> Unit>>,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(title, color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp))
        Column(Modifier.align(Alignment.Center), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            options.forEach { (label, action) ->
                Text(label, color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = action).padding(8.dp))
            }
        }
        TerminalFooter(onBack)
    }
}

@Composable
fun MorseToTextInputScreen(
    input: String,
    onInputChanged: (String) -> Unit,
    onConvert: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text("COMMS // MORSE TERMINAL", color = PipGreen, fontSize = 30.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.padding(24.dp))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Text("MORSE > TEXT", color = PipGreen, fontSize = 24.sp,
                fontFamily = FontFamily.Monospace)
            Text("INPUT", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text(if (input.isEmpty()) "_" else input, color = PipGreen, fontSize = 24.sp,
                fontFamily = FontFamily.Monospace)
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MorseButton(".") { onInputChanged(input + ".") }
                MorseButton("-") { onInputChanged(input + "-") }
                MorseButton("_") { onInputChanged(input + "_") }
                MorseButton("CONVERT", enabled = input.isNotEmpty(), onClick = onConvert)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                MorseButton("DELETE", enabled = input.isNotEmpty()) {
                    onInputChanged(input.dropLast(1))
                }
                MorseButton("CLEAR", enabled = input.isNotEmpty()) {
                    onInputChanged("")
                }
            }
        }
        TerminalFooter(onBack)
    }
}

@Composable
fun MorseToTextOutputScreen(output: String, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text("COMMS // MORSE TERMINAL", color = PipGreen, fontSize = 30.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.padding(24.dp))
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)) {
            Text("OUTPUT", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text(output, color = PipGreen, fontSize = 26.sp, fontFamily = FontFamily.Monospace)
        }
        TerminalFooter(onBack)
    }
}

@Composable
private fun MorseButton(label: String, enabled: Boolean = true, onClick: () -> Unit) {
    Text("[ $label ]", color = if (enabled) PipGreen else PipGreenDim, fontSize = 20.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.border(1.dp, if (enabled) PipGreen else PipGreenDim)
            .clickable(enabled = enabled, onClick = onClick).padding(horizontal = 14.dp, vertical = 10.dp))
}

@Composable
fun TerminalFooter(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Text("< BACK", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp))
        Text("PIP-SuriOS v1.4", color = PipGreenDim, fontSize = 18.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp))
    }
}
