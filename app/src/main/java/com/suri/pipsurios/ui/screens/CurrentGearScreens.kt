package com.suri.pipsurios.ui.screens

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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
    Box(
        modifier = Modifier.fillMaxSize().background(PipBlack),
        contentAlignment = Alignment.Center
    ) {
        Text("LOADING...", color = PipGreen, fontSize = 30.sp, fontFamily = FontFamily.Monospace)
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
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
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
    onEditPrimaryWeapon: () -> Unit,
    onDeletePrimaryRole: () -> Unit,
    onDeletePrimaryWeapon: () -> Unit,
    onBack: () -> Unit
) {
    val operatorFields = listOf(
        OperatorField.ID,
        OperatorField.NAME,
        OperatorField.CALLSIGN,
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
                "PRIMARY WEAPON",
                color = PipGreen,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 10.dp)
            )
            SetupDataRow(
                label = "ROLE",
                value = setupLoadout.primaryRole?.displayName ?: "NOT SET",
                onEdit = onEditPrimaryWeapon,
                onDelete = onDeletePrimaryRole
            )
            SetupDataRow(
                label = "REPLICA",
                value = setupLoadout.primaryWeaponDisplayName() ?: "NOT SET",
                onEdit = onEditPrimaryWeapon,
                onDelete = onDeletePrimaryWeapon
            )
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
        SetupDataAction("EDIT", PipGreen, onEdit)
        SetupDataAction("DELETE", PipRed, onDelete)
    }
}

@Composable
private fun SetupDataAction(label: String, color: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Text(
        text = label,
        color = color,
        fontSize = 16.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .border(1.dp, color)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
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
    customWeaponInput: Boolean = false
) {
    var roleExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - PRIMARY WEAPON", onBack = onBack) {
        val roleSelector: @Composable () -> Unit = {
            TerminalDropdown(
                label = "ROLE",
                value = configuration.primaryRole?.displayName ?: "SELECT ROLE",
                options = PrimaryWeaponRole.entries.toList(),
                optionText = { it.displayName },
                expanded = roleExpanded,
                onExpandedChange = {
                    roleExpanded = it
                    if (it) weaponExpanded = false
                },
                onSelected = { role ->
                    onConfigurationChanged(
                        configuration.copy(
                            primaryRole = role,
                            primaryWeapon = null,
                            primaryWeaponText = null
                        )
                    )
                    roleExpanded = false
                }
            )
        }
        val weaponSelector: @Composable () -> Unit = {
            if (customWeaponInput) {
                TerminalTextInput(
                    label = "WEAPON",
                    value = configuration.primaryWeaponText
                        ?: configuration.primaryWeapon?.displayName.orEmpty(),
                    placeholder = "ENTER REPLICA",
                    onValueChange = { value ->
                        onConfigurationChanged(
                            configuration.copy(
                                primaryWeapon = null,
                                primaryWeaponText = value.take(MAX_PRIMARY_WEAPON_LENGTH)
                            )
                        )
                    }
                )
            } else {
                TerminalDropdown(
                    label = "WEAPON",
                    value = configuration.primaryWeaponDisplayName() ?: "SELECT WEAPON",
                    options = configuration.primaryRole?.weapons.orEmpty(),
                    optionText = { it.displayName },
                    enabled = configuration.primaryRole != null,
                    expanded = weaponExpanded,
                    onExpandedChange = {
                        weaponExpanded = it
                        if (it) roleExpanded = false
                    },
                    onSelected = { weapon ->
                        onConfigurationChanged(
                            configuration.copy(primaryWeapon = weapon, primaryWeaponText = null)
                        )
                        weaponExpanded = false
                    }
                )
            }
        }
        if (customWeaponInput) {
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                roleSelector()
                weaponSelector()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Top
            ) {
                roleSelector()
                weaponSelector()
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
    verticalLayout: Boolean = false
) {
    val handgunWeapons = SecondaryWeaponCatalog.handgun
    val demolitionWeapons = SecondaryWeaponCatalog.demolition
    var typeExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - SECONDARY WEAPON", onBack = onBack) {
        val content: @Composable () -> Unit = {
            TerminalDropdown(
                label = "TYPE",
                value = configuration.secondaryType ?: "SELECT TYPE",
                options = listOf("HANDGUN", "DEMOLITION"),
                optionText = { it },
                expanded = typeExpanded,
                onExpandedChange = {
                    typeExpanded = it
                    if (it) weaponExpanded = false
                },
                onSelected = { type ->
                    onConfigurationChanged(
                        configuration.copy(secondaryType = type, secondaryWeapon = null)
                    )
                    typeExpanded = false
                }
            )
            TerminalDropdown(
                label = "WEAPON",
                value = configuration.secondaryWeapon?.displayName ?: "SELECT WEAPON",
                options = when (configuration.secondaryType) {
                    "HANDGUN" -> handgunWeapons
                    "DEMOLITION" -> demolitionWeapons
                    else -> emptyList()
                },
                optionText = { it.displayName },
                enabled = configuration.secondaryType != null,
                expanded = weaponExpanded,
                onExpandedChange = {
                    weaponExpanded = it
                    if (it) typeExpanded = false
                },
                onSelected = { weapon ->
                    onConfigurationChanged(configuration.copy(secondaryWeapon = weapon))
                    weaponExpanded = false
                }
            )
        }
        if (verticalLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                content()
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
                content()
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
    verticalLayout: Boolean = false
) {
    val accesories = listOf(
        InventoryItem.DETON_A,
        InventoryItem.THUNDER_B,
        InventoryItem.TANTO,
        InventoryItem.MINI_KNIFE,
        InventoryItem.VOLCANO,
        InventoryItem.WATCH_2
    )
    var expanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - ACCESORIES", onBack = onBack) {
        Box(modifier = Modifier.offset(y = (-12).dp)) {
            TerminalMultiSelect(
                label = "ACCESORIES",
                value = if (configuration.accesories.isEmpty()) {
                    "SELECT ACCESORIES"
                } else {
                    configuration.accesories.joinToString(" + ") { it.displayName }
                },
                options = accesories,
                optionText = { it.displayName },
                selected = configuration.accesories,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onToggle = { item ->
                    val updated = if (item in configuration.accesories) {
                        configuration.accesories - item
                    } else {
                        configuration.accesories + item
                    }
                    onConfigurationChanged(configuration.copy(accesories = updated))
                },
                compactLayout = verticalLayout
            )
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
    val selectedProfile = HeadgearProfile.entries.find {
        it.displayName == configuration.headgearProfile
    }
    var profileExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - HEADGEAR", onBack = onBack) {
        val content: @Composable () -> Unit = {
            TerminalDropdown(
                label = "PROFILE",
                value = selectedProfile?.displayName ?: "SELECT PROFILE",
                options = HeadgearProfile.entries.toList(),
                optionText = { it.displayName },
                expanded = profileExpanded,
                onExpandedChange = { profileExpanded = it },
                onSelected = { profile ->
                    onConfigurationChanged(configuration.copy(headgearProfile = profile.displayName))
                    profileExpanded = false
                }
            )
            selectedProfile?.let { profile ->
                TerminalVisualList(label = "ITEM", entries = profile.items)
            }
        }
        if (verticalLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                content()
            }
        } else {
            Row(
                horizontalArrangement = Arrangement.spacedBy(48.dp),
                verticalAlignment = Alignment.Top
            ) {
                content()
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
    verticalLayout: Boolean = false
) {
    val selectedRole = FrontPanelRole.entries.find {
        it.displayName == configuration.frontPanelRole
    }
    var roleExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - FRONT PANEL", onBack = onBack) {
        val content: @Composable () -> Unit = {
            TerminalDropdown(
                label = "ROLE",
                value = selectedRole?.displayName ?: "SELECT ROLE",
                options = FrontPanelRole.entries.toList(),
                optionText = { it.displayName },
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = it },
                onSelected = { role ->
                    onConfigurationChanged(configuration.copy(frontPanelRole = role.displayName))
                    roleExpanded = false
                }
            )
            selectedRole?.let { role ->
                TerminalVisualList(
                    label = "PANEL",
                    entries = role.panels.map { it.displayName }
                )
            }
        }
        if (verticalLayout) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                content()
            }
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
                content()
            }
        }
    }
}

