package com.suri.pipsurios.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PIPSuriOSColorScheme = darkColorScheme(
    primary = PipGreen,
    secondary = PipGreenDim,
    tertiary = PipAmber,

    background = PipBlack,
    surface = PipBlack,

    onPrimary = PipBlack,
    onSecondary = PipBlack,
    onTertiary = PipBlack,

    onBackground = PipGreen,
    onSurface = PipGreen,

    error = PipRed,
    onError = PipBlack
)

@Composable
fun PIPSuriOSTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PIPSuriOSColorScheme,
        typography = Typography,
        content = content
    )
}