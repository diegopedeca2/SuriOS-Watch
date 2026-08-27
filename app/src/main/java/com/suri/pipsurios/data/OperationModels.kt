package com.suri.pipsurios.data

import com.suri.pipsurios.ui.state.LoadoutConfiguration
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

data class OperationLoadoutSnapshot(
    val primaryWeapon: String?,
    val secondaryWeapon: String?,
    val accesories: List<String>,
    val headgear: String?,
    val frontPanel: String?,
    val uniform: String? = null
) {
    companion object {
        fun from(loadout: LoadoutConfiguration): OperationLoadoutSnapshot =
            OperationLoadoutSnapshot(
                primaryWeapon = loadout.primaryWeaponDisplayName(),
                secondaryWeapon = loadout.secondaryWeapon?.displayName,
                accesories = loadout.accesories.map { it.displayName }.sorted(),
                headgear = loadout.headgearProfile,
                frontPanel = loadout.frontPanelRole,
                uniform = loadout.uniform
            )
    }
}

data class OperationConsumables(
    val primaryMag: BigDecimal,
    val secondaryMag: BigDecimal,
    val grenades40mm: BigDecimal,
    val grenades9mm: BigDecimal,
    val grenadesCo2: BigDecimal,
    val primaryHpa: BigDecimal,
    val secondaryHpa: BigDecimal
)

data class OperationDraft(
    val date: String = "",
    val location: String = "",
    val loadout: OperationLoadoutSnapshot? = null,
    val consumables: OperationConsumables? = null
)

data class OperationEditDraft(
    val originalFilename: String,
    val date: String,
    val location: String,
    val loadout: OperationLoadoutSnapshot,
    val consumables: OperationConsumables
) {
    fun toOperationLog() = OperationLog(date, location, loadout, consumables)

    companion object {
        fun from(entry: OperationLogEntry) = OperationEditDraft(
            originalFilename = entry.filename,
            date = entry.log.date,
            location = entry.log.location,
            loadout = entry.log.loadout.copy(accesories = entry.log.loadout.accesories.toList()),
            consumables = entry.log.consumables.copy()
        )
    }
}

data class OperationLog(
    val date: String,
    val location: String,
    val loadout: OperationLoadoutSnapshot,
    val consumables: OperationConsumables
)

object OperationInputValidator {
    private val displayDateFormatter = DateTimeFormatter
        .ofPattern("dd/MM/uuuu")
        .withResolverStyle(ResolverStyle.STRICT)
    private val filenameDateFormatter = DateTimeFormatter.ofPattern("uuuuMMdd")
    private val decimalPattern = Regex("^\\d+(?:[.,]\\d{1,2})?$")

    fun isValidDate(value: String): Boolean =
        value.matches(Regex("^\\d{2}/\\d{2}/\\d{4}$")) &&
            runCatching { LocalDate.parse(value, displayDateFormatter) }.isSuccess

    fun isValidLocation(value: String): Boolean = value.trim().isNotEmpty()

    fun dateToFilenameBase(value: String): String? = runCatching {
        LocalDate.parse(value, displayDateFormatter).format(filenameDateFormatter)
    }.getOrNull()

    fun formatDateInput(value: String): String {
        val digits = value.filter(Char::isDigit).take(8)
        return buildString {
            digits.forEachIndexed { index, character ->
                if (index == 2 || index == 4) append('/')
                append(character)
            }
        }
    }

    fun parseDecimal(value: String): BigDecimal? {
        val normalized = value.trim().replace(',', '.')
        if (!decimalPattern.matches(value.trim())) return null
        return normalized.toBigDecimalOrNull()
    }

    fun formatDecimal(value: BigDecimal): String = value.stripTrailingZeros().toPlainString()
}
