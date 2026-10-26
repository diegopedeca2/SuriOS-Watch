package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.PipSuriOsVersion
import com.suri.pipsurios.runic.RunicCodec
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipNeutral

@Composable
fun RunicTranslatorScreen(
    input: String,
    output: String,
    direction: RunicCodec.Direction,
    onInputChanged: (String) -> Unit,
    onDirectionChanged: (RunicCodec.Direction) -> Unit,
    onTranslate: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            "COMMS // RUNE\nTRANSLATOR",
            color = PipGreen,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.padding(24.dp)
        )
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(start = 20.dp, end = 20.dp, top = 96.dp, bottom = 92.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "ELDER FUTHARK // TRANSLITERATION",
                color = PipGreenDim,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
            Text(
                "ELIGE LA DIRECCIÓN",
                color = PipGreen,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            )
            RunicButton(
                label = if (direction == RunicCodec.Direction.LATIN_TO_RUNES) {
                    "> LATIN > RUNES [ON]"
                } else {
                    "> LATIN > RUNES"
                },
                enabled = direction != RunicCodec.Direction.LATIN_TO_RUNES,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDirectionChanged(RunicCodec.Direction.LATIN_TO_RUNES) }
            )
            RunicButton(
                label = if (direction == RunicCodec.Direction.RUNES_TO_LATIN) {
                    "> RUNES > LATIN [ON]"
                } else {
                    "> RUNES > LATIN"
                },
                enabled = direction != RunicCodec.Direction.RUNES_TO_LATIN,
                modifier = Modifier.fillMaxWidth(),
                onClick = { onDirectionChanged(RunicCodec.Direction.RUNES_TO_LATIN) }
            )
            Text(
                if (direction == RunicCodec.Direction.LATIN_TO_RUNES) {
                    "INPUT // TEXTO LATINO"
                } else {
                    "INPUT // RUNAS UNICODE"
                },
                color = PipGreen,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim)
                    .heightIn(min = 72.dp)
                    .padding(10.dp)
            ) {
                if (input.isEmpty()) {
                    Text(
                        if (direction == RunicCodec.Direction.LATIN_TO_RUNES) {
                            "ESCRIBE O PEGA EL TEXTO"
                        } else {
                            "ESCRIBE O PEGA LAS RUNAS"
                        },
                        color = PipGreenDim,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                BasicTextField(
                    value = input,
                    onValueChange = onInputChanged,
                    textStyle = TextStyle(
                        color = PipNeutral,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            RunicButton(
                "[ TRANSLATE ]",
                enabled = input.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                onClick = onTranslate
            )
            RunicButton(
                "[ CLEAR ]",
                enabled = input.isNotEmpty() || output.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                onClick = onClear
            )
            Text("OUTPUT", color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, PipGreenDim.copy(alpha = 0.7f))
                    .heightIn(min = 72.dp)
                    .padding(10.dp)
            ) {
                Text(
                    output.ifEmpty { "_" },
                    color = PipGreen,
                    fontSize = 22.sp,
                    fontFamily = FontFamily.Monospace,
                    softWrap = true
                )
            }
        }
        RunicTranslatorFooter(onBack)
    }
}

@Composable
private fun RunicButton(
    label: String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Text(
        label,
        color = if (enabled) PipGreen else PipGreenDim,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
            .border(1.dp, if (enabled) PipGreen else PipGreenDim)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
private fun RunicTranslatorFooter(onBack: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                "< BACK",
                color = PipGreenDim,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onBack)
            )
            Text(
                PipSuriOsVersion,
                color = PipGreenDim,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
