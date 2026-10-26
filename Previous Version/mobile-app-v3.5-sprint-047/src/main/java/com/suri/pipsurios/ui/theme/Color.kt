package com.suri.pipsurios.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Visual tokens shared by every screen and hosted activity.
 */
data class ColorPalette(
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

object ColorPalettes {
    val original = ColorPalette(
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

}

private val palette: ColorPalette = ColorPalettes.original

val PipBlack: Color get() = palette.background
val PipGreen: Color get() = palette.primary
val PipGreenBright: Color get() = palette.bright
val PipGreenDim: Color get() = palette.secondary
val PipBlue: Color get() = palette.blue
val PipGray: Color get() = palette.gray
val PipAmber: Color get() = palette.amber
val PipRed: Color get() = palette.red
val PipNeutral: Color get() = palette.neutral
val PipNeutralDim: Color get() = palette.neutralDim
val PipPanel: Color get() = palette.panel
val PipMapBackground: Color get() = palette.mapBackground
val PipActionBackground: Color get() = palette.actionBackground
