package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.state.ComplementCatalog
import com.suri.pipsurios.ui.state.ComplementCategory
import com.suri.pipsurios.ui.state.ComplementDefinition
import com.suri.pipsurios.ui.state.ComplementRole
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed

@Composable
fun ComplementsScreen(onBack: () -> Unit) {
    var selectedRole by remember { mutableStateOf<ComplementRole?>(null) }
    var selectedWeapon by remember { mutableStateOf<InventoryItem?>(null) }
    var roleExpanded by remember { mutableStateOf(false) }
    var weaponExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(PipBlack)) {
        Text(
            text = "INVENTORY - COMPLEMENTS",
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp)
        )

        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.Top
        ) {
            TerminalDropdown(
                label = "ROLE",
                value = selectedRole?.displayName ?: "SELECT ROLE",
                options = ComplementRole.entries.toList(),
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
                options = selectedRole?.items.orEmpty(),
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
            selectedWeapon?.let { weapon ->
                ComplementCatalog.forSelection(selectedRole, weapon)?.let { definition ->
                    ComplementVisualList(definition = definition)
                } ?: Text(
                    text = "UNDER CONSTRUCTION",
                    color = PipRed,
                    fontSize = 24.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Text(
            text = "< BACK",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomStart).clickable(onClick = onBack).padding(24.dp)
        )
        Text(
            text = "PIP-SuriOS v2.0",
            color = PipGreenDim,
            fontSize = 18.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp)
        )
    }
}

@Composable
private fun ComplementVisualList(definition: ComplementDefinition) {
    Column(
        modifier = Modifier
            .width(250.dp)
            .heightIn(max = 250.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("COMPLEMENTS", color = PipGreen, fontSize = 20.sp, fontFamily = FontFamily.Monospace)
        definition.sections.forEach { section ->
            section.category?.let { category ->
                Text(
                    text = "${category.displayName}:",
                    color = PipGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
            section.values.forEach { value ->
                val prefix = when (section.category) {
                    ComplementCategory.MAGS, ComplementCategory.OTHER, null -> "> "
                    else -> ""
                }
                Text(
                    text = "$prefix$value",
                    color = PipGreen,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
