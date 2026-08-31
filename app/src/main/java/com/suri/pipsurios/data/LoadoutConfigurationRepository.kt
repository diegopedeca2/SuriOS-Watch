package com.suri.pipsurios.data

import android.content.Context
import androidx.core.content.edit
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.state.LoadoutConfiguration

/** Durable SET-UP template consumed as the initial source for CURRENT GEAR. */
class LoadoutConfigurationRepository private constructor(
    private val preferences: android.content.SharedPreferences
) {
    fun load(): LoadoutConfiguration = LoadoutConfiguration(
        primaryRole = readEnum<PrimaryWeaponRole>(KEY_PRIMARY_ROLE),
        primaryWeapon = readEnum<InventoryItem>(KEY_PRIMARY_WEAPON),
        primaryWeaponText = preferences.getString(KEY_PRIMARY_WEAPON_TEXT, null)
            ?.trim()
            ?.takeIf { it.isNotEmpty() },
        secondaryType = preferences.getString(KEY_SECONDARY_TYPE, null),
        secondaryWeapon = readEnum<InventoryItem>(KEY_SECONDARY_WEAPON),
        accesories = preferences.getStringSet(KEY_ACCESORIES, emptySet())
            .orEmpty()
            .mapNotNull { value -> enumValueOrNull<InventoryItem>(value) }
            .toSet(),
        headgearProfile = preferences.getString(KEY_HEADGEAR_PROFILE, null),
        frontPanelRole = preferences.getString(KEY_FRONT_PANEL_ROLE, null),
        uniform = preferences.getString(KEY_UNIFORM, null)
    )

    fun save(configuration: LoadoutConfiguration) {
        preferences.edit {
            putString(KEY_PRIMARY_ROLE, configuration.primaryRole?.name)
            putString(KEY_PRIMARY_WEAPON, configuration.primaryWeapon?.name)
            putString(KEY_PRIMARY_WEAPON_TEXT, configuration.primaryWeaponText?.trim())
            putString(KEY_SECONDARY_TYPE, configuration.secondaryType)
            putString(KEY_SECONDARY_WEAPON, configuration.secondaryWeapon?.name)
            putStringSet(KEY_ACCESORIES, configuration.accesories.map { it.name }.toSet())
            putString(KEY_HEADGEAR_PROFILE, configuration.headgearProfile)
            putString(KEY_FRONT_PANEL_ROLE, configuration.frontPanelRole)
            putString(KEY_UNIFORM, configuration.uniform)
        }
    }

    private inline fun <reified T : Enum<T>> readEnum(key: String): T? =
        preferences.getString(key, null)?.let { value -> enumValueOrNull<T>(value) }

    private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
        enumValues<T>().firstOrNull { it.name == value }

    companion object {
        private const val PREFERENCES_NAME = "loadout_setup"
        private const val KEY_PRIMARY_ROLE = "primary_role"
        private const val KEY_PRIMARY_WEAPON = "primary_weapon"
        private const val KEY_PRIMARY_WEAPON_TEXT = "primary_weapon_text"
        private const val KEY_SECONDARY_TYPE = "secondary_type"
        private const val KEY_SECONDARY_WEAPON = "secondary_weapon"
        private const val KEY_ACCESORIES = "accesories"
        private const val KEY_HEADGEAR_PROFILE = "headgear_profile"
        private const val KEY_FRONT_PANEL_ROLE = "front_panel_role"
        private const val KEY_UNIFORM = "uniform"

        fun from(context: Context): LoadoutConfigurationRepository =
            LoadoutConfigurationRepository(
                context.applicationContext.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                )
            )
    }
}
