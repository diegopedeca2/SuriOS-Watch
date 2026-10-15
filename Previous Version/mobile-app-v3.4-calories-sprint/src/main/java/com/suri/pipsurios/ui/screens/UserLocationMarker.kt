package com.suri.pipsurios.ui.screens

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipBlue

/** Shared, high-contrast marker for the live A56/GPS position on map canvases. */
internal fun DrawScope.drawUserLocationMarker(center: Offset) {
    // Dark halo keeps the marker readable over both light map tiles and overlays.
    drawCircle(PipBlack.copy(alpha = 0.82f), radius = 17f, center = center)
    drawCircle(PipBlue, radius = 13f, center = center, style = Stroke(width = 3.5f))
    drawLine(PipBlue, center - Offset(22f, 0f), center + Offset(22f, 0f), strokeWidth = 2.5f)
    drawLine(PipBlue, center - Offset(0f, 22f), center + Offset(0f, 22f), strokeWidth = 2.5f)
    drawCircle(PipBlue, radius = 5f, center = center)
}
