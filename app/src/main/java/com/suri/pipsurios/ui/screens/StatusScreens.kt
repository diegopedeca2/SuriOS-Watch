package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import com.suri.pipsurios.ui.state.ComplementCatalog
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay

@Composable
fun StatusLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    Box(
        modifier = Modifier.fillMaxSize().background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Text("LOADING...", color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun StatusScreen(
    activeLoadout: LoadoutConfiguration,
    onDontForgetSelected: () -> Unit,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "STATUS",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .heightIn(max = 260.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            StatusEntry("PRIMARY WEAPON", activeLoadout.primaryWeaponDisplayName())
            StatusEntry("SECONDARY WEAPON", activeLoadout.secondaryWeapon?.displayName)
            StatusEntry(
                "ACCESORIES",
                activeLoadout.accesories
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(" + ") { it.displayName }
            )
            StatusEntry("HEADGEAR", activeLoadout.headgearProfile)
            StatusEntry("FRONT PANEL", activeLoadout.frontPanelRole)
            StatusEntry("UNIFORM", activeLoadout.uniform)
        }

        Text(
            text = "DON'T FORGET",
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 64.dp)
                .clickable(onClick = onDontForgetSelected)
        )

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.5",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
fun DontForgetScreen(activeLoadout: LoadoutConfiguration, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "STATUS - DON'T FORGET",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        val reminderLines = ComplementCatalog.reminderLines(activeLoadout)
        if (reminderLines.isNotEmpty()) {
            var checkedItems by remember(reminderLines) {
                mutableStateOf(emptySet<String>())
            }
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 88.dp)
                    .heightIn(max = 300.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                reminderLines.forEach { item ->
                    Text(
                        text = "${if (item in checkedItems) "[X]" else "[ ]"} $item",
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable {
                            checkedItems = if (item in checkedItems) {
                                checkedItems - item
                            } else {
                                checkedItems + item
                            }
                        }
                    )
                }
            }
        } else {
            Text(
                text = "UNDER CONSTRUCTION",
                color = PipRed,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.5",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun StatusEntry(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = "> ${value ?: "NOT CONFIGURED"}",
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
