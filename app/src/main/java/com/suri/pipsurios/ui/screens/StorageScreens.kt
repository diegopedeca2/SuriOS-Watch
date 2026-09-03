package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.storage.StorageBalance
import com.suri.pipsurios.storage.StorageItem
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim

@Composable
fun StorageGroupScreen(title: String, items: List<StorageItem>, onItem: (StorageItem) -> Unit, onBack: () -> Unit) {
    InventoryVisualMenuScreen(
        title = title,
        entries = items.map { "> ${it.displayName}" },
        entryActions = items.associate { item -> "> ${item.displayName}" to { onItem(item) } },
        scrollable = true,
        onBack = onBack
    )
}

@Composable
fun StorageItemScreen(
    balance: StorageBalance,
    onPurchase: () -> Unit,
    onUsed: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text("STORAGE - ${balance.item.displayName}", color = PipGreen, fontSize = 26.sp,
            fontFamily = FontFamily.Monospace, modifier = Modifier.align(Alignment.TopStart).padding(24.dp))
        Column(
            modifier = Modifier.align(Alignment.Center).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StorageAction("PURCHASE", balance.purchase, onPurchase)
                StorageAction("USED", balance.used, onUsed)
            }
            StorageValue("CONSUMED", balance.consumed)
            StorageValue("TOTAL", balance.total)
        }
        Text("< BACK", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp))
        Text("PIP-SuriOS v3.0", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp))
    }
}

@Composable
private fun StorageValue(label: String, value: java.math.BigDecimal) {
    Text("$label: ${value.stripTrailingZeros().toPlainString().replace('.', ',')}", color = PipGreen,
        fontSize = 20.sp, fontFamily = FontFamily.Monospace)
}

@Composable
private fun StorageAction(label: String, value: java.math.BigDecimal, action: () -> Unit) {
    Column(
        modifier = Modifier.border(1.dp, PipGreen).clickable(onClick = action)
            .padding(horizontal = 28.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
        Text(value.stripTrailingZeros().toPlainString(), color = PipGreen, fontSize = 22.sp,
            fontFamily = FontFamily.Monospace)
    }
}
