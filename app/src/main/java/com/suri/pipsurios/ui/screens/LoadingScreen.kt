package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipGreenDim
import kotlinx.coroutines.delay

private val homeModules = listOf(
    "SET-UP",
    "CURRENT GEAR",
    "INVENTORY",
    "STATUS",
    "DATA",
    "TOOLS"
)

@Composable
fun LoadingScreen(
    operatorId: String,
    onFinished: () -> Unit,
    onOperatorMissing: () -> Unit
) {
    var visibleModuleCount by remember { mutableIntStateOf(0) }
    var readyModuleCount by remember { mutableIntStateOf(0) }
    var showSystemMessage by remember { mutableStateOf(false) }

    val normalizedOperatorId = operatorId.trim()
    val operatorConfigured = normalizedOperatorId.isNotEmpty()
    val loginText = if (operatorConfigured) {
        "LOG-IN CONFIRMED > ID:$normalizedOperatorId"
    } else {
        "PLEASE SET YOUR USER"
    }
    val systemText = if (operatorConfigured) {
        "SYSTEM BOOTING UP // WELCOME, $normalizedOperatorId"
    } else {
        "PLEASE SET YOUR USER"
    }

    LaunchedEffect(Unit) {
        // Keep the menu timing: modules start at 3.25 seconds, and the
        // transition happens 1.5 seconds after the final system message.
        delay(3_250)
        homeModules.forEachIndexed { index, _ ->
            visibleModuleCount = index + 1
            delay(750)
            readyModuleCount = index + 1
            delay(250)
        }
        showSystemMessage = true
        delay(1_500)
        if (operatorConfigured) {
            onFinished()
        } else {
            onOperatorMissing()
        }
    }

    TerminalScreen {
        TerminalPanel(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.90f)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                LoadingLine(id = "login", text = loginText)
                homeModules.forEachIndexed { index, module ->
                    if (visibleModuleCount > index) {
                        val readySuffix = if (readyModuleCount > index) " READY" else ""
                        LoadingLine(
                            id = "module-$module",
                            text = "> $module.....$readySuffix"
                        )
                    }
                }
                if (showSystemMessage) {
                    LoadingLine(id = "system", text = systemText)
                }
            }
        }
    }
}

@Composable
private fun LoadingLine(
    id: String,
    text: String,
    modifier: Modifier = Modifier
) {
    var visibleCharacterCount by remember(id) { mutableIntStateOf(0) }

    LaunchedEffect(id, text) {
        val startIndex = visibleCharacterCount.coerceAtMost(text.length)
        if (visibleCharacterCount > text.length) {
            visibleCharacterCount = text.length
        }
        for (index in startIndex until text.length) {
            visibleCharacterCount = index + 1
            delay(TYPEWRITER_DELAY_MILLIS)
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text.take(visibleCharacterCount),
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )
        if (visibleCharacterCount < text.length) {
            Box(
                modifier = Modifier
                    .width(ACTIVE_CELL_WIDTH)
                    .height(ACTIVE_CELL_HEIGHT)
                    .background(PipGreenDim)
            )
        }
    }
}

private val ACTIVE_CELL_WIDTH = 10.dp
private val ACTIVE_CELL_HEIGHT = 22.dp
private const val TYPEWRITER_DELAY_MILLIS = 18L
