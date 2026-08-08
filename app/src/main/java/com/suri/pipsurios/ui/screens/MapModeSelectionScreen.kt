package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
fun MapModeSelectionScreen(
    onTerrainSelected: () -> Unit,
    onOperationSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            MapModeText(text = "SELECT MODE:")
            MapModeText(
                text = "> TERRAIN",
                modifier = Modifier.clickable(onClick = onTerrainSelected)
            )
            MapModeText(
                text = "> OPERATION",
                modifier = Modifier.clickable(onClick = onOperationSelected)
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
    }
}

@Composable
private fun MapModeText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = PipGreen,
        fontSize = 24.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier
    )
}
