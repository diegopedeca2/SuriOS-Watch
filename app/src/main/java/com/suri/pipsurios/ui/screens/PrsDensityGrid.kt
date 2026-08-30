package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.prs.DensityCloud
import com.suri.pipsurios.prs.PrsContactSnapshot
import com.suri.pipsurios.prs.PrsGridProbe
import com.suri.pipsurios.prs.PrsObservationSource
import com.suri.pipsurios.R
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipBlue
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipPanel
import com.suri.pipsurios.ui.skin.SkinSession
import androidx.compose.ui.graphics.ColorFilter
import kotlin.math.min

/**
 * GRID v2.0 visual language retained as a display surface. Contacts are
 * rendered as centred diffuse annuli: radial signal bands are shown, while
 * azimuth remains deliberately uniform because BLE does not provide bearing.
 */
@Composable
fun PrsDensityGrid(
    contacts: List<PrsContactSnapshot>,
    selectedContactId: String?,
    modifier: Modifier = Modifier,
    selectedDisplayName: String? = null,
    probeNodes: List<PrsGridProbe> = emptyList(),
    surfaceColor: Color = PipPanel,
    showEmblem: Boolean = true,
    showTargetLabel: Boolean = true
) {
    Box(modifier = modifier.border(1.dp, PipGreenDim).background(surfaceColor)) {
        if (showEmblem) {
            Image(
                painter = painterResource(SkinSession.emblemResource),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                alpha = 0.10f,
                colorFilter = ColorFilter.tint(PipGreenDim),
                modifier = Modifier.fillMaxSize().padding(48.dp)
            )
        }
        Canvas(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            val gridColor = PipGreenDim.copy(alpha = 0.32f)
            val columns = 6
            val rows = 4
            repeat(columns + 1) { index ->
                val x = size.width * index / columns
                drawLine(gridColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = 1f)
            }
            repeat(rows + 1) { index ->
                val y = size.height * index / rows
                drawLine(gridColor, Offset(0f, y), Offset(size.width, y), strokeWidth = 1f)
            }

            var scanlineY = 0f
            while (scanlineY < size.height) {
                drawLine(
                    PipGreenDim.copy(alpha = 0.08f),
                    Offset(0f, scanlineY),
                    Offset(size.width, scanlineY),
                    strokeWidth = 1f
                )
                scanlineY += 6.dp.toPx()
            }

            val bracket = 18.dp.toPx()
            val corners = listOf(
                Offset(0f, 0f) to Pair(1f, 1f),
                Offset(size.width, 0f) to Pair(-1f, 1f),
                Offset(0f, size.height) to Pair(1f, -1f),
                Offset(size.width, size.height) to Pair(-1f, -1f)
            )
            corners.forEach { (corner, direction) ->
                drawLine(PipGreen, corner, corner + Offset(bracket * direction.first, 0f), strokeWidth = 2f)
                drawLine(PipGreen, corner, corner + Offset(0f, bracket * direction.second), strokeWidth = 2f)
            }

            val centre = Offset(size.width / 2f, size.height / 2f)
            val maxRadius = min(size.width, size.height) * 0.46f
            listOf(0.25f, 0.50f, 0.75f, 1.0f).forEach { fraction ->
                drawCircle(
                    PipGreenDim.copy(alpha = 0.24f),
                    radius = maxRadius * fraction,
                    center = centre,
                    style = Stroke(1.dp.toPx())
                )
            }

            // The main surface belongs to the A56. Contacts heard by the
            // Watch 2 are rendered in the probe-local subgrid below, so they
            // are not mistaken for measurements made at the phone position.
            val phoneContacts = contacts.filter { it.source == PrsObservationSource.A56 }
            val probeContacts = contacts.filter { it.source == PrsObservationSource.PROBE_WATCH_2 }

            phoneContacts.filterNot { it.contactId == selectedContactId }.forEach { contact ->
                drawDensityCloud(
                    centre = centre,
                    maxRadius = maxRadius,
                    cloud = contact.inference.densityCloud,
                    selected = false
                )
            }
            phoneContacts.firstOrNull { it.contactId == selectedContactId }?.let { contact ->
                drawDensityCloud(
                    centre = centre,
                    maxRadius = maxRadius,
                    cloud = contact.inference.densityCloud,
                    selected = true
                )
            }
            probeNodes.forEach { probe ->
                drawProbeSubgrid(
                    probe = probe,
                    contacts = probeContacts,
                    selectedContactId = selectedContactId
                )
            }
            drawCircle(PipGreen, radius = 6.dp.toPx(), center = centre)
        }
        if (showTargetLabel && selectedDisplayName != null) {
            Text(
                text = "TRACK TARGET: $selectedDisplayName",
                color = PipAmber,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.TopStart).padding(8.dp)
            )
        }
        if (probeNodes.isNotEmpty()) {
            Text(
                text = "WATCH 2 // PROBE SUBGRID",
                color = PipBlue,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawProbeSubgrid(
    probe: PrsGridProbe,
    contacts: List<PrsContactSnapshot>,
    selectedContactId: String?
) {
    val width = min(size.width, size.height) * 0.24f
    val centre = Offset(size.width * probe.xFraction, size.height * probe.yFraction)
    val left = (centre.x - width / 2f).coerceIn(8.dp.toPx(), size.width - width - 8.dp.toPx())
    val top = (centre.y - width / 2f).coerceIn(8.dp.toPx(), size.height - width - 8.dp.toPx())
    val colour = if (probe.state == "ACTIVE") PipBlue else PipAmber
    // The probe-local grid remains visible through its internal guides and
    // density clouds; the former blue bounding frame is intentionally omitted
    // so it does not dominate the main GRID.
    repeat(3) { index ->
        val offset = width * (index + 1) / 4f
        drawLine(colour.copy(alpha = 0.45f), Offset(left + offset, top), Offset(left + offset, top + width), strokeWidth = 1f)
        drawLine(colour.copy(alpha = 0.45f), Offset(left, top + offset), Offset(left + width, top + offset), strokeWidth = 1f)
    }

    // A probe contact has no measured bearing. Its signal cloud is therefore
    // centred on the probe node and remains azimuth-free inside this local
    // surface. This is deliberately a density cue, not a target coordinate.
    val localRadius = width * 0.42f
    contacts.filterNot { it.contactId == selectedContactId }.forEach { contact ->
        drawDensityCloud(
            centre = centre,
            maxRadius = localRadius,
            cloud = contact.inference.densityCloud,
            selected = false
        )
    }
    contacts.firstOrNull { it.contactId == selectedContactId }?.let { contact ->
        drawDensityCloud(
            centre = centre,
            maxRadius = localRadius,
            cloud = contact.inference.densityCloud,
            selected = true
        )
    }

    // Explicit node marker: green is the A56 position; blue/amber is the
    // Watch 2 position/status. The marker is kept visible above the nested
    // grid so the probe is a real node in the main GRID, not just a frame.
    drawCircle(colour.copy(alpha = 0.22f), radius = 10.dp.toPx(), center = centre)
    drawCircle(colour, radius = 4.dp.toPx(), center = centre)
    drawLine(colour, Offset(centre.x - 9.dp.toPx(), centre.y), Offset(centre.x + 9.dp.toPx(), centre.y), strokeWidth = 1.5.dp.toPx())
    drawLine(colour, Offset(centre.x, centre.y - 9.dp.toPx()), Offset(centre.x, centre.y + 9.dp.toPx()), strokeWidth = 1.5.dp.toPx())
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDensityCloud(
    centre: Offset,
    maxRadius: Float,
    cloud: DensityCloud,
    selected: Boolean
) {
    val colour = if (selected) PipAmber else PipGreen
    val radius = maxRadius * cloud.radialCenterFraction
    val spread = maxRadius * cloud.radialSpreadFraction
    val confidence = cloud.confidence.coerceIn(0.12f, 0.72f)

    val emphasis = if (selected) 1.65f else 1f
    drawCircle(colour.copy(alpha = (0.025f + confidence * 0.06f) * emphasis), radius = radius + spread, center = centre)
    drawCircle(colour.copy(alpha = (0.09f + confidence * 0.12f) * emphasis), radius = radius + spread * 0.62f, center = centre, style = Stroke(if (selected) 2.dp.toPx() else 1.dp.toPx()))
    drawCircle(colour.copy(alpha = (0.17f + confidence * 0.16f) * emphasis), radius = radius, center = centre, style = Stroke(if (selected) 3.dp.toPx() else 1.dp.toPx()))
    drawCircle(colour.copy(alpha = 0.11f * emphasis), radius = (radius - spread * 0.62f).coerceAtLeast(4.dp.toPx()), center = centre, style = Stroke(if (selected) 2.dp.toPx() else 1.dp.toPx()))
}
