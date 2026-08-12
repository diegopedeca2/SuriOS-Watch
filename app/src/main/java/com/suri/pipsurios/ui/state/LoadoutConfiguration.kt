package com.suri.pipsurios.ui.state

import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole

data class LoadoutConfiguration(
    val primaryRole: PrimaryWeaponRole? = null,
    val primaryWeapon: InventoryItem? = null,
    val secondaryType: String? = null,
    val secondaryWeapon: InventoryItem? = null,
    val accesories: Set<InventoryItem> = emptySet(),
    val headgearProfile: String? = null,
    val frontPanelRole: String? = null,
    val uniform: String? = null
)
