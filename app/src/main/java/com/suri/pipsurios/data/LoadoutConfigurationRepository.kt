package com.suri.pipsurios.data

import android.content.Context
import androidx.core.content.edit
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.HeadgearProfile
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
import com.suri.pipsurios.ui.state.GearCatalogDefaults
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import org.json.JSONArray

/** Durable SET-UP template consumed as the initial source for CURRENT GEAR. */
class LoadoutConfigurationRepository private constructor(
    private val preferences: android.content.SharedPreferences
) {
    fun load(): LoadoutConfiguration {
        val primaryRole = readEnum<PrimaryWeaponRole>(KEY_PRIMARY_ROLE)
        val primaryWeapon = readEnum<InventoryItem>(KEY_PRIMARY_WEAPON)
        val primaryRoleText = preferences.getString(KEY_PRIMARY_ROLE_TEXT, null)
        val primaryModelText = preferences.getString(KEY_PRIMARY_MODEL_TEXT, null)
            ?: preferences.getString(KEY_PRIMARY_WEAPON_TEXT, null)
        val secondaryType = preferences.getString(KEY_SECONDARY_TYPE, null)
        val secondaryWeapon = readEnum<InventoryItem>(KEY_SECONDARY_WEAPON)
        val secondaryTypeText = preferences.getString(KEY_SECONDARY_TYPE_TEXT, null)
        val secondaryModelText = preferences.getString(KEY_SECONDARY_MODEL_TEXT, null)
        val legacyPrimary = listOf(
            primaryRoleText ?: primaryRole?.displayName,
            primaryModelText ?: primaryWeapon?.displayName
        ).filterNotNull().filter { it.isNotBlank() }.joinToString(" - ")
        val legacySecondary = listOf(
            secondaryTypeText ?: secondaryType,
            secondaryModelText ?: secondaryWeapon?.displayName
        ).filterNotNull().filter { it.isNotBlank() }.joinToString(" - ")

        return LoadoutConfiguration(
            primaryRole = primaryRole,
            primaryWeapon = primaryWeapon,
            primaryRoleText = primaryRoleText,
            primaryModelText = primaryModelText,
            primaryWeaponOptions = readList(
                KEY_PRIMARY_WEAPON_OPTIONS,
                legacyPrimary.takeIf { it.isNotBlank() }?.let(::listOf).orEmpty()
            ),
            secondaryType = secondaryType,
            secondaryWeapon = secondaryWeapon,
            secondaryTypeText = secondaryTypeText,
            secondaryModelText = secondaryModelText,
            secondaryWeaponOptions = readList(
                KEY_SECONDARY_WEAPON_OPTIONS,
                legacySecondary.takeIf { it.isNotBlank() }?.let(::listOf).orEmpty()
            ),
            accesories = preferences.getStringSet(KEY_ACCESORIES, emptySet())
                .orEmpty()
                .mapNotNull { value -> enumValueOrNull<InventoryItem>(value) }
                .toSet(),
            headgearProfile = preferences.getString(KEY_HEADGEAR_PROFILE, null),
            frontPanelRole = preferences.getString(KEY_FRONT_PANEL_ROLE, null),
            uniform = preferences.getString(KEY_UNIFORM, null),
            customAccesories = preferences.getStringSet(KEY_CUSTOM_ACCESORIES, emptySet()).orEmpty(),
            accesoryOptions = readList(KEY_ACCESORY_OPTIONS, GearCatalogDefaults.accesoryOptions),
            frontPanelOptions = readList(KEY_FRONT_PANEL_OPTIONS, GearCatalogDefaults.frontPanelOptions),
            uniformOptions = readList(KEY_UNIFORM_OPTIONS, GearCatalogDefaults.uniformOptions),
            headgearComponents = if (preferences.contains(KEY_HEADGEAR_COMPONENTS)) {
                preferences.getStringSet(KEY_HEADGEAR_COMPONENTS, emptySet()).orEmpty()
            } else {
                HeadgearProfile.entries
                    .firstOrNull { it.displayName == preferences.getString(KEY_HEADGEAR_PROFILE, null) }
                    ?.items
                    ?.toSet()
                    .orEmpty()
            }
        )
    }

    fun save(configuration: LoadoutConfiguration) {
        preferences.edit {
            putString(KEY_PRIMARY_ROLE, configuration.primaryRole?.name)
            putString(KEY_PRIMARY_WEAPON, configuration.primaryWeapon?.name)
            putString(KEY_PRIMARY_ROLE_TEXT, configuration.primaryRoleText)
            putString(KEY_PRIMARY_MODEL_TEXT, configuration.primaryModelText)
            remove(KEY_PRIMARY_WEAPON_TEXT)
            putString(KEY_PRIMARY_WEAPON_OPTIONS, encodeList(configuration.primaryWeaponOptions))
            putString(KEY_SECONDARY_TYPE, configuration.secondaryType)
            putString(KEY_SECONDARY_WEAPON, configuration.secondaryWeapon?.name)
            putString(KEY_SECONDARY_TYPE_TEXT, configuration.secondaryTypeText)
            putString(KEY_SECONDARY_MODEL_TEXT, configuration.secondaryModelText)
            putString(KEY_SECONDARY_WEAPON_OPTIONS, encodeList(configuration.secondaryWeaponOptions))
            putStringSet(KEY_ACCESORIES, configuration.accesories.map { it.name }.toSet())
            putString(KEY_HEADGEAR_PROFILE, configuration.headgearProfile)
            putString(KEY_FRONT_PANEL_ROLE, configuration.frontPanelRole)
            putString(KEY_UNIFORM, configuration.uniform)
            putStringSet(KEY_CUSTOM_ACCESORIES, configuration.customAccesories)
            putString(KEY_ACCESORY_OPTIONS, encodeList(configuration.accesoryOptions))
            putString(KEY_FRONT_PANEL_OPTIONS, encodeList(configuration.frontPanelOptions))
            putString(KEY_UNIFORM_OPTIONS, encodeList(configuration.uniformOptions))
            putStringSet(KEY_HEADGEAR_COMPONENTS, configuration.headgearComponents)
        }
    }

    private fun readList(key: String, fallback: List<String>): List<String> =
        preferences.getString(key, null)?.let { encoded ->
            runCatching {
                val values = JSONArray(encoded)
                (0 until values.length())
                    .map { values.optString(it).trim() }
                    .filter { it.isNotEmpty() }
                    .distinct()
            }.getOrElse { fallback }
        } ?: fallback

    private fun encodeList(values: List<String>): String =
        JSONArray(values.map { it.trim() }.filter { it.isNotEmpty() }.distinct()).toString()

    private inline fun <reified T : Enum<T>> readEnum(key: String): T? =
        preferences.getString(key, null)?.let { value -> enumValueOrNull<T>(value) }

    private inline fun <reified T : Enum<T>> enumValueOrNull(value: String): T? =
        enumValues<T>().firstOrNull { it.name == value }

    companion object {
        private const val PREFERENCES_NAME = "loadout_setup"
        private const val KEY_PRIMARY_ROLE = "primary_role"
        private const val KEY_PRIMARY_WEAPON = "primary_weapon"
        private const val KEY_PRIMARY_ROLE_TEXT = "primary_role_text"
        private const val KEY_PRIMARY_MODEL_TEXT = "primary_model_text"
        private const val KEY_PRIMARY_WEAPON_TEXT = "primary_weapon_text"
        private const val KEY_PRIMARY_WEAPON_OPTIONS = "primary_weapon_options"
        private const val KEY_SECONDARY_TYPE = "secondary_type"
        private const val KEY_SECONDARY_WEAPON = "secondary_weapon"
        private const val KEY_SECONDARY_TYPE_TEXT = "secondary_type_text"
        private const val KEY_SECONDARY_MODEL_TEXT = "secondary_model_text"
        private const val KEY_SECONDARY_WEAPON_OPTIONS = "secondary_weapon_options"
        private const val KEY_ACCESORIES = "accesories"
        private const val KEY_HEADGEAR_PROFILE = "headgear_profile"
        private const val KEY_FRONT_PANEL_ROLE = "front_panel_role"
        private const val KEY_UNIFORM = "uniform"
        private const val KEY_CUSTOM_ACCESORIES = "custom_accesories"
        private const val KEY_ACCESORY_OPTIONS = "accesory_options"
        private const val KEY_FRONT_PANEL_OPTIONS = "front_panel_options"
        private const val KEY_UNIFORM_OPTIONS = "uniform_options"
        private const val KEY_HEADGEAR_COMPONENTS = "headgear_components"

        fun from(context: Context): LoadoutConfigurationRepository =
            LoadoutConfigurationRepository(
                context.applicationContext.getSharedPreferences(
                    PREFERENCES_NAME,
                    Context.MODE_PRIVATE
                )
            )
    }
}
