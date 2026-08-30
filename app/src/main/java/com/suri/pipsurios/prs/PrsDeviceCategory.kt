package com.suri.pipsurios.prs

import android.bluetooth.BluetoothClass
import java.util.Locale

enum class PrsDeviceCategory(val label: String) {
    PHONE("PHONE"),
    WATCH("WATCH"),
    TV("TV"),
    AUDIO("AUDIO"),
    COMPUTER("COMPUTER")
}

/** Best-effort category inference from data exposed by a passive BLE scan. */
object PrsDeviceClassifier {
    fun classify(
        deviceName: String?,
        advertisingDataHex: String?,
        bluetoothClass: Int?
    ): PrsDeviceCategory? {
        val normalizedName = deviceName?.normalizeForMatching().orEmpty()
        if (normalizedName.isNotEmpty()) {
            nameCategory(normalizedName)?.let { return it }
        }

        bluetoothClassCategory(bluetoothClass)?.let { return it }
        return appearanceCategory(advertisingDataHex)
    }

    private fun nameCategory(name: String): PrsDeviceCategory? = when {
        name.hasAnyToken(
            "BUDS", "AIRPODS", "HEADPHONE", "HEADSET", "EARBUD",
            "SPEAKER", "SOUNDBAR", "AUDIO", "BOOMBOX", "JBL", "BOSE"
        ) -> PrsDeviceCategory.AUDIO
        name.hasAnyToken(
            "WATCH", "WATCH 2", "SMARTWATCH", "WEARABLE", "FITBIT", "GARMIN", "GALAXY FIT"
        ) -> PrsDeviceCategory.WATCH
        name.hasAnyToken(
            "TV", "TELEVISION", "TELEVISOR", "BRAVIA", "ROKU", "CHROMECAST",
            "FIRE TV", "APPLE TV", "ANDROID TV", "WEBOS", "TIZEN"
        ) -> PrsDeviceCategory.TV
        name.hasAnyToken(
            "LAPTOP", "MACBOOK", "DESKTOP", "COMPUTER", "PC", "SURFACE",
            "THINKPAD", "CHROMEBOOK"
        ) -> PrsDeviceCategory.COMPUTER
        name.hasAnyToken(
            "PHONE", "MOBILE", "SMARTPHONE", "IPHONE", "GALAXY", "PIXEL",
            "ONEPLUS", "XIAOMI", "REDMI", "HUAWEI", "OPPO", "VIVO",
            "MOTOROLA", "NOKIA", "REALME", "IPAD", "TABLET"
        ) -> PrsDeviceCategory.PHONE
        else -> null
    }

    private fun bluetoothClassCategory(deviceClass: Int?): PrsDeviceCategory? {
        deviceClass ?: return null
        if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX) {
            return PrsDeviceCategory.TV
        }

        return when (deviceClass and BLUETOOTH_MAJOR_CLASS_MASK) {
            BluetoothClass.Device.Major.PHONE -> PrsDeviceCategory.PHONE
            BluetoothClass.Device.Major.WEARABLE -> PrsDeviceCategory.WATCH
            BluetoothClass.Device.Major.COMPUTER -> PrsDeviceCategory.COMPUTER
            BluetoothClass.Device.Major.AUDIO_VIDEO -> PrsDeviceCategory.AUDIO
            else -> null
        }
    }

    private fun appearanceCategory(advertisingDataHex: String?): PrsDeviceCategory? {
        val appearance = advertisingDataHex.appearanceValue() ?: return null
        return when {
            appearance == APPEARANCE_GENERIC_PHONE -> PrsDeviceCategory.PHONE
            appearance == APPEARANCE_WEARABLE_COMPUTER_WATCH -> PrsDeviceCategory.WATCH
            appearance == APPEARANCE_GENERIC_TABLET -> PrsDeviceCategory.PHONE
            appearance in APPEARANCE_COMPUTER_RANGE -> PrsDeviceCategory.COMPUTER
            appearance in APPEARANCE_WATCH_RANGE -> PrsDeviceCategory.WATCH
            appearance in APPEARANCE_AUDIO_RANGE -> PrsDeviceCategory.AUDIO
            else -> null
        }
    }

    private fun String?.appearanceValue(): Int? {
        val bytes = this
            ?.trim()
            ?.takeIf { it.isNotEmpty() && it.length % 2 == 0 }
            ?.chunked(2)
            ?.map { it.toIntOrNull(16) ?: return null }
            ?: return null

        var index = 0
        while (index < bytes.size) {
            val length = bytes[index]
            if (length == 0) break
            val typeIndex = index + 1
            val endIndex = typeIndex + length
            if (endIndex > bytes.size) return null
            if (bytes[typeIndex] == AD_TYPE_APPEARANCE && length >= APPEARANCE_FIELD_LENGTH) {
                return bytes[typeIndex + 1] or (bytes[typeIndex + 2] shl 8)
            }
            index = endIndex
        }
        return null
    }

    private fun String.normalizeForMatching(): String =
        uppercase(Locale.US)
            .replace(NON_ALPHANUMERIC, " ")
            .trim()
            .replace(REPEATED_SPACES, " ")

    private fun String.hasAnyToken(vararg tokens: String): Boolean {
        val padded = " $this "
        return tokens.any { token -> padded.contains(" ${token.normalizeForMatching()} ") }
    }

    private const val AD_TYPE_APPEARANCE = 0x19
    private const val APPEARANCE_FIELD_LENGTH = 3
    private const val APPEARANCE_GENERIC_PHONE = 0x0040
    private const val APPEARANCE_GENERIC_TABLET = 0x0087
    private const val APPEARANCE_WEARABLE_COMPUTER_WATCH = 0x0086
    private const val BLUETOOTH_MAJOR_CLASS_MASK = 0x1F00
    private val APPEARANCE_COMPUTER_RANGE = 0x0080..0x008F
    private val APPEARANCE_WATCH_RANGE = 0x00C0..0x00FF
    private val APPEARANCE_AUDIO_RANGE = 0x0800..0x08FF
    private val NON_ALPHANUMERIC = Regex("[^A-Z0-9]+")
    private val REPEATED_SPACES = Regex("\\s+")
}

fun BleObservation.categorySuffix(): String =
    deviceCategory?.let { " [${it.label}]" }.orEmpty()
