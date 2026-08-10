package com.suri.pipsurios.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.suri.pipsurios.ui.theme.PipBlack
import com.suri.pipsurios.ui.theme.PipAmber
import com.suri.pipsurios.ui.theme.PipGreen
import com.suri.pipsurios.ui.theme.PipGreenDim
import com.suri.pipsurios.ui.theme.PipRed

@Composable
fun InventoryScreen(
    onBack: () -> Unit,
    onSniperSelected: () -> Unit,
    onAssaultSelected: () -> Unit,
    onDemolitionSelected: () -> Unit,
    onHandgunSelected: () -> Unit,
    onAccesoriesSelected: () -> Unit,
    onComplementsSelected: () -> Unit
) {
    InventoryLayout(title = "INVENTORY - ARMORY", onBack = onBack) {
        InventoryEntries(
            entries = listOf(
                "> SNIPER" to onSniperSelected,
                "> ASSAULT" to onAssaultSelected,
                "> DEMOLITION" to onDemolitionSelected,
                "> HANDGUN" to onHandgunSelected,
                "> ACCESORIES" to onAccesoriesSelected,
                "> COMPLEMENTS" to onComplementsSelected
            ),
            compact = true
        )
    }
}

@Composable
fun InventoryModeSelectionScreen(
    onArmorySelected: () -> Unit,
    onConsumablesSelected: () -> Unit,
    onLoadoutsSelected: () -> Unit,
    onBack: () -> Unit
) {
    InventoryLayout(title = "INVENTORY SELECT MODE", onBack = onBack) {
        InventoryEntries(
            entries = listOf(
                "> ARMORY" to onArmorySelected,
                "> CONSUMABLES" to onConsumablesSelected,
                "> LOADOUTS" to onLoadoutsSelected
            )
        )
    }
}

@Composable
fun InventoryVisualMenuScreen(
    title: String,
    entries: List<String>,
    entryActions: Map<String, () -> Unit> = emptyMap(),
    scrollable: Boolean = false,
    onBack: () -> Unit
) {
    InventoryLayout(title = title, onBack = onBack) {
        InventoryEntries(
            entries = entries.map { it to entryActions[it] },
            scrollable = scrollable
        )
    }
}

@Composable
fun InventoryCategoryScreen(
    title: String,
    entries: List<String>,
    entryActions: Map<String, () -> Unit> = emptyMap(),
    onBack: () -> Unit
) {
    InventoryLayout(title = title, onBack = onBack) {
        InventoryEntries(entries.map { it to entryActions[it] })
    }
}

enum class InventoryItem(val displayName: String) {
    L96("L96"),
    LEVAR_15("LevAR-15"),
    MCX("MCX"),
    APC_9K("APC-9K"),
    MGL("MGL"),
    VOLCANO("VOLCANO"),
    DESERT_EAGLE("DESERT EAGLE"),
    AAP_01C("AAP-01C"),
    DETON_A("DETON-A"),
    THUNDER_B("THUNDER B"),
    TANTO("TANTO"),
    MINI_KNIFE("MINI KNIFE")
}

enum class PrimaryWeaponRole(
    val displayName: String,
    val weapons: List<InventoryItem>
) {
    SNIPER("SNIPER", listOf(InventoryItem.L96, InventoryItem.LEVAR_15)),
    ASSAULT("ASSAULT", listOf(InventoryItem.MCX, InventoryItem.APC_9K)),
    DEMOLITION("DEMOLITION", listOf(InventoryItem.MGL, InventoryItem.VOLCANO))
}

private data class InventoryDetailLine(
    val text: String,
    val color: Color = PipGreen
)

private data class InventoryDetailSection(
    val heading: String,
    val lines: List<InventoryDetailLine>
)

private data class InventoryItemDetails(
    val title: String,
    val sections: List<InventoryDetailSection> = emptyList(),
    val underConstruction: Boolean = false
)

