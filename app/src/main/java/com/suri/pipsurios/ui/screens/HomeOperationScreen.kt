package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.R
import com.suri.pipsurios.ui.skin.SkinSession

@Composable
fun HomeOperationScreen(
    onBack: () -> Unit,
    onInventorySelected: () -> Unit,
    onDataSelected: () -> Unit,
    onCurrentGearSelected: () -> Unit,
    onSetUpSelected: () -> Unit,
    onStatusSelected: () -> Unit,
    onToolsSelected: () -> Unit,
    onInformationSelected: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
    ) {
        Image(
            painter = painterResource(SkinSession.emblemResource),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(0.82f)
                .alpha(0.26f),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(PipGreenDim)
        )

        Text(
            text = "HOMESCREEN",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )

        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.58f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ModuleColumn(
                entries = listOf("> SET-UP", "> CURRENT GEAR", "> INVENTORY"),
                onInventorySelected = onInventorySelected,
                onCurrentGearSelected = onCurrentGearSelected,
                onSetUpSelected = onSetUpSelected
            )
            ModuleColumn(
                entries = listOf("> STATUS", "> DATA", "> TOOLS", "> INFORMATION"),
                onDataSelected = onDataSelected,
                onStatusSelected = onStatusSelected,
                onToolsSelected = onToolsSelected,
                onInformationSelected = onInformationSelected
            )
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .clickable(onClick = onBack)
                .padding(24.dp)
        )

        Text(
            text = "PIP-SuriOS v2.6",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        )
    }
}

@Composable
private fun ModuleColumn(
    entries: List<String>,
    onInventorySelected: (() -> Unit)? = null,
    onDataSelected: (() -> Unit)? = null,
    onCurrentGearSelected: (() -> Unit)? = null,
    onSetUpSelected: (() -> Unit)? = null,
    onStatusSelected: (() -> Unit)? = null,
    onToolsSelected: (() -> Unit)? = null,
    onInformationSelected: (() -> Unit)? = null
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.Start
    ) {
        entries.forEach { entry ->
            Text(
                text = entry,
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = when {
                    entry == "> INVENTORY" && onInventorySelected != null ->
                        Modifier.clickable(onClick = onInventorySelected)
                    entry == "> DATA" && onDataSelected != null ->
                        Modifier.clickable(onClick = onDataSelected)
                    entry == "> CURRENT GEAR" && onCurrentGearSelected != null ->
                        Modifier.clickable(onClick = onCurrentGearSelected)
                    entry == "> SET-UP" && onSetUpSelected != null ->
                        Modifier.clickable(onClick = onSetUpSelected)
                    entry == "> STATUS" && onStatusSelected != null ->
                        Modifier.clickable(onClick = onStatusSelected)
                    entry == "> TOOLS" && onToolsSelected != null ->
                        Modifier.clickable(onClick = onToolsSelected)
                    entry == "> INFORMATION" && onInformationSelected != null ->
                        Modifier.clickable(onClick = onInformationSelected)
                    else -> Modifier
                }
            )
        }
    }
}
