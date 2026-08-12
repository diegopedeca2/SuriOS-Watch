package com.suri.pipsurios.ui.state

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole

enum class ComplementCategory(val displayName: String) {
    BBS("BBs"),
    SLING("SLING"),
    HOLSTER("HOLSTER"),
    MAGS("MAGs"),
    AMMO("AMMO"),
    OTHER("OTHER")
}

data class ComplementSection(
    val category: ComplementCategory?,
    val values: List<String>
)

data class ComplementDefinition(val sections: List<ComplementSection>)

enum class ComplementRole(
    val displayName: String,
    val items: List<InventoryItem>
) {
    SNIPER("SNIPER", PrimaryWeaponRole.SNIPER.weapons),
    ASSAULT("ASSAULT", PrimaryWeaponRole.ASSAULT.weapons),
    HANDGUN("HANDGUN", listOf(InventoryItem.DESERT_EAGLE, InventoryItem.AAP_01C)),
    DEMOLITION("DEMOLITION", PrimaryWeaponRole.DEMOLITION.weapons),
    ACCESORIES(
        "ACCESORIES",
        listOf(
            InventoryItem.DETON_A,
            InventoryItem.THUNDER_B,
            InventoryItem.TANTO,
            InventoryItem.MINI_KNIFE,
            InventoryItem.VOLCANO
        )
    )
}

object ComplementCatalog {
    private val definitions = mapOf(
        key(ComplementRole.SNIPER, InventoryItem.L96) to definition(
            section(ComplementCategory.BBS, "0,40"),
            section(ComplementCategory.SLING, "2 POINT"),
            section(ComplementCategory.MAGS, "40 rds x4"),
            section(ComplementCategory.OTHER, "BIPOD")
        ),
        key(ComplementRole.SNIPER, InventoryItem.LEVAR_15) to definition(
            section(ComplementCategory.BBS, "0,45"),
            section(ComplementCategory.SLING, "2 POINT"),
            section(ComplementCategory.MAGS, "170 rds x4", "30 rds x1"),
            section(ComplementCategory.OTHER, "BIPOD", "HPA MAIN KIT")
        ),
        key(ComplementRole.ASSAULT, InventoryItem.MCX) to definition(
            section(ComplementCategory.BBS, "0,30 - NORMAL + TRACER"),
            section(ComplementCategory.SLING, "1 POINT"),
            section(ComplementCategory.MAGS, "170 rds x4"),
            section(
                ComplementCategory.OTHER,
                "MINI LAUNCHER", "DBAL A2", "SILENCER TRACER", "HPA MAIN KIT", "PRIMARY"
            )
        ),
        key(ComplementRole.ASSAULT, InventoryItem.APC_9K) to definition(
            section(ComplementCategory.BBS, "0,30 - NORMAL + TRACER"),
            section(ComplementCategory.SLING, "1 POINT"),
            section(ComplementCategory.MAGS, "170 rds x4"),
            section(
                ComplementCategory.OTHER,
                "MINI LAUNCHER", "DBAL A2", "TRACER", "HPA MAIN KIT"
            )
        ),
        key(ComplementRole.HANDGUN, InventoryItem.DESERT_EAGLE) to definition(
            section(ComplementCategory.BBS, "0,28"),
            section(ComplementCategory.HOLSTER, "SPECIFIC"),
            section(ComplementCategory.MAGS, "28 rds x3"),
            section(ComplementCategory.OTHER, "HPA SECONDARY KIT")
        ),
        key(ComplementRole.HANDGUN, InventoryItem.AAP_01C) to definition(
            section(ComplementCategory.BBS, "0,30 - NORMAL + TRACER"),
            section(ComplementCategory.HOLSTER, "SPECIFIC"),
            section(ComplementCategory.MAGS, "28 rds x3"),
            section(ComplementCategory.OTHER, "FLASHLIGHT", "TRACER", "HPA SECONDARY KIT")
        ),
        key(ComplementRole.DEMOLITION, InventoryItem.MGL) to definition(
            section(ComplementCategory.BBS, "0,20"),
            section(ComplementCategory.SLING, "1 POINT"),
            section(ComplementCategory.AMMO, "40mm GRENADES x11"),
            section(ComplementCategory.OTHER, "DBAL A2", "GAS")
        ),
        key(ComplementRole.DEMOLITION, InventoryItem.VOLCANO) to definition(
            section(ComplementCategory.BBS, "0,20 NORMAL + TRACER"),
            section(ComplementCategory.SLING, "1 POINT"),
            section(ComplementCategory.HOLSTER, "SPECIFIC"),
            section(ComplementCategory.AMMO, "40mm GRENADES x11"),
            section(ComplementCategory.OTHER, "DBAL A2", "GAS")
        ),
        key(ComplementRole.ACCESORIES, InventoryItem.DETON_A) to definition(
            ComplementSection(null, listOf("9mm CARTRIDGES"))
        ),
        key(ComplementRole.ACCESORIES, InventoryItem.THUNDER_B) to definition(
            ComplementSection(null, listOf("CO2 VIALS", "CASINGS"))
        )
    )

    fun forSelection(role: ComplementRole?, item: InventoryItem?): ComplementDefinition? =
        if (role == null || item == null) null else definitions[key(role, item)]

    fun reminderLines(loadout: LoadoutConfiguration): List<String> {
        val selectedDefinitions = buildList {
            primaryRole(loadout.primaryRole)?.let { role ->
                forSelection(role, loadout.primaryWeapon)?.let(::add)
            }
            secondaryRole(loadout.secondaryType)?.let { role ->
                forSelection(role, loadout.secondaryWeapon)?.let(::add)
            }
            loadout.accesories.forEach { item ->
                forSelection(ComplementRole.ACCESORIES, item)?.let(::add)
            }
        }

        return selectedDefinitions
            .flatMap { definition -> definition.sections.flatMap(::reminderLines) }
            .distinct()
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
    }

    private fun reminderLines(section: ComplementSection): List<String> = section.values.map { value ->
        when (section.category) {
            ComplementCategory.BBS,
            ComplementCategory.SLING,
            ComplementCategory.HOLSTER,
            ComplementCategory.MAGS,
            ComplementCategory.AMMO -> "${section.category.displayName}: $value"
            ComplementCategory.OTHER, null -> value
        }
    }

    private fun primaryRole(role: PrimaryWeaponRole?): ComplementRole? = when (role) {
        PrimaryWeaponRole.SNIPER -> ComplementRole.SNIPER
        PrimaryWeaponRole.ASSAULT -> ComplementRole.ASSAULT
        PrimaryWeaponRole.DEMOLITION -> ComplementRole.DEMOLITION
        null -> null
    }

    private fun secondaryRole(type: String?): ComplementRole? = when (type) {
        "HANDGUN" -> ComplementRole.HANDGUN
        "DEMOLITION" -> ComplementRole.DEMOLITION
        else -> null
    }

    private fun key(role: ComplementRole, item: InventoryItem) = role to item
    private fun definition(vararg sections: ComplementSection) = ComplementDefinition(sections.toList())
    private fun section(category: ComplementCategory, vararg values: String) =
        ComplementSection(category, values.toList())
}
