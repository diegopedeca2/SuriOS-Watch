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
    onBack: () -> Unit
) {
    CurrentGearLayout(title = "CURRENT GEAR", onBack = onBack) {
        Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
            listOf(
                "> PRIMARY WEAPON", "> SECONDARY WEAPON", "> ACCESORIES",
                "> HEADGEAR", "> FRONT PANEL"
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
                            else -> onFrontPanelSelected
                        }
                    )
                )
            }
        }
    }
}

@Composable
fun PrimaryWeaponScreen(onBack: () -> Unit) {
    var selectedRole by remember { mutableStateOf<PrimaryWeaponRole?>(null) }
    var selectedWeapon by remember { mutableStateOf<InventoryItem?>(null) }
    var roleExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "CURRENT GEAR - PRIMARY WEAPON", onBack = onBack) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(48.dp),
            verticalAlignment = Alignment.Top
        ) {
            TerminalDropdown(
                label = "ROLE",
                value = selectedRole?.displayName ?: "SELECT ROLE",
                options = PrimaryWeaponRole.entries.toList(),
                optionText = { it.displayName },
                expanded = roleExpanded,
                onExpandedChange = {
                    roleExpanded = it
                    if (it) weaponExpanded = false
                },
                onSelected = { role ->
                    selectedRole = role
                    selectedWeapon = null
                    roleExpanded = false
                }
            )
            TerminalDropdown(
                label = "WEAPON",
                value = selectedWeapon?.displayName ?: "SELECT WEAPON",
                options = selectedRole?.weapons.orEmpty(),
                optionText = { it.displayName },
                enabled = selectedRole != null,
                expanded = weaponExpanded,
                onExpandedChange = {
                    weaponExpanded = it
                    if (it) roleExpanded = false
                },
                onSelected = { weapon ->
                    selectedWeapon = weapon
                    weaponExpanded = false
                }
            )
        }
    }
}

@Composable
fun SecondaryWeaponScreen(onBack: () -> Unit) {
    val handgunWeapons = listOf(InventoryItem.DESERT_EAGLE, InventoryItem.AAP_01C)
    val demolitionWeapons = PrimaryWeaponRole.DEMOLITION.weapons
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedWeapon by remember { mutableStateOf<InventoryItem?>(null) }
    var typeExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "CURRENT GEAR - SECONDARY WEAPON", onBack = onBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
            TerminalDropdown(
                label = "TYPE",
                value = selectedType ?: "SELECT TYPE",
                options = listOf("HANDGUN", "DEMOLITION"),
                optionText = { it },
                expanded = typeExpanded,
                onExpandedChange = {
                    typeExpanded = it
                    if (it) weaponExpanded = false
                },
                onSelected = { type ->
                    selectedType = type
                    selectedWeapon = null
                    typeExpanded = false
                }
            )
            TerminalDropdown(
                label = "WEAPON",
                value = selectedWeapon?.displayName ?: "SELECT WEAPON",
                options = when (selectedType) {
                    "HANDGUN" -> handgunWeapons
                    "DEMOLITION" -> demolitionWeapons
                    else -> emptyList()
                },
                optionText = { it.displayName },
                enabled = selectedType != null,
                expanded = weaponExpanded,
                onExpandedChange = {
                    weaponExpanded = it
                    if (it) typeExpanded = false
                },
                onSelected = { weapon ->
                    selectedWeapon = weapon
                    weaponExpanded = false
                }
            )
        }
    }
}

@Composable
fun AccesoriesScreen(onBack: () -> Unit) {
    val accesories = listOf(
        InventoryItem.DETON_A,
        InventoryItem.THUNDER_B,
        InventoryItem.TANTO,
        InventoryItem.MINI_KNIFE
    )
    var selected by remember { mutableStateOf(setOf<InventoryItem>()) }
    var expanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "CURRENT GEAR - ACCESORIES", onBack = onBack) {
        Box(modifier = Modifier.offset(y = (-12).dp)) {
            TerminalMultiSelect(
                label = "ACCESORIES",
                value = if (selected.isEmpty()) {
                    "SELECT ACCESORIES"
                } else {
                    selected.joinToString(" + ") { it.displayName }
                },
                options = accesories,
                optionText = { it.displayName },
                selected = selected,
                expanded = expanded,
                onExpandedChange = { expanded = it },
                onToggle = { item ->
                    selected = if (item in selected) selected - item else selected + item
                }
            )
        }
    }
}

private enum class HeadgearProfile(val displayName: String, val items: List<String>) {
    SURI_14("SURI-14", listOf("VYPER", "DYE MASK")),
    BROTHERHOOD("BROTHERHOOD", listOf("HELMET", "NVG", "GAS MASK", "SECURITY GOGLES"))
}

@Composable
fun HeadgearScreen(onBack: () -> Unit) {
    var selectedProfile by remember { mutableStateOf<HeadgearProfile?>(null) }
    var profileExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "CURRENT GEAR - HEADGEAR", onBack = onBack) {
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
                    selectedProfile = profile
                    profileExpanded = false
                }
            )
            selectedProfile?.let { profile ->
                TerminalVisualList(label = "ITEM", entries = profile.items)
            }
        }
    }
}

private enum class FrontPanelRole(val displayName: String, val panels: List<InventoryItem>) {
    SNIPER_ASSAULT(
        "SNIPER - ASSAULT",
        PrimaryWeaponRole.SNIPER.weapons + InventoryItem.MCX
    ),
    LIGHT_ASSAULT("LIGHT ASSAULT", listOf(InventoryItem.APC_9K)),
    DEMOLITION("DEMOLITION", PrimaryWeaponRole.DEMOLITION.weapons)
}

@Composable
fun FrontPanelScreen(onBack: () -> Unit) {
    var selectedRole by remember { mutableStateOf<FrontPanelRole?>(null) }
    var roleExpanded by remember { mutableStateOf(false) }

    CurrentGearLayout(title = "CURRENT GEAR - FRONT PANEL", onBack = onBack) {
        Row(horizontalArrangement = Arrangement.spacedBy(48.dp), verticalAlignment = Alignment.Top) {
            TerminalDropdown(
                label = "ROLE",
                value = selectedRole?.displayName ?: "SELECT ROLE",
                options = FrontPanelRole.entries.toList(),
                optionText = { it.displayName },
                expanded = roleExpanded,
                onExpandedChange = { roleExpanded = it },
                onSelected = { role ->
                    selectedRole = role
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
private fun TerminalVisualList(label: String, entries: List<String>) {
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
private fun <T> TerminalDropdown(
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
            Column(modifier = Modifier.width(560.dp).border(1.dp, PipGreen)) {
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
private fun CurrentGearLayout(title: String, onBack: () -> Unit, content: @Composable () -> Unit) {
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
            text = "PIP-SuriOS v1.4",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}
