package com.suri.pipsurios.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DelegatableNode

private fun colorSchemeFor(palette: ColorPalette) = darkColorScheme(
    primary = palette.primary,
    secondary = palette.secondary,
    tertiary = palette.amber,

    background = palette.background,
    surface = palette.background,

    onPrimary = palette.background,
    onSecondary = palette.background,
    onTertiary = palette.background,

    onBackground = palette.primary,
    onSurface = palette.primary,

    error = palette.red,
    onError = palette.background
)

@Composable
fun PIPSuriOSTheme(
    content: @Composable () -> Unit
) {
    val palette = ColorPalettes.original
    MaterialTheme(
        colorScheme = colorSchemeFor(palette),
        typography = Typography,
        content = {
            // The PIP interface is intentionally quiet: clicking a control must not
            // draw a transient overlay/ripple that can appear as a flash in darkness.
            CompositionLocalProvider(LocalIndication provides NoVisualIndication, content)
        }
    )
}

/** Indication implementation that keeps click semantics but draws no press feedback. */
private object NoVisualIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        NoVisualIndicationNode()

    override fun equals(other: Any?): Boolean = other === this

    override fun hashCode(): Int = 0
}

private class NoVisualIndicationNode : Modifier.Node()
