package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipGreenDim

/** P.R.S.-specific back control: the glyph is intentionally the only label. */
@Composable
fun PrsBackButton(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .border(1.dp, PipGreenDim.copy(alpha = 0.85f))
            .clickable(onClick = onBack),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "<",
            color = PipGreenDim,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
