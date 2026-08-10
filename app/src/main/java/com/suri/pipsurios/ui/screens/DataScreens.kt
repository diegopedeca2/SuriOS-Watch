package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.data.OperationDraft
import com.suri.pipsurios.data.OperationInputValidator
import com.suri.pipsurios.data.OperationLog
import com.suri.pipsurios.data.OperationLogEntry
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import kotlinx.coroutines.delay

@Composable
fun DataLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    DataCenterMessage("LOADING...")
}

@Composable
fun DataSavedScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    DataCenterMessage("DATA SAVED")
}

@Composable
private fun DataCenterMessage(message: String) {
    Box(Modifier.fillMaxSize().background(PipBlack), contentAlignment = Alignment.Center) {
        Text(message, color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun DataScreen(
    onInputOperation: () -> Unit,
    onLog: () -> Unit,
    onStatistics: () -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "DATA", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DataMenuEntry("> INPUT OPERATION", onInputOperation)
            DataMenuEntry("> LOG", onLog)
            DataMenuEntry("> STATISTICS", onStatistics)
        }
    }
}

@Composable
fun DataPlaceholderScreen(title: String, onBack: () -> Unit) {
    DataFrame(title = title, onBack = onBack) {
        Text(
            text = "UNDER CONSTRUCTION",
            color = PipRed,
            fontSize = 24.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun DataLogScreen(
    entries: List<OperationLogEntry>,
    unreadableFileCount: Int,
    loading: Boolean,
    onEntrySelected: (OperationLogEntry) -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "DATA - LOG", onBack = onBack) {
        when {
            loading -> Text(
                text = "LOADING...",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
            entries.isEmpty() -> Text(
                text = "NO DATA",
                color = PipRed,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
            else -> Column(
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(start = 42.dp, end = 42.dp, top = 82.dp, bottom = 72.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                entries.forEach { entry ->
                    Text(
                        text = "> ${entry.log.date} - ${entry.log.location}",
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.clickable { onEntrySelected(entry) }
                            .padding(vertical = 5.dp)
                    )
                }
            }
        }
        if (!loading && unreadableFileCount > 0) {
            Text(
                text = "$unreadableFileCount LOG FILE(S) UNREADABLE",
                color = PipRed,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 28.dp)
            )
        }
    }
}

@Composable
fun DataLogDetailScreen(log: OperationLog, onBack: () -> Unit) {
    var editNotice by remember { mutableStateOf(false) }
    LaunchedEffect(editNotice) {
        if (editNotice) {
            delay(1_500)
            editNotice = false
        }
    }
    DataFrame(title = "DATA - LOG DETAIL", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 42.dp, end = 42.dp, top = 76.dp, bottom = 82.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            DataValue("DATE", log.date)
            DataValue("LOCATION", log.location)
            Text("LOADOUT", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            DataValue("PRIMARY WEAPON", log.loadout.primaryWeapon)
            DataValue("SECONDARY WEAPON", log.loadout.secondaryWeapon)
            DataValue(
                "ACCESORIES",
                log.loadout.accesories.takeIf { it.isNotEmpty() }?.joinToString(" + ")
            )
            DataValue("HEADGEAR", log.loadout.headgear)
            DataValue("FRONT PANEL", log.loadout.frontPanel)
            Text("CONSUMABLES", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            DataValue("PRIMARY MAG", OperationInputValidator.formatDecimal(log.consumables.primaryMag))
            DataValue("SECONDARY MAG", OperationInputValidator.formatDecimal(log.consumables.secondaryMag))
            DataValue("40mm GRENADES", OperationInputValidator.formatDecimal(log.consumables.grenades40mm))
            DataValue("9mm GRENADES", OperationInputValidator.formatDecimal(log.consumables.grenades9mm))
            DataValue("CO2 GRENADES", OperationInputValidator.formatDecimal(log.consumables.grenadesCo2))
            DataValue("PRIMARY HPA", OperationInputValidator.formatDecimal(log.consumables.primaryHpa))
            DataValue("SECONDARY HPA", OperationInputValidator.formatDecimal(log.consumables.secondaryHpa))
        }
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            DataButton("EDIT", onClick = { editNotice = true })
            if (editNotice) {
                Text(
                    text = "UNDER CONSTRUCTION",
                    color = PipRed,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun OperationLoadoutScreen(
    activeLoadout: LoadoutConfiguration,
    loadoutConfirmed: Boolean,
    onConfirmLoadout: () -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "INPUT OPERATION - LOADOUT", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.Center).heightIn(max = 260.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DataValue("PRIMARY WEAPON", activeLoadout.primaryWeapon?.displayName)
            DataValue("SECONDARY WEAPON", activeLoadout.secondaryWeapon?.displayName)
            DataValue(
                "ACCESORIES",
                activeLoadout.accesories.takeIf { it.isNotEmpty() }
                    ?.joinToString(" + ") { it.displayName }
            )
            DataValue("HEADGEAR", activeLoadout.headgearProfile)
            DataValue("FRONT PANEL", activeLoadout.frontPanelRole)
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            DataButton(
                label = if (loadoutConfirmed) "LOADOUT CONFIRMED" else "CONFIRM LOADOUT",
                onClick = onConfirmLoadout
            )
            DataButton("NEXT", onNext, enabled = loadoutConfirmed)
        }
    }
}

@Composable
fun OperationConfirmScreen(
    draft: OperationDraft,
    saveError: String?,
    saving: Boolean,
    onEdit: () -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "INPUT OPERATION - CONFIRM DATA", onBack = onBack) {
        val loadout = draft.loadout
        val consumables = draft.consumables
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 76.dp, bottom = 84.dp)
                .heightIn(max = 300.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(9.dp)
        ) {
            DataValue("DATE", draft.date)
            DataValue("LOCATION", draft.location)
            Text("LOADOUT", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            DataValue("PRIMARY WEAPON", loadout?.primaryWeapon)
            DataValue("SECONDARY WEAPON", loadout?.secondaryWeapon)
            DataValue("ACCESORIES", loadout?.accesories?.takeIf { it.isNotEmpty() }?.joinToString(" + "))
            DataValue("HEADGEAR", loadout?.headgear)
            DataValue("FRONT PANEL", loadout?.frontPanel)
            Text("CONSUMABLES", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
            DataValue("PRIMARY MAG", consumables?.primaryMag?.let(OperationInputValidator::formatDecimal))
            DataValue("SECONDARY MAG", consumables?.secondaryMag?.let(OperationInputValidator::formatDecimal))
            DataValue("40mm GRENADES", consumables?.grenades40mm?.let(OperationInputValidator::formatDecimal))
            DataValue("9mm GRENADES", consumables?.grenades9mm?.let(OperationInputValidator::formatDecimal))
            DataValue("CO2 GRENADES", consumables?.grenadesCo2?.let(OperationInputValidator::formatDecimal))
            DataValue("PRIMARY HPA", consumables?.primaryHpa?.let(OperationInputValidator::formatDecimal))
            DataValue("SECONDARY HPA", consumables?.secondaryHpa?.let(OperationInputValidator::formatDecimal))
        }

        saveError?.let {
            Text(
                text = it,
                color = PipRed,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 68.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            DataButton("EDIT", onEdit, enabled = !saving)
            DataButton(if (saving) "SAVING..." else "CONFIRM DATA", onConfirm, enabled = !saving)
        }
    }
}

@Composable
private fun DataFrame(
    title: String,
    onBack: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    Box(Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        content()
        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v1.7",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun DataMenuEntry(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        color = PipGreen,
        fontSize = 24.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun DataValue(label: String, value: String?) {
    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
        Text(label, color = PipGreenDim, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = "> ${value?.takeIf { it.isNotBlank() } ?: "NOT CONFIGURED"}",
            color = PipGreen,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
private fun DataButton(label: String, onClick: () -> Unit, enabled: Boolean = true) {
    val color: Color = if (enabled) PipGreen else PipGreenDim
    Text(
        text = label,
        color = color,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.border(1.dp, color).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}
