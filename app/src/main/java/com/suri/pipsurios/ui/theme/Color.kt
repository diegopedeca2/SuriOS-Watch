package com.suri.pipsurios.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.suri.pipsurios.ui.skin.SkinId
import com.suri.pipsurios.ui.skin.SkinSession

/**
 * Visual tokens shared by every screen. The values are process-scoped so a
 * selected skin also reaches screens hosted by separate activities.
 */
data class SkinPalette(
    val background: Color,
    val primary: Color,
    val bright: Color,
    val secondary: Color,
    val blue: Color,
    val gray: Color,
    val amber: Color,
    val red: Color,
    val neutral: Color,
    val neutralDim: Color,
    val panel: Color,
    val mapBackground: Color,
    val actionBackground: Color
)

object SkinPalettes {
    val brotherhoodOfSteel = SkinPalette(
        background = Color(0xFF000000),
        primary = Color(0xFF66FF66),
        bright = Color(0xFF66FF99),
        secondary = Color(0xFF3FAF5A),
        blue = Color(0xFF33AAFF),
        gray = Color(0xFF5A5A5A),
        amber = Color(0xFFFFC857),
        red = Color(0xFFFF4D4D),
        neutral = Color.White,
        neutralDim = Color.LightGray,
        panel = Color(0xFF06130C),
        mapBackground = Color(0xFF050805),
        actionBackground = Color(0xFF303030)
    )

    /**
     * NECRON: blackstone background, ghost-teal energy, aged bronze control
     * accents and the existing semantic amber/red warning channels.
     */
    val necron = SkinPalette(
        background = Color(0xFF000000),
        primary = Color(0xFF9DFFE9),
        bright = Color(0xFFD4FFF6),
        secondary = Color(0xFF48BFAF),
        blue = Color(0xFF63D8F2),
        gray = Color(0xFF657A76),
        amber = Color(0xFFE7B86A),
        red = Color(0xFFFF5F62),
        neutral = Color(0xFFDCEBE7),
        neutralDim = Color(0xFFA8BCB7),
        panel = Color(0xFF071211),
        mapBackground = Color(0xFF030A08),
        actionBackground = Color(0xFF27332F)
    )

    fun forSkin(skin: SkinId): SkinPalette = when (skin) {
        SkinId.NECRON -> necron
        else -> brotherhoodOfSteel
    }
}

private val activePalette: SkinPalette
    get() = SkinPalettes.forSkin(SkinSession.activeSkin)

// Existing names remain source-compatible while resolving against the active skin.
val PipBlack: Color get() = activePalette.background
val PipGreen: Color get() = activePalette.primary
val PipGreenBright: Color get() = activePalette.bright
val PipGreenDim: Color get() = activePalette.secondary
val PipBlue: Color get() = activePalette.blue
val PipGray: Color get() = activePalette.gray
val PipAmber: Color get() = activePalette.amber
val PipRed: Color get() = activePalette.red
val PipNeutral: Color get() = activePalette.neutral
val PipNeutralDim: Color get() = activePalette.neutralDim
val PipPanel: Color get() = activePalette.panel
val PipMapBackground: Color get() = activePalette.mapBackground
val PipActionBackground: Color get() = activePalette.actionBackground
