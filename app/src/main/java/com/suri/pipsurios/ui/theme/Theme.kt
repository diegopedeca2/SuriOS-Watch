package com.suri.pipsurios.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.foundation.interaction.PressInteraction
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

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
    val context = LocalContext.current.applicationContext
    val clickSound = remember(context) { PipClickSound(context) }
    val indication = remember(clickSound) { ClickSoundIndication(clickSound) }
    DisposableEffect(clickSound) {
        onDispose { clickSound.release() }
    }
    MaterialTheme(
        colorScheme = colorSchemeFor(palette),
        typography = Typography,
        content = {
            // The interface stays free of visual ripples; the short audio cue is
            // shared by every Compose clickable in every application screen.
            CompositionLocalProvider(LocalIndication provides indication, content)
        }
    )
}

/** Indication implementation that keeps click semantics without drawing a ripple. */
private class ClickSoundIndication(
    private val sound: PipClickSound
) : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode =
        ClickSoundIndicationNode(interactionSource, sound)

    override fun equals(other: Any?): Boolean =
        other is ClickSoundIndication && other.sound === sound

    override fun hashCode(): Int = System.identityHashCode(sound)
}

private class ClickSoundIndicationNode(
    private val interactionSource: InteractionSource,
    private val sound: PipClickSound
) : Modifier.Node() {
    private var interactionJob: Job? = null

    override fun onAttach() {
        interactionJob = coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Press) sound.play()
            }
        }
    }

    override fun onDetach() {
        interactionJob?.cancel()
        interactionJob = null
    }
}
