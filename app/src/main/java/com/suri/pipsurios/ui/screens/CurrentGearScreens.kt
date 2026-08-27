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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
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
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed
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
    onOperatorSelected: () -> Unit,
    onPrimaryWeaponSelected: () -> Unit,
    onSecondaryWeaponSelected: () -> Unit,
    onAccesoriesSelected: () -> Unit,
    onHeadgearSelected: () -> Unit,
    onFrontPanelSelected: () -> Unit,
    onUniformSelected: () -> Unit,
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "SET-UP", onBack = onBack) {
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
    titlePrefix: String = "CURRENT GEAR"
) {
    var roleExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - PRIMARY WEAPON", onBack = onBack) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.Top
        ) {
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
                        configuration.copy(primaryRole = role, primaryWeapon = null)
                    )
                    roleExpanded = false
                }
            )
            TerminalDropdown(
                label = "WEAPON",
                value = configuration.primaryWeapon?.displayName ?: "SELECT WEAPON",
                options = configuration.primaryRole?.weapons.orEmpty(),
                optionText = { it.displayName },
                enabled = configuration.primaryRole != null,
                expanded = weaponExpanded,
                onExpandedChange = {
                    weaponExpanded = it
                    if (it) roleExpanded = false
                },
                onSelected = { weapon ->
                    onConfigurationChanged(configuration.copy(primaryWeapon = weapon))
                    weaponExpanded = false
                }
            )
        }
    }
}

@Composable
fun SecondaryWeaponScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR"
) {
    val handgunWeapons = SecondaryWeaponCatalog.handgun
    val demolitionWeapons = SecondaryWeaponCatalog.demolition
    var typeExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - SECONDARY WEAPON", onBack = onBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
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
    }
}

@Composable
fun AccesoriesScreen(
    configuration: LoadoutConfiguration,
    onConfigurationChanged: (LoadoutConfiguration) -> Unit,
    onBack: () -> Unit,
    titlePrefix: String = "CURRENT GEAR"
) {
    val accesories = listOf(
        InventoryItem.DETON_A,
        InventoryItem.THUNDER_B,
        InventoryItem.TANTO,
        InventoryItem.MINI_KNIFE,
        InventoryItem.VOLCANO
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
                }
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
    titlePrefix: String = "CURRENT GEAR"
) {
    val selectedProfile = HeadgearProfile.entries.find {
        it.displayName == configuration.headgearProfile
    }
    var profileExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - HEADGEAR", onBack = onBack) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.Top
        ) {
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
    titlePrefix: String = "CURRENT GEAR"
) {
    val selectedRole = FrontPanelRole.entries.find {
        it.displayName == configuration.frontPanelRole
    }
    var roleExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "$titlePrefix - FRONT PANEL", onBack = onBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
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
private fun <T> TerminalMultiSelect(
    label: String,
    value: String,
    options: List<T>,
    optionText: (T) -> String,
    selected: Set<T>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onToggle: (T) -> Unit
) {
    Column(modifier = Modifier.width(560.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        Text(
            text = value,
            color = PipGreen,
            fontSize = 20.sp,
            fontFamily = FontFamily.Monospace,
            maxLines = 2,
            modifier = Modifier
                .width(560.dp)
                .border(1.dp, PipGreen)
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 14.dp, vertical = 10.dp)
        )
        if (expanded) {
            Column(
                modifier = Modifier
                    .width(560.dp)
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
                            .width(560.dp)
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
            text = "PIP-SuriOS v2.2",
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
