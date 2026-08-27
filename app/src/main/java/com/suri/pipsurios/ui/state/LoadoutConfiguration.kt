package com.suri.pipsurios.ui.state

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole

data class LoadoutConfiguration(
    val primaryRole: PrimaryWeaponRole? = null,
    val primaryWeapon: InventoryItem? = null,
    /** Free-form replica name used by SET-UP for custom primary combinations. */
    val primaryWeaponText: String? = null,
    val secondaryType: String? = null,
    val secondaryWeapon: InventoryItem? = null,
    val accesories: Set<InventoryItem> = emptySet(),
    val headgearProfile: String? = null,
    val frontPanelRole: String? = null,
    val uniform: String? = null
) {
    fun primaryWeaponDisplayName(): String? =
        primaryWeaponText?.trim()?.takeIf { it.isNotEmpty() }
            ?: primaryWeapon?.displayName
}