@Composable
fun UniformScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR"
) {
    var expanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - UNIFORM", onBack = onBack) {
        TerminalDropdown(
            label = "UNIFORM",
            value = configuration.uniform ?: "SELECT UNIFORM",
            options = UniformCatalog.options,
            optionText = { it },
            expanded = expanded,
            onExpandedChange = { expanded = it },
            onSelected = { uniform ->
                onConfigurationChanged(configuration.copy(uniform = uniform))
                expanded = false
            }
        )
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
private fun TerminalTextInput(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.width(270.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.width(270.dp).border(1.dp, PipGreen),
            textStyle = TextStyle(
                color = PipGreen,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace
            ),
            placeholder = {
                Text(placeholder, color = PipGreenDim, fontFamily = FontFamily.Monospace)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Characters,
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboard?.hide() }),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = PipBlack,
                unfocusedContainerColor = PipBlack,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PipGreen
            )
        )
    }
}

@Composable
private fun <T> TerminalMultiSelect(
    label: String,
    value: String,
    options: List<T>,
    optionText: (T) -> String,
    selected: Set<T>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onToggle: (T) -> Unit,
    compactLayout: Boolean = false
) {
    val width = if (compactLayout) 320.dp else 560.dp
    Column(modifier = Modifier.width(width), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 2,
            modifier = Modifier
                .width(width)
                .border(1.dp, PipGreen)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
        if (expanded) {
            Column(
                modifier = Modifier
                    .width(width)
                    .heightIn(max = 150.dp)
                    .border(1.dp, PipGreen)
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach { option ->
                    Text(
                        text = if (option in selected) {
                            "[X] ${optionText(option)}"
                        } else {
                            "[ ] ${optionText(option)}"
                        },
                        color = PipGreen,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier
                            .width(width)
                            .clickable { onToggle(option) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
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
        Box(modifier = Modifier.align(Alignment.Center)) { content() }
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

private const val MAX_PRIMARY_WEAPON_LENGTH = 80
