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
import com.suri.pipsurios.data.OperationEditDraft
import com.suri.pipsurios.data.OperationLoadoutSnapshot
import com.suri.pipsurios.data.PercentageDistribution
import com.suri.pipsurios.data.StatisticsCalculator
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
fun DataDeletedScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    DataCenterMessage("DATA DELETED")
}

@Composable
fun DataModifiedScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    DataCenterMessage("DATA MODIFIED")
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
fun DataStatisticsScreen(
    onPrimaryWeapon: () -> Unit,
    onSecondaryWeapon: () -> Unit,
    onLocation: () -> Unit,
    onHeadgear: () -> Unit,
    onUniform: () -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "DATA - STATISTICS", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.Start
        ) {
            DataMenuEntry("> PRIMARY WEAPON", onPrimaryWeapon)
            DataMenuEntry("> SECONDARY WEAPON", onSecondaryWeapon)
            DataMenuEntry("> LOCATION", onLocation)
            DataMenuEntry("> HEADGEAR", onHeadgear)
            DataMenuEntry("> UNIFORM", onUniform)
        }
    }
}

@Composable
fun PrimaryWeaponStatisticsScreen(
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) = WeaponStatisticsScreen(
    title = "STATISTICS - PRIMARY WEAPON",
    distribution = distribution,
    loading = loading,
    onBack = onBack
)

@Composable
fun SecondaryWeaponStatisticsScreen(
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) = WeaponStatisticsScreen(
    title = "STATISTICS - SECONDARY WEAPON",
    distribution = distribution,
    loading = loading,
    onBack = onBack
)

@Composable
fun LocationStatisticsScreen(
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) = WeaponStatisticsScreen(
    title = "STATISTICS - LOCATION",
    distribution = distribution,
    loading = loading,
    onBack = onBack
)

@Composable
fun HeadgearStatisticsScreen(
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) = WeaponStatisticsScreen(
    title = "STATISTICS - HEADGEAR",
    distribution = distribution,
    loading = loading,
    onBack = onBack
)

@Composable
fun UniformStatisticsScreen(
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) = WeaponStatisticsScreen(
    title = "STATISTICS - UNIFORM",
    distribution = distribution,
    loading = loading,
    onBack = onBack
)

