package com.suri.pipsurios.ui.state

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole

object GearCatalogDefaults {
    val accesoryOptions = listOf(
        "DETON-A",
        "THUNDER B",
        "TANTO",
        "MINI KNIFE",
        "VOLCANO",
        "WATCH 2"
    )
    val frontPanelOptions = listOf("SNIPER - ASSAULT", "LIGHT ASSAULT", "DEMOLITION")
    val uniformOptions = listOf("MCBCK - SUMMER", "MCBCK - LONG", "DESERT")
}

data class LoadoutConfiguration(
    val primaryRole: PrimaryWeaponRole? = null,
    val primaryWeapon: InventoryItem? = null,
    val primaryRoleText: String? = null,
    val primaryModelText: String? = null,
    val primaryWeaponOptions: List<String> = emptyList(),
    val secondaryType: String? = null,
    val secondaryWeapon: InventoryItem? = null,
    val secondaryTypeText: String? = null,
    val secondaryModelText: String? = null,
    val secondaryWeaponOptions: List<String> = emptyList(),
    val accesories: Set<InventoryItem> = emptySet(),
    val headgearProfile: String? = null,
    val frontPanelRole: String? = null,
    val uniform: String? = null,
    val customAccesories: Set<String> = emptySet(),
    val accesoryOptions: List<String> = GearCatalogDefaults.accesoryOptions,
    val frontPanelOptions: List<String> = GearCatalogDefaults.frontPanelOptions,
    val uniformOptions: List<String> = GearCatalogDefaults.uniformOptions,
    val headgearComponents: Set<String> = emptySet()
) {
    fun primaryRoleInput(): String =
        primaryRoleText?.trim()?.takeIf { it.isNotEmpty() } ?: primaryRole?.displayName.orEmpty()

    fun primaryModelInput(): String =
        primaryModelText?.trim()?.takeIf { it.isNotEmpty() } ?: primaryWeapon?.displayName.orEmpty()

    fun secondaryTypeInput(): String =
        secondaryTypeText?.trim()?.takeIf { it.isNotEmpty() } ?: secondaryType.orEmpty()

    fun secondaryModelInput(): String =
        secondaryModelText?.trim()?.takeIf { it.isNotEmpty() } ?: secondaryWeapon?.displayName.orEmpty()

    fun primaryWeaponDisplayName(): String? =
        listOf(primaryRoleInput(), primaryModelInput())
            .filter { it.isNotBlank() }
            .joinToString(" - ")
            .takeIf { it.isNotBlank() }

    fun secondaryWeaponDisplayName(): String? =
        listOf(secondaryTypeInput(), secondaryModelInput())
            .filter { it.isNotBlank() }
            .joinToString(" - ")
            .takeIf { it.isNotBlank() }

    fun withPrimaryWeaponInputs(role: String, model: String): LoadoutConfiguration {
        val cleanRole = role.trim()
        val cleanModel = model.trim()
        return copy(
            primaryRoleText = cleanRole.takeIf { it.isNotEmpty() },
            primaryModelText = cleanModel.takeIf { it.isNotEmpty() },
            primaryRole = PrimaryWeaponRole.entries.firstOrNull {
                it.displayName.equals(cleanRole, ignoreCase = true)
            },
            primaryWeapon = InventoryItem.entries.firstOrNull {
                it.displayName.equals(cleanModel, ignoreCase = true)
            }
        )
    }

    fun withSecondaryWeaponInputs(type: String, model: String): LoadoutConfiguration {
        val cleanType = type.trim()
        val cleanModel = model.trim()
        return copy(
            secondaryTypeText = cleanType.takeIf { it.isNotEmpty() },
            secondaryModelText = cleanModel.takeIf { it.isNotEmpty() },
            secondaryType = cleanType.takeIf { it.isNotEmpty() },
            secondaryWeapon = InventoryItem.entries.firstOrNull {
                it.displayName.equals(cleanModel, ignoreCase = true)
            }
        )
    }

    fun withPrimaryWeaponOption(option: String): LoadoutConfiguration {
        val parts = option.split(" - ", limit = 2).map(String::trim)
        val role = parts.getOrNull(0).orEmpty()
        val model = parts.getOrNull(1).orEmpty()
        return withPrimaryWeaponInputs(role, model)
    }

    fun withSecondaryWeaponOption(option: String): LoadoutConfiguration {
        val parts = option.split(" - ", limit = 2).map(String::trim)
        val type = parts.getOrNull(0).orEmpty()
        val model = parts.getOrNull(1).orEmpty()
        return withSecondaryWeaponInputs(type, model)
    }
}
