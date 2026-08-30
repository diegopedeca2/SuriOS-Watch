package com.suri.pipsurios.prs

import android.content.Context
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.util.Locale

enum class PrsDeviceRuleType(val label: String) {
    ADDRESS("BLE ADDRESS"),
    ADVERTISED_NAME("BLE NAME")
}

data class PrsSavedDevice(
    val type: PrsDeviceRuleType,
    val value: String,
    val displayName: String? = null,
    val enabled: Boolean = true
)

/** Persistent device rules used before a BLE observation enters P.R.S. tracking. */
class PrsDeviceRegistry private constructor(
    private val preferences: android.content.SharedPreferences
) {
    private val savedDevices = loadDevices().toMutableList()

    @Synchronized
    fun snapshot(): List<PrsSavedDevice> = savedDevices
        .sortedWith(compareBy({ it.displayName ?: "" }, { it.type.ordinal }, { it.value }))

    @Synchronized
    fun isIgnored(observation: BleObservation): Boolean =
        savedDevices.any { it.enabled && it.matches(observation) }

    @Synchronized
    fun savedDeviceFor(observation: BleObservation): PrsSavedDevice? =
        savedDevices.firstOrNull { it.enabled && it.matches(observation) }
            ?: savedDevices.firstOrNull { it.matches(observation) }

    @Synchronized
    fun saveAddress(rawAddress: String, displayName: String? = null): Boolean {
        val address = normalizeAddress(rawAddress) ?: return false
        return save(
            type = PrsDeviceRuleType.ADDRESS,
            value = address,
            displayName = cleanDisplayName(displayName)
        )
    }

    @Synchronized
    fun saveName(rawName: String, displayName: String? = rawName): Boolean {
        val name = normalizeName(rawName) ?: return false
        return save(
            type = PrsDeviceRuleType.ADVERTISED_NAME,
            value = name,
            displayName = cleanDisplayName(displayName) ?: name
        )
    }

    @Synchronized
    fun setEnabled(device: PrsSavedDevice, enabled: Boolean): Boolean {
        val index = savedDevices.indexOfFirst { it.sameKeyAs(device) }
        if (index < 0 || savedDevices[index].enabled == enabled) return false
        savedDevices[index] = savedDevices[index].copy(enabled = enabled)
        persist()
        return true
    }

    @Synchronized
    fun remove(device: PrsSavedDevice): Boolean {
        val removed = savedDevices.removeAll { it.sameKeyAs(device) }
        if (!removed) return false
        persist()
        return true
    }

    private fun save(
        type: PrsDeviceRuleType,
        value: String,
        displayName: String?
    ): Boolean {
        val index = savedDevices.indexOfFirst { it.type == type && it.value == value }
        if (index >= 0) {
            val current = savedDevices[index]
            val updated = current.copy(displayName = displayName ?: current.displayName)
            if (updated == current) return false
            savedDevices[index] = updated
        } else {
            savedDevices += PrsSavedDevice(type, value, displayName, enabled = true)
        }
        persist()
        return true
    }

    private fun loadDevices(): List<PrsSavedDevice> {
        val current = preferences
            .getStringSet(KEY_SAVED_DEVICES, emptySet())
            .orEmpty()
            .mapNotNull(::decodeDevice)
        val legacy = preferences
            .getStringSet(KEY_IGNORED_RULES, emptySet())
            .orEmpty()
            .mapNotNull(::decodeLegacyRule)
        val merged = (current + legacy).distinctBy { it.type to it.value }
        if (legacy.isNotEmpty()) {
            preferences.edit()
                .putStringSet(KEY_SAVED_DEVICES, merged.map(::encodeDevice).toSet())
                .remove(KEY_IGNORED_RULES)
                .apply()
        }
        return merged
    }

    private fun persist() {
        preferences.edit()
            .putStringSet(KEY_SAVED_DEVICES, savedDevices.map(::encodeDevice).toSet())
            .remove(KEY_IGNORED_RULES)
            .apply()
    }

    private fun decodeDevice(encoded: String): PrsSavedDevice? {
        val fields = encoded.split('|')
        if (fields.size != 4) return null
        val type = decodeType(fields[0]) ?: return null
        val enabled = when (fields[1]) {
            "1" -> true
            "0" -> false
            else -> return null
        }
        val value = decodeText(fields[3]) ?: return null
        val normalizedValue = when (type) {
            PrsDeviceRuleType.ADDRESS -> normalizeAddress(value) ?: return null
            PrsDeviceRuleType.ADVERTISED_NAME -> normalizeName(value) ?: return null
        }
        val displayName = decodeText(fields[2])?.let(::cleanDisplayName)
        return PrsSavedDevice(type, normalizedValue, displayName, enabled)
    }

    private fun decodeLegacyRule(encoded: String): PrsSavedDevice? {
        val separator = encoded.indexOf('|')
        if (separator <= 0 || separator == encoded.lastIndex) return null
        val type = decodeType(encoded.substring(0, separator)) ?: return null
        val rawValue = encoded.substring(separator + 1)
        val value = when (type) {
            PrsDeviceRuleType.ADDRESS -> normalizeAddress(rawValue) ?: return null
            PrsDeviceRuleType.ADVERTISED_NAME -> normalizeName(rawValue) ?: return null
        }
        return PrsSavedDevice(type, value, rawValue.trim(), enabled = true)
    }

    private fun decodeType(rawType: String): PrsDeviceRuleType? = when (rawType) {
        PrsDeviceRuleType.ADDRESS.name -> PrsDeviceRuleType.ADDRESS
        PrsDeviceRuleType.ADVERTISED_NAME.name -> PrsDeviceRuleType.ADVERTISED_NAME
        else -> null
    }

    private fun encodeDevice(device: PrsSavedDevice): String = listOf(
        device.type.name,
        if (device.enabled) "1" else "0",
        encodeText(device.displayName.orEmpty()),
        encodeText(device.value)
    ).joinToString("|")

    private fun encodeText(value: String): String = Base64.encodeToString(
        value.toByteArray(StandardCharsets.UTF_8),
        Base64.NO_WRAP or Base64.URL_SAFE
    )

    private fun decodeText(value: String): String? = runCatching {
        String(Base64.decode(value, Base64.NO_WRAP or Base64.URL_SAFE), StandardCharsets.UTF_8)
    }.getOrNull()

    private fun cleanDisplayName(rawName: String?): String? = rawName
        ?.filterNot(Char::isISOControl)
        ?.trim()
        ?.replace(Regex("\\s+"), " ")
        ?.takeIf { it.isNotEmpty() }

    private fun PrsSavedDevice.matches(observation: BleObservation): Boolean = when (type) {
        PrsDeviceRuleType.ADDRESS -> normalizeAddress(observation.deviceIdentifier) == value
        PrsDeviceRuleType.ADVERTISED_NAME -> normalizeName(observation.deviceName) == value
    }

    private fun PrsSavedDevice.sameKeyAs(other: PrsSavedDevice): Boolean =
        type == other.type && value == other.value

    companion object {
        private const val PREFERENCES_NAME = "prs_devices"
        private const val KEY_SAVED_DEVICES = "saved_devices"
        private const val KEY_IGNORED_RULES = "ignored_rules"

        fun from(context: Context): PrsDeviceRegistry = PrsDeviceRegistry(
            context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        )

        fun normalizeAddress(rawAddress: String): String? {
            val compact = rawAddress
                .trim()
                .uppercase(Locale.US)
                .replace('-', ':')
            val hexadecimal = compact.replace(":", "")
            if (hexadecimal.length != 12 || hexadecimal.any { it !in "0123456789ABCDEF" }) {
                return null
            }
            return hexadecimal.chunked(2).joinToString(":")
        }

        private fun normalizeName(rawName: String?): String? = rawName
            ?.filterNot(Char::isISOControl)
            ?.trim()
            ?.replace(Regex("\\s+"), " ")
            ?.takeIf { it.isNotEmpty() }
            ?.uppercase(Locale.US)
    }
}