@Composable
private fun WeaponStatisticsScreen(
    title: String,
    distribution: PercentageDistribution<String>?,
    loading: Boolean,
    onBack: () -> Unit
) {
    DataFrame(title = title, onBack = onBack) {
        if (loading) {
            Text(
                text = "LOADING...",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (distribution == null || distribution.validRecordCount == 0) {
            Text(
                text = "NO DATA",
                color = PipRed,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
                    .padding(start = 42.dp, end = 42.dp, top = 82.dp, bottom = 72.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                distribution.entries.forEach { entry ->
                    Text(
                        text = "${entry.option} - ${StatisticsCalculator.formatPercentage(entry.percentage)}",
                        color = PipGreen,
                        fontSize = 24.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
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
fun DataLogDetailScreen(
    log: OperationLog,
    deleting: Boolean,
    deleteError: String?,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var deleteConfirmationVisible by remember { mutableStateOf(false) }
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
            DataValue("UNIFORM", log.loadout.uniform)
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
            if (deleteConfirmationVisible) {
                Text(
                    text = "DELETE LOG?",
                    color = PipRed,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
                DataButton(
                    label = if (deleting) "DELETING..." else "CONFIRM DELETE",
                    onClick = onDelete,
                    enabled = !deleting,
                    color = PipRed
                )
                DataButton(
                    label = "CANCEL",
                    onClick = { deleteConfirmationVisible = false },
                    enabled = !deleting
                )
            } else {
                DataButton("EDIT", onClick = onEdit)
                DataButton(
                    label = "DELETE",
                    onClick = { deleteConfirmationVisible = true },
                    color = PipRed
                )
            }
        }
        deleteError?.let { error ->
            Text(
                text = error,
                color = PipRed,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp)
            )
        }
    }
}

@Composable
fun OperationEditLoadoutScreen(
    loadout: OperationLoadoutSnapshot,
    onLoadoutChanged: (OperationLoadoutSnapshot) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val primaryOptions = PrimaryWeaponRole.entries.flatMap { it.weapons }.map { it.displayName }
    val secondaryOptions = SecondaryWeaponCatalog.weapons.map { it.displayName }
    val accessoryOptions = listOf("DETON-A", "THUNDER B", "TANTO", "MINI KNIFE", "VOLCANO", "WATCH 2")
    val headgearOptions = HeadgearCatalog.profiles
    val frontPanelOptions = FrontPanelRole.entries.map { it.displayName }
    val uniformOptions = UniformCatalog.options
    var expanded by remember { mutableStateOf<String?>(null) }

    DataFrame(title = "EDIT OPERATION - LOADOUT", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 72.dp, bottom = 82.dp)
                .heightIn(max = 300.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            EditStringSelector(
                "PRIMARY WEAPON", loadout.primaryWeapon, primaryOptions, expanded == "PRIMARY",
                onExpandedChange = { expanded = if (expanded == "PRIMARY") null else "PRIMARY" },
                onSelected = { value -> onLoadoutChanged(loadout.copy(primaryWeapon = value)); expanded = null }
            )
            EditStringSelector(
                "SECONDARY WEAPON", loadout.secondaryWeapon, secondaryOptions, expanded == "SECONDARY",
                onExpandedChange = { expanded = if (expanded == "SECONDARY") null else "SECONDARY" },
                onSelected = { value -> onLoadoutChanged(loadout.copy(secondaryWeapon = value)); expanded = null }
            )
            EditStringSelector(
                label = "UNIFORM",
                value = loadout.uniform,
                options = uniformOptions,
                expanded = expanded == "UNIFORM",
                placeholder = "SELECT UNIFORM",
                onExpandedChange = { expanded = if (expanded == "UNIFORM") null else "UNIFORM" },
                onSelected = { value ->
                    onLoadoutChanged(loadout.copy(uniform = value))
                    expanded = null
                }
            )
            Text("ACCESORIES", color = PipGreenDim, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
            accessoryOptions.forEach { option ->
                val selected = option in loadout.accesories
                Text(
                    text = if (selected) "[X] $option" else "[ ] $option",
                    color = PipGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable {
                        val updated = if (selected) loadout.accesories - option else loadout.accesories + option
                        onLoadoutChanged(loadout.copy(accesories = updated))
                    }
                )
            }
            EditStringSelector(
                "HEADGEAR", loadout.headgear, headgearOptions, expanded == "HEADGEAR",
                onExpandedChange = { expanded = if (expanded == "HEADGEAR") null else "HEADGEAR" },
                onSelected = { value -> onLoadoutChanged(loadout.copy(headgear = value)); expanded = null }
            )
            EditStringSelector(
                "FRONT PANEL", loadout.frontPanel, frontPanelOptions, expanded == "FRONT",
                onExpandedChange = { expanded = if (expanded == "FRONT") null else "FRONT" },
                onSelected = { value -> onLoadoutChanged(loadout.copy(frontPanel = value)); expanded = null }
            )
        }
        DataButton("NEXT", onNext, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
    }
}

@Composable
private fun EditStringSelector(
    label: String,
    value: String?,
    options: List<String>,
    expanded: Boolean,
    placeholder: String = "NOT CONFIGURED",
    onExpandedChange: () -> Unit,
    onSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, color = PipGreenDim, fontSize = 15.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value ?: placeholder,
            color = PipGreen,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.border(1.dp, PipGreen).clickable(onClick = onExpandedChange)
                .padding(horizontal = 12.dp, vertical = 7.dp)
        )
        if (expanded) options.forEach { option ->
            Text("> $option", color = PipGreen, fontSize = 17.sp, fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable { onSelected(option) }.padding(vertical = 3.dp))
        }
    }
}

@Composable
fun OperationEditConfirmScreen(
    draft: OperationEditDraft,
    saveError: String?,
    saving: Boolean,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    DataFrame(title = "EDIT OPERATION - CONFIRM MODIFICATIONS", onBack = onBack) {
        Column(
            modifier = Modifier.align(Alignment.TopStart)
                .padding(start = 42.dp, end = 42.dp, top = 76.dp, bottom = 88.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DataValue("DATE", draft.date)
            DataValue("LOCATION", draft.location)
            DataValue("PRIMARY WEAPON", draft.loadout.primaryWeapon)
            DataValue("SECONDARY WEAPON", draft.loadout.secondaryWeapon)
            DataValue("ACCESORIES", draft.loadout.accesories.takeIf { it.isNotEmpty() }?.joinToString(" + "))
            DataValue("HEADGEAR", draft.loadout.headgear)
            DataValue("FRONT PANEL", draft.loadout.frontPanel)
            DataValue("UNIFORM", draft.loadout.uniform)
            DataValue("PRIMARY MAG", OperationInputValidator.formatDecimal(draft.consumables.primaryMag))
            DataValue("SECONDARY MAG", OperationInputValidator.formatDecimal(draft.consumables.secondaryMag))
            DataValue("40mm GRENADES", OperationInputValidator.formatDecimal(draft.consumables.grenades40mm))
            DataValue("9mm GRENADES", OperationInputValidator.formatDecimal(draft.consumables.grenades9mm))
            DataValue("CO2 GRENADES", OperationInputValidator.formatDecimal(draft.consumables.grenadesCo2))
            DataValue("PRIMARY HPA", OperationInputValidator.formatDecimal(draft.consumables.primaryHpa))
            DataValue("SECONDARY HPA", OperationInputValidator.formatDecimal(draft.consumables.secondaryHpa))
        }
        saveError?.let { Text(it, color = PipRed, fontSize = 14.sp, fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 70.dp)) }
        DataButton(if (saving) "SAVING..." else "CONFIRM MODIFICATIONS", onConfirm, !saving,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
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
            DataValue("PRIMARY WEAPON", activeLoadout.primaryWeaponDisplayName())
            DataValue("SECONDARY WEAPON", activeLoadout.secondaryWeapon?.displayName)
            DataValue(
                "ACCESORIES",
                activeLoadout.accesories.takeIf { it.isNotEmpty() }
                    ?.joinToString(" + ") { it.displayName }
            )
            DataValue("HEADGEAR", activeLoadout.headgearProfile)
            DataValue("FRONT PANEL", activeLoadout.frontPanelRole)
            DataValue("UNIFORM", activeLoadout.uniform)
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
            DataValue("UNIFORM", loadout?.uniform)
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
            text = "PIP-SuriOS v2.6",
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
@Suppress("ModifierParameter")
private fun DataButton(
    label: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    color: Color = PipGreen,
    modifier: Modifier = Modifier
) {
    val displayColor: Color = if (enabled) color else PipGreenDim
    Text(
        text = label,
        color = displayColor,
        fontSize = 18.sp,
        fontFamily = FontFamily.Monospace,
        modifier = modifier.border(1.dp, displayColor)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    )
}