@Composable
fun InventoryDetailsScreen(item: InventoryItem, onBack: () -> Unit) {
    val details = inventoryItemDetails(item)

    InventoryLayout(title = details.title, onBack = onBack) {
        if (details.underConstruction) {
            Text(
                text = "UNDER CONSTRUCTION",
                color = PipRed,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace
            )
        } else {
            Column(
                modifier = Modifier
                    .heightIn(max = 260.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.Start
            ) {
                details.sections.forEach { section ->
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InventoryDetailText(section.heading)
                        section.lines.forEach { line ->
                            InventoryDetailText(line.text, line.color)
                        }
                    }
                }
            }
        }
    }
}

private fun inventoryItemDetails(item: InventoryItem): InventoryItemDetails = when (item) {
    InventoryItem.L96 -> InventoryItemDetails(
        title = "SNIPER - L96",
        sections = listOf(
            section("SIGHT:", red("PENDING REPLACEMENT")),
            section("AMMUNITION:", green("0,40"), red("OUT OF AMMO")),
            section(
                "MAGAZINES:",
                green("> 40 BBs - 4"),
                green("> 20 BBs - 4"),
                green("SPRING LOADED"),
                green("JOULES - 2,8J")
            )
        )
    )

    InventoryItem.LEVAR_15 -> InventoryItemDetails(
        title = "SNIPER - LevAR-15",
        sections = listOf(
            section("SIGHT:", red("PENDING REPLACEMENT")),
            section("AMMUNITION:", green("0,45"), amber("LOW")),
            section("MAGAZINES:", green("> 170 BBs - 4"), green("> 30 BBs - 1")),
            section("PSI:", red("TBD")),
            section("JOULES:", green("2,8J"))
        )
    )

    InventoryItem.MCX -> InventoryItemDetails(
        title = "ASSAULT - MCX",
        sections = listOf(
            section("SIGHT:", green("SHORT-DOT")),
            section("AMMUNITION:", green("> 0,30 NORMAL"), green("> 0,30 TRACER")),
            section("MAGAZINES:", green("> 170 BBs - 4"), green("> 30 BBs - 1 - STORED")),
            section("PSI:", green("95")),
            section("JOULES:", green("1,1J")),
            section(
                "ACCESORIES:",
                green("> DBAL-A2"),
                green("> MINI LAUNCHER"),
                green("> SILENCER + TRACER UNIT")
            )
        )
    )

    InventoryItem.APC_9K -> InventoryItemDetails(
        title = "ASSAULT - APC-9K",
        sections = listOf(
            section("SIGHT:", green("HOLO + MAGNIFIER")),
            section("AMMUNITION:", green("> 0,30 NORMAL"), green("> 0,30 TRACER")),
            section(
                "MAGAZINES:",
                green("> 50 BBs HPA - 4"),
                green("> 50 BBs GAS - 1 - STORED"),
                green("> 28 BBs HPA - 4"),
                green("> 28 BBs GAS - 2 - STORED")
            ),
            section("PSI:", green("100")),
            section("JOULES:", green("1,0J")),
            section(
                "ACCESORIES:",
                green("> DBAL-A2"),
                green("> MINI LAUNCHER"),
                green("> TRACER UNIT")
            )
        )
    )

    InventoryItem.MGL -> InventoryItemDetails(
        title = "DEMOLITION - MGL",
        sections = listOf(
            section("SIGHT:", green("GRENADE LAUNCHER 3D PRINT")),
            section("AMMUNITION:", green("> 0,20 NORMAL")),
            section("GRENADES:", green("11")),
            section("PSI:", green("N/A")),
            section("JOULES:", green("N/A")),
            section("ACCESORIES:", green("> DBAL-A2"))
        )
    )

    InventoryItem.VOLCANO -> InventoryItemDetails(
        title = "DEMOLITION - VOLCANO",
        sections = listOf(
            section("SIGHT:", green("GRENADE LAUNCHER 3D PRINT")),
            section("AMMUNITION:", green("> 0,20 NORMAL"), green("> 0,20 TRACER")),
            section("GRENADES:", green("11")),
            section("PSI:", green("N/A")),
            section("JOULES:", green("N/A")),
            section("ACCESORIES:", green("> DBAL-A2"), green("> MINI LAUNCHER"))
        )
    )

    InventoryItem.DESERT_EAGLE -> InventoryItemDetails(
        title = "HANDGUN - DESERT EAGLE",
        sections = listOf(
            section("SIGHT:", green("N/A")),
            section("AMMUNITION:", green("> 0,28 NORMAL")),
            section("MAGAZINES:", green("> 28 BBs HPA - 3")),
            section("PSI:", green("70")),
            section("JOULES:", green("0,9J"))
        )
    )

    InventoryItem.AAP_01C -> InventoryItemDetails(
        title = "HANDGUN - AAP-01C",
        sections = listOf(
            section("SIGHT:", green("ACRO P-2")),
            section("AMMUNITION:", green("> 0,30 NORMAL"), green("> 0,30 TRACER")),
            section(
                "MAGAZINES:",
                green("> 50 BBs HPA - 1 - STORED"),
                green("> 28 BBs HPA - 3")
            ),
            section("PSI:", amber("TBD")),
            section("JOULES:", green("1J")),
            section("ACCESORIES:", green("> FLASHLIGHT"), green("> TRACER UNIT"))
        )
    )

    InventoryItem.DETON_A -> InventoryItemDetails(
        title = "ACCESORIES - DETON-A",
        sections = listOf(
            section("UNITS AVAILABLE:", green("3")),
            section("BACKUP:", green("27"))
        )
    )

    InventoryItem.THUNDER_B -> InventoryItemDetails(
        title = "ACCESORIES - THUNDER B",
        sections = listOf(
            section("UNITS AVAILABLE:", green("2")),
            section("BACKUP:", green("5"))
        )
    )

    InventoryItem.TANTO -> InventoryItemDetails(
        title = "ACCESORIES - TANTO",
        underConstruction = true
    )

    InventoryItem.MINI_KNIFE -> InventoryItemDetails(
        title = "ACCESORIES - MINI KNIFE",
        underConstruction = true
    )
}

