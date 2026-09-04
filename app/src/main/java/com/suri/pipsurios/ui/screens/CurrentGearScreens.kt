package com.suri.pipsurios.ui.screens

import com.suri.pipsurios.PipSuriOsVersion
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
import com.suri.pipsurios.data.OperatorField
import com.suri.pipsurios.data.OperatorProfile
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import kotlinx.coroutines.delay

@Composable
fun CurrentGearLoadingScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1_500)
        onFinished()
    }
    TerminalScreen {
        LoadingGlitchText(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun CurrentGearScreen(
    onPrimaryWeaponSelected: () -> Unit,
    onSecondaryWeaponSelected: () -> Unit,
    onAccesoriesSelected: () -> Unit,
    onHeadgearSelected: () -> Unit,
    onFrontPanelSelected: () -> Unit,
    onUniformSelected: () -> Unit,
    isApplied: Boolean,
    onApply: () -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(
        title = "CURRENT GEAR",
        onBack = onBack,
        onApply = onApply,
        isApplied = isApplied
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(
                "> PRIMARY WEAPON", "> SECONDARY WEAPON", "> ACCESORIES",
                "> HEADGEAR", "> FRONT PANEL", "> UNIFORM"
            ).forEach { entry ->
                Text(
                    text = entry,
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(
                        onClick = when (entry) {
                            "> PRIMARY WEAPON" -> onPrimaryWeaponSelected
                            "> SECONDARY WEAPON" -> onSecondaryWeaponSelected
                            "> ACCESORIES" -> onAccesoriesSelected
                            "> HEADGEAR" -> onHeadgearSelected
                            "> FRONT PANEL" -> onFrontPanelSelected
                            else -> onUniformSelected
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun SetUpScreen(
    onInputSelected: () -> Unit,
    onDataSelected: () -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "SET-UP", onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(
                "> INPUT" to onInputSelected,
                "> DATA" to onDataSelected
            ).forEach { (entry, action) ->
                Text(
                    text = entry,
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(onClick = action)
                )
            }
        }
    }
}

@Composable
fun SetUpInputScreen(
    onOperatorSelected: () -> Unit,
    onPrimaryWeaponSelected: () -> Unit,
    onSecondaryWeaponSelected: () -> Unit,
    onAccesoriesSelected: () -> Unit,
    onHeadgearSelected: () -> Unit,
    onFrontPanelSelected: () -> Unit,
    onUniformSelected: () -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "SET-UP - INPUT", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            listOf(
                "> OPERATOR", "> PRIMARY WEAPON", "> SECONDARY WEAPON", "> ACCESORIES",
                "> HEADGEAR", "> FRONT PANEL", "> UNIFORM"
            ).forEach { entry ->
                Text(
                    text = entry,
                    color = PipGreen,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.clickable(
                        onClick = when (entry) {
                            "> OPERATOR" -> onOperatorSelected
                            "> PRIMARY WEAPON" -> onPrimaryWeaponSelected
                            "> SECONDARY WEAPON" -> onSecondaryWeaponSelected
                            "> ACCESORIES" -> onAccesoriesSelected
                            "> HEADGEAR" -> onHeadgearSelected
                            "> FRONT PANEL" -> onFrontPanelSelected
                            else -> onUniformSelected
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun SetUpDataScreen(
    operatorProfile: OperatorProfile,
    setupLoadout: LoadoutConfiguration,
    onEditOperatorField: (OperatorField) -> Unit,
    onDeleteOperatorField: (OperatorField) -> Unit,
    onWeaponReplicasSelected: () -> Unit,
    onCustomListsSelected: () -> Unit,
    onBack: () -> Unit
) {
    val operatorFields = listOf(
        OperatorField.ID,
        OperatorField.NAME,
        OperatorField.NUMBER,
        OperatorField.COUNTRY,
        OperatorField.TEAM
    )

    CurrentGearLayout(title = "SET-UP - DATA", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("OPERATOR", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            operatorFields.forEach { field ->
                SetupDataRow(
                    label = field.name,
                    value = operatorProfile.valueFor(field).ifBlank { "NOT SET" },
                    onEdit = { onEditOperatorField(field) },
                    onDelete = { onDeleteOperatorField(field) }
                )
            }

            Text(
                text = "> WEAPON REPLICAS",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clickable(onClick = onWeaponReplicasSelected)
            )
            Text(
                text = "> CUSTOM LISTS",
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable(onClick = onCustomListsSelected)
            )
        }
    }
}

@Composable
fun SetUpWeaponReplicasScreen(
    setupLoadout: LoadoutConfiguration,
    onEditPrimaryWeapon: (String, String) -> Unit,
    onDeletePrimaryWeapon: (String) -> Unit,
    onEditSecondaryWeapon: (String, String) -> Unit,
    onDeleteSecondaryWeapon: (String) -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "SET-UP - DATA - WEAPON REPLICAS", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("PRIMARY WEAPON", color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
            Text(
                "ACTIVE: ${setupLoadout.primaryWeaponDisplayName() ?: "NOT SET"}",
                color = PipGreenDim,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            EditableCatalogSection(
                title = "PRIMARY WEAPON OPTIONS",
                options = setupLoadout.primaryWeaponOptions,
                onEdit = onEditPrimaryWeapon,
                onDelete = onDeletePrimaryWeapon
            )
            Text(
                "SECONDARY WEAPON",
                color = PipGreen,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                "ACTIVE: ${setupLoadout.secondaryWeaponDisplayName() ?: "NOT SET"}",
                color = PipGreenDim,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )
            EditableCatalogSection(
                title = "SECONDARY WEAPON OPTIONS",
                options = setupLoadout.secondaryWeaponOptions,
                onEdit = onEditSecondaryWeapon,
                onDelete = onDeleteSecondaryWeapon
            )
        }
    }
}

@Composable
fun SetUpCustomListsScreen(
    setupLoadout: LoadoutConfiguration,
    onEditAccessory: (String, String) -> Unit,
    onDeleteAccessory: (String) -> Unit,
    onEditFrontPanel: (String, String) -> Unit,
    onDeleteFrontPanel: (String) -> Unit,
    onEditUniform: (String, String) -> Unit,
    onDeleteUniform: (String) -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "SET-UP - DATA - CUSTOM LISTS", onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            EditableCatalogSection("ACCESORIES", setupLoadout.accesoryOptions, onEditAccessory, onDeleteAccessory)
            EditableCatalogSection("FRONT PANEL", setupLoadout.frontPanelOptions, onEditFrontPanel, onDeleteFrontPanel)
            EditableCatalogSection("UNIFORM", setupLoadout.uniformOptions, onEditUniform, onDeleteUniform)
        }
    }
}

@Composable
private fun EditableCatalogSection(
    title: String,
    options: List<String>,
    onEdit: (String, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var editingOption by remember { mutableStateOf<String?>(null) }
    var editValue by remember { mutableStateOf("") }

    Text(title, color = PipGreen, fontSize = 22.sp, fontFamily = FontFamily.Monospace)
    if (options.isEmpty()) {
        Text("NOT SET", color = PipGreenDim, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
    }
    options.forEach { option ->
        if (editingOption == option) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = editValue,
                    onValueChange = { editValue = it },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = PipGreen,
                        unfocusedTextColor = PipGreen,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = PipGreen,
                        unfocusedIndicatorColor = PipGreenDim,
                        cursorColor = PipGreen
                    ),
                    modifier = Modifier.weight(1f)
                )
                SetupDataAction("SAVE", PipGreen) {
                    val newValue = editValue.trim()
                    if (newValue.isNotEmpty()) {
                        onEdit(option, newValue)
                        editingOption = null
                    }
                }
                SetupDataAction("CANCEL", PipGreenDim) { editingOption = null }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "> $option",
                    color = PipGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                SetupDataAction("EDIT", PipGreen) {
                    editingOption = option
                    editValue = option
                }
                SetupDataAction("DELETE", PipRed) {
                    if (editingOption == option) editingOption = null
                    onDelete(option)
                }
            }
        }
    }
}

@Composable
private fun SetupDataRow(
    label: String,
    value: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "$label: $value",
            color = PipGreen,
            fontSize = 19.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        SetupDataAction("EDIT", PipGreen, onClick = onEdit)
        SetupDataAction("DELETE", PipRed, onClick = onDelete)
    }
}

@Composable
private fun SetupDataAction(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val actionColor = if (enabled) color else PipGreenDim
    Text(
        text = label,
        color = actionColor,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .border(1.dp, actionColor)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Composable
private fun ManualGearTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "ENTER $label",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(label, color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
        TextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text(placeholder, fontFamily = FontFamily.Monospace) },
            colors = TextFieldDefaults.colors(
                focusedTextColor = PipGreen,
                unfocusedTextColor = PipGreen,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = PipGreen,
                unfocusedIndicatorColor = PipGreenDim,
                cursorColor = PipGreen
            ),
            modifier = modifier.fillMaxWidth()
        )
    }
}

@Composable
fun SetUpPlaceholderScreen(title: String, onBack: () -> Unit) {
    CurrentGearLayout(title = title, onBack = onBack) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title.removePrefix("SET-UP - "), color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Text("UNDER CONSTRUCTION", color = PipRed, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun OperatorScreen(onBack: () -> Unit) {
    CurrentGearLayout(title = "CURRENT GEAR - OPERATOR", onBack = onBack) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text("OPERATOR", color = PipGreen, fontSize = 24.sp, fontFamily = FontFamily.Monospace)
            Text("UNDER CONSTRUCTION", color = PipRed, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        }
    }
}

@Composable
fun PrimaryWeaponScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    catalogMode: Boolean = false
) {
    var role by remember {
        mutableStateOf(configuration.primaryRoleInput())
    }
    var model by remember {
        mutableStateOf(configuration.primaryModelInput())
    }
    var appliedValue by remember { mutableStateOf<String?>(null) }
    var optionInputs by remember(configuration.primaryWeaponOptions.joinToString("\u0000")) {
        mutableStateOf(configuration.primaryWeaponOptions)
    }

    CurrentGearLayout(
        title = "$titlePrefix - PRIMARY WEAPON",
        onBack = onBack,
        contentAlignment = Alignment.TopCenter,
        contentModifier = Modifier.padding(top = 96.dp, bottom = 76.dp)
    ) {
        Column(
            modifier = Modifier
                .width(340.dp)
                .heightIn(max = 760.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ManualGearTextField(
                label = "ROLE",
                value = role,
                onValueChange = { role = it }
            )
            ManualGearTextField(
                label = "MODEL",
                value = model,
                onValueChange = { model = it }
            )
            SetupDataAction(
                label = "APPLY",
                color = PipGreen,
                enabled = role.trim().isNotEmpty() || model.trim().isNotEmpty()
            ) {
                val base = configuration.withPrimaryWeaponInputs(role = role, model = model)
                val option = base.primaryWeaponDisplayName()
                val updated = if (catalogMode && option != null) {
                    base.copy(primaryWeaponOptions = (configuration.primaryWeaponOptions + option).distinct())
                } else {
                    base
                }
                onConfigurationChanged(updated)
                appliedValue = option
                role = ""
                model = ""
            }
            appliedValue?.let { value ->
                Text("APPLIED: $value", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            }
            if (catalogMode) {
                Text("SAVED OPTIONS", color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
                optionInputs.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ManualGearTextField(
                            label = option.ifBlank { "EMPTY OPTION ${index + 1}" },
                            value = option,
                            modifier = Modifier.weight(1f),
                            onValueChange = { value ->
                                val updatedOptions = optionInputs.toMutableList().also { it[index] = value }
                                optionInputs = updatedOptions
                                val base = configuration.copy(primaryWeaponOptions = updatedOptions)
                                onConfigurationChanged(
                                    if (configuration.primaryWeaponDisplayName() == option) {
                                        base.withPrimaryWeaponOption(value)
                                    } else {
                                        base
                                    }
                                )
                            }
                        )
                        SetupDataAction("DELETE", PipRed) {
                            val updatedOptions = optionInputs.toMutableList().also { it.removeAt(index) }
                            optionInputs = updatedOptions
                            onConfigurationChanged(
                                if (configuration.primaryWeaponDisplayName() == option) {
                                    configuration.copy(
                                        primaryWeaponOptions = updatedOptions,
                                        primaryRole = null,
                                        primaryWeapon = null,
                                        primaryRoleText = null,
                                        primaryModelText = null
                                    )
                                } else {
                                    configuration.copy(primaryWeaponOptions = updatedOptions)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecondaryWeaponScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    catalogMode: Boolean = false
) {
    var type by remember {
        mutableStateOf(configuration.secondaryTypeInput())
    }
    var model by remember {
        mutableStateOf(configuration.secondaryModelInput())
    }
    var appliedValue by remember { mutableStateOf<String?>(null) }
    var optionInputs by remember(configuration.secondaryWeaponOptions.joinToString("\u0000")) {
        mutableStateOf(configuration.secondaryWeaponOptions)
    }

    CurrentGearLayout(
        title = "$titlePrefix - SECONDARY WEAPON",
        onBack = onBack,
        contentAlignment = Alignment.TopCenter,
        contentModifier = Modifier.padding(top = 96.dp, bottom = 76.dp)
    ) {
        Column(
            modifier = Modifier
                .width(340.dp)
                .heightIn(max = 760.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ManualGearTextField(
                label = "TYPE",
                value = type,
                onValueChange = { type = it }
            )
            ManualGearTextField(
                label = "MODEL",
                value = model,
                onValueChange = { model = it }
            )
            SetupDataAction(
                label = "APPLY",
                color = PipGreen,
                enabled = type.trim().isNotEmpty() || model.trim().isNotEmpty()
            ) {
                val base = configuration.withSecondaryWeaponInputs(type = type, model = model)
                val option = base.secondaryWeaponDisplayName()
                val updated = if (catalogMode && option != null) {
                    base.copy(secondaryWeaponOptions = (configuration.secondaryWeaponOptions + option).distinct())
                } else {
                    base
                }
                onConfigurationChanged(updated)
                appliedValue = option
                type = ""
                model = ""
            }
            appliedValue?.let { value ->
                Text("APPLIED: $value", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            }
            if (catalogMode) {
                Text("SAVED OPTIONS", color = PipGreen, fontSize = 18.sp, fontFamily = FontFamily.Monospace)
                optionInputs.forEachIndexed { index, option ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ManualGearTextField(
                            label = option.ifBlank { "EMPTY OPTION ${index + 1}" },
                            value = option,
                            modifier = Modifier.weight(1f),
                            onValueChange = { value ->
                                val updatedOptions = optionInputs.toMutableList().also { it[index] = value }
                                optionInputs = updatedOptions
                                val base = configuration.copy(secondaryWeaponOptions = updatedOptions)
                                onConfigurationChanged(
                                    if (configuration.secondaryWeaponDisplayName() == option) {
                                        base.withSecondaryWeaponOption(value)
                                    } else {
                                        base
                                    }
                                )
                            }
                        )
                        SetupDataAction("DELETE", PipRed) {
                            val updatedOptions = optionInputs.toMutableList().also { it.removeAt(index) }
                            optionInputs = updatedOptions
                            onConfigurationChanged(
                                if (configuration.secondaryWeaponDisplayName() == option) {
                                    configuration.copy(
                                        secondaryWeaponOptions = updatedOptions,
                                        secondaryType = null,
                                        secondaryWeapon = null,
                                        secondaryTypeText = null,
                                        secondaryModelText = null
                                    )
                                } else {
                                    configuration.copy(secondaryWeaponOptions = updatedOptions)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccesoriesScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    verticalLayout: Boolean = false,
    accesoryOptions: List<String>? = null,
    catalogMode: Boolean = false
) {
    val configuredNames = if (catalogMode) {
        accesoryOptions ?: configuration.accesoryOptions
    } else {
        (configuration.accesories.map { it.displayName } + configuration.customAccesories).distinct()
    }
    var itemInputs by remember(configuredNames.joinToString("\u0000"), catalogMode) {
        mutableStateOf(configuredNames.ifEmpty { listOf("") })
    }
    var newItemInput by remember { mutableStateOf("") }

    fun saveItems(values: List<String>) {
        val cleanValues = values.map { it.trim() }.filter { it.isNotEmpty() }.distinct()
        if (catalogMode) {
            onConfigurationChanged(configuration.copy(accesoryOptions = cleanValues))
        } else {
            val builtIn = cleanValues.mapNotNull { value ->
                InventoryItem.entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) }
            }.toSet()
            val custom = cleanValues.filter { value ->
                InventoryItem.entries.none { it.displayName.equals(value, ignoreCase = true) }
            }.toSet()
            onConfigurationChanged(
                configuration.copy(accesories = builtIn, customAccesories = custom)
            )
        }
    }

    CurrentGearLayout(title = "$titlePrefix - ACCESORIES", onBack = onBack) {
        Column(
            modifier = Modifier
                .width(340.dp)
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                if (catalogMode) "CUSTOM ACCESORIES" else "ACCESORIES",
                color = PipGreen,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
            ManualGearTextField(
                label = "NEW ACCESORY",
                value = newItemInput,
                onValueChange = { newItemInput = it }
            )
            SetupDataAction(
                label = "APPLY",
                color = PipGreen,
                enabled = newItemInput.trim().isNotEmpty() &&
                    itemInputs.none { it.trim().equals(newItemInput.trim(), ignoreCase = true) }
            ) {
                val updated = (itemInputs + newItemInput.trim()).distinct()
                itemInputs = updated
                saveItems(updated)
                newItemInput = ""
            }
            itemInputs.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ManualGearTextField(
                        label = "ITEM ${index + 1}",
                        value = item,
                        modifier = Modifier.weight(1f),
                        onValueChange = { value ->
                            val updated = itemInputs.toMutableList().also { it[index] = value }
                            itemInputs = updated
                            saveItems(updated)
                        }
                    )
                    SetupDataAction("DELETE", PipRed) {
                        val updated = itemInputs.toMutableList().also { it.removeAt(index) }
                        itemInputs = updated.ifEmpty { mutableListOf("") }
                        saveItems(itemInputs)
                    }
                }
            }
        }
    }
}

enum class HeadgearProfile(val displayName: String, val items: List<String>) {
    SURI_14("SURI-14", listOf("VYPER", "DYE MASK")),
    BROTHERHOOD("BROTHERHOOD", listOf("HELMET", "NVG", "GAS MASK", "SECURITY GOGGLES"))
}

object HeadgearCatalog {
    val profiles = HeadgearProfile.entries.map(HeadgearProfile::displayName)
}

object UniformCatalog {
    val options = listOf("MCBCK - SUMMER", "MCBCK - LONG", "DESERT")
}

@Composable
fun HeadgearScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    verticalLayout: Boolean = false
) {
    val defaultComponents = HeadgearProfile.entries
        .firstOrNull { it.displayName == configuration.headgearProfile }
        ?.items
        .orEmpty()
    var step by remember { mutableStateOf(1) }
    var name by remember { mutableStateOf(configuration.headgearProfile.orEmpty()) }
    var newComponentInput by remember { mutableStateOf("") }
    var componentInputs by remember {
        val initial = configuration.headgearComponents.ifEmpty { defaultComponents.toSet() }.toList()
        mutableStateOf(initial.ifEmpty { listOf("") })
    }

    CurrentGearLayout(
        title = "$titlePrefix - HEADGEAR",
        onBack = if (step == 2) { { step = 1 } } else onBack
    ) {
        if (step == 1) {
            Column(
                modifier = Modifier.width(320.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text("STEP 1/2 - NAME", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    placeholder = { Text("ENTER HEADGEAR NAME", fontFamily = FontFamily.Monospace) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = PipGreen,
                        unfocusedTextColor = PipGreen,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = PipGreen,
                        unfocusedIndicatorColor = PipGreenDim,
                        cursorColor = PipGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                SetupDataAction(
                    label = "APPLY",
                    color = PipGreen,
                    enabled = name.trim().isNotEmpty()
                ) {
                    val nextName = name.trim()
                    val nextComponents = if (nextName == configuration.headgearProfile) {
                        componentInputs.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                    } else {
                        emptySet()
                    }
                    componentInputs = nextComponents.toList().ifEmpty { listOf("") }
                    onConfigurationChanged(
                        configuration.copy(
                            headgearProfile = nextName,
                            headgearComponents = nextComponents
                        )
                    )
                    step = 2
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .width(340.dp)
                    .heightIn(max = 560.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("STEP 2/2 - COMPONENTS", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
                ManualGearTextField(
                    label = "NEW COMPONENT",
                    value = newComponentInput,
                    onValueChange = { newComponentInput = it }
                )
                SetupDataAction(
                    label = "APPLY",
                    color = PipGreen,
                    enabled = newComponentInput.trim().isNotEmpty() &&
                        componentInputs.none { it.trim().equals(newComponentInput.trim(), ignoreCase = true) }
                ) {
                    val updatedInputs = (componentInputs + newComponentInput.trim()).distinct()
                    componentInputs = updatedInputs
                    onConfigurationChanged(
                        configuration.copy(
                            headgearProfile = name.trim().ifBlank { configuration.headgearProfile },
                            headgearComponents = updatedInputs.map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                        )
                    )
                    newComponentInput = ""
                }
                componentInputs.forEachIndexed { index, component ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ManualGearTextField(
                            label = "COMPONENT ${index + 1}",
                            value = component,
                            modifier = Modifier.weight(1f),
                            onValueChange = { value ->
                                val updatedInputs = componentInputs.toMutableList().also { it[index] = value }
                                componentInputs = updatedInputs
                                val updatedComponents = updatedInputs.map { it.trim() }
                                    .filter { it.isNotEmpty() }
                                    .toSet()
                                onConfigurationChanged(
                                    configuration.copy(
                                        headgearProfile = name.trim().ifBlank { configuration.headgearProfile },
                                        headgearComponents = updatedComponents
                                    )
                                )
                            }
                        )
                        SetupDataAction("DELETE", PipRed) {
                            val updatedInputs = componentInputs.toMutableList().also { it.removeAt(index) }
                            componentInputs = updatedInputs.ifEmpty { mutableListOf("") }
                            val updatedComponents = componentInputs.map { it.trim() }
                                .filter { it.isNotEmpty() }
                                .toSet()
                            onConfigurationChanged(
                                configuration.copy(
                                    headgearProfile = name.trim().ifBlank { configuration.headgearProfile },
                                    headgearComponents = updatedComponents
                                )
                            )
                        }
                    }
                }
                SetupDataAction("ADD COMPONENT", PipGreen) {
                    componentInputs = componentInputs + ""
                }
            }
        }
    }
}

enum class FrontPanelRole(val displayName: String, val panels: List<InventoryItem>) {
    SNIPER_ASSAULT(
        "SNIPER - ASSAULT",
        PrimaryWeaponRole.SNIPER.weapons + InventoryItem.MCX
    ),
    LIGHT_ASSAULT("LIGHT ASSAULT", listOf(InventoryItem.APC_9K)),
    DEMOLITION("DEMOLITION", PrimaryWeaponRole.DEMOLITION.weapons)
}

@Composable
fun FrontPanelScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    verticalLayout: Boolean = false,
    frontPanelOptions: List<String>? = null
) {
    var panel by remember {
        mutableStateOf(configuration.frontPanelRole.orEmpty())
    }
    var appliedPanel by remember { mutableStateOf<String?>(null) }

    CurrentGearLayout(title = "$titlePrefix - FRONT PANEL", onBack = onBack) {
        Column(
            modifier = Modifier.width(340.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ManualGearTextField(
                label = "FRONT PANEL",
                value = panel,
                onValueChange = { panel = it }
            )
            SetupDataAction(
                label = "APPLY",
                color = PipGreen,
                enabled = panel.trim().isNotEmpty()
            ) {
                val value = panel.trim()
                onConfigurationChanged(configuration.copy(frontPanelRole = value))
                appliedPanel = value
                panel = ""
            }
            appliedPanel?.let { value ->
                Text("APPLIED: $value", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
fun UniformScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR",
    uniformOptions: List<String>? = null
) {
    var uniform by remember {
        mutableStateOf(configuration.uniform.orEmpty())
    }
    var appliedUniform by remember { mutableStateOf<String?>(null) }

    CurrentGearLayout(title = "$titlePrefix - UNIFORM", onBack = onBack) {
        Column(
            modifier = Modifier.width(340.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ManualGearTextField(
                label = "UNIFORM",
                value = uniform,
                onValueChange = { uniform = it }
            )
            SetupDataAction(
                label = "APPLY",
                color = PipGreen,
                enabled = uniform.trim().isNotEmpty()
            ) {
                val value = uniform.trim()
                onConfigurationChanged(configuration.copy(uniform = value))
                appliedUniform = value
                uniform = ""
            }
            appliedUniform?.let { value ->
                Text("APPLIED: $value", color = PipGreenDim, fontSize = 16.sp, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

@Composable
internal fun TerminalVisualList(label: String, entries: List<String>) {
    Column(
        modifier = Modifier.width(270.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        entries.forEach { entry ->
            Text(
                text = "> $entry",
                color = PipGreen,
                fontSize = 20.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
internal fun <T> TerminalDropdown(
    label: String,
    value: String,
    options: List<T>,
    optionText: (T) -> String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelected: (T) -> Unit,
    enabled: Boolean = true
) {
    val textColor = if (enabled) PipGreen else PipGreenDim
    Column(modifier = Modifier.width(270.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            color = textColor,
            fontSize = 22.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .width(270.dp)
                .border(1.dp, textColor)
                .then(if (enabled) Modifier.clickable { onExpandedChange(!expanded) } else Modifier)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
        if (expanded && enabled) {
            Column(modifier = Modifier.width(270.dp).border(1.dp, PipGreen)) {
                options.forEach { option ->
                    Text(
                        text = "> ${optionText(option)}",
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .width(270.dp)
                            .clickable { onSelected(option) }
                            .padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrentGearLayout(
    title: String,
    onBack: () -> Unit,
    onApply: (() -> Unit)? = null,
    isApplied: Boolean = false,
    contentAlignment: Alignment = Alignment.Center,
    contentModifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )
        Box(
            modifier = Modifier
                .align(contentAlignment)
                .then(contentModifier)
        ) { content() }
        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = PipSuriOsVersion,
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
        if (onApply != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 64.dp)
            ) {
                Text(
                    text = "APPLY",
                    color = if (isApplied) PipBlack else PipGreen,
                    fontSize = 20.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .background(if (isApplied) PipGreen else PipBlack)
                        .border(1.dp, PipGreen)
                        .clickable(onClick = onApply)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
    }
}
