package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

private val pmrChannels = listOf(
    "Canal 1" to "446.00625 MHz",
    "Canal 2" to "446.01875 MHz",
    "Canal 3" to "446.03125 MHz",
    "Canal 4" to "446.04375 MHz",
    "Canal 5" to "446.05625 MHz",
    "Canal 6" to "446.06875 MHz",
    "Canal 7" to "446.08125 MHz",
    "Canal 8" to "446.09375 MHz",
    "Canal 9" to "446.10625 MHz",
    "Canal 10" to "446.11875 MHz",
    "Canal 11" to "446.13125 MHz",
    "Canal 12" to "446.14375 MHz",
    "Canal 13" to "446.15625 MHz",
    "Canal 14" to "446.16875 MHz",
    "Canal 15" to "446.18125 MHz",
    "Canal 16" to "446.19375 MHz"
)

@Composable
fun CommsScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
    ) {
        Text(
            text = "COMMS",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width(282.dp)
                .border(1.dp, PipGreen)
        ) {
            PmrRow("PMR", "FREQUENCIES")
            LazyColumn(modifier = Modifier.height(232.dp)) {
                items(pmrChannels) { (channel, frequency) ->
                    PmrRow(channel, frequency)
                }
            }
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
            text = "PIP-SuriOS v1.9",
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
private fun PmrRow(firstColumn: String, secondColumn: String) {
    Row(modifier = Modifier.height(30.dp)) {
        PmrCell(firstColumn, Modifier.width(112.dp))
        PmrCell(secondColumn, Modifier.width(170.dp))
    }
}

@Composable
private fun PmrCell(text: String, modifier: Modifier) {
    Box(
        modifier = modifier.border(0.5.dp, PipGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = PipGreen,
            fontSize = 14.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