private fun section(
    heading: String,
    vararg lines: InventoryDetailLine
) = InventoryDetailSection(heading, lines.toList())

private fun green(text: String) = InventoryDetailLine(text)

private fun red(text: String) = InventoryDetailLine(text, PipRed)

private fun amber(text: String) = InventoryDetailLine(text, PipAmber)

@Composable
private fun InventoryLayout(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PipBlack)
    ) {
        Text(
            text = title,
            color = PipGreen,
            fontSize = 30.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(24.dp)
        )

        Box(modifier = Modifier.align(Alignment.Center)) {
            content()
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
            text = "PIP-SuriOS v1.7",
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
private fun InventoryEntries(
    entries: List<Pair<String, (() -> Unit)?>>,
    scrollable: Boolean = false,
    compact: Boolean = false
) {
    val entriesModifier = if (scrollable) {
        Modifier
            .heightIn(max = 260.dp)
            .verticalScroll(rememberScrollState())
    } else {
        Modifier
    }

    Column(
        modifier = entriesModifier,
        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 18.dp),
        horizontalAlignment = Alignment.Start
    ) {
        entries.forEach { (text, onClick) ->
            Text(
                text = text,
                color = PipGreen,
                fontSize = 24.sp,
                fontFamily = FontFamily.Monospace,
                modifier = if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            )
        }
    }
}

@Composable
private fun InventoryDetailText(
    text: String,
    color: Color = PipGreen
) {
    Text(
        text = text,
        color = color,
        fontSize = 20.sp,
        fontFamily = FontFamily.Monospace
    )
}
