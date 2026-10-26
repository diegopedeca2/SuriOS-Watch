package com.suri.pipsurios.storage

import com.suri.pipsurios.data.OperationLog
import java.math.BigDecimal

enum class StorageGroup { BBS, GRENADES, GAS }

data class StorageItem(val stableId: String, val displayName: String, val group: StorageGroup)
data class StorageLedgerEntry(
    val purchase: BigDecimal = BigDecimal.ZERO,
    val used: BigDecimal = BigDecimal.ZERO,
    val total: BigDecimal? = null
)

data class StorageBalance(
    val item: StorageItem,
    val purchase: BigDecimal,
    val used: BigDecimal,
    val consumed: BigDecimal,
    val total: BigDecimal
)

object StorageCatalog {
    val bbs = listOf("Random", "0,20", "0,20 TRACER", "0,28", "0,30", "0,30 TRACER", "0,40", "0,45")
        .map { StorageItem("bbs:${it.lowercase()}", it, StorageGroup.BBS) }
    val grenades = listOf("9mm GRENADES", "CO2 GRENADES")
        .map { StorageItem("grenades:${it.lowercase()}", it, StorageGroup.GRENADES) }
    val gas = listOf("06 KG", "08 KG", "10 KG", "12 KG", "14 KG")
        .map { StorageItem("gas:${it.lowercase()}", it, StorageGroup.GAS) }
    val all = bbs + grenades + gas
}

object StorageCalculator {
    fun balance(item: StorageItem, entry: StorageLedgerEntry?, logs: List<OperationLog>): StorageBalance {
        val ledger = entry ?: StorageLedgerEntry()
        val consumption = logs.fold(BigDecimal.ZERO) { total, log ->
            total + when (item.displayName) {
                "9mm GRENADES" -> log.consumables.grenades9mm
                "CO2 GRENADES" -> log.consumables.grenadesCo2
                else -> BigDecimal.ZERO
            }
        }
        return StorageBalance(item, ledger.purchase, ledger.used, consumption,
            ledger.purchase - ledger.used - consumption)
    }
}
