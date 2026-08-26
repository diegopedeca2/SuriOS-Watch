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
import com.suri.pipsurios.ui.skin.SkinCatalog
import com.suri.pipsurios.ui.skin.SkinId

@Composable
fun SkinSelectionScreen(onSkinSelected: (SkinId) -> Unit) {
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
            Text(
                text = "SELECT SKIN:",
                color = PipGreenDim,
                fontSize = 18.sp,
                fontFamily = FontFamily.Monospace
            )

            SkinCatalog.all.forEach { skin ->
                Text(
                    text = "> ${skin.displayName}",
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable { onSkinSelected(skin) }
                )
            }
        }
    }
}

@Composable
fun PendingSkinScreen(skin: SkinId, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize().background(PipBlack), contentAlignment = Alignment.Center) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(skin.displayName, color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Text("UNDER CONSTRUCTION", color = PipGreenDim, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text("< BACK", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onBack).padding(12.dp))
        }
    }
}
