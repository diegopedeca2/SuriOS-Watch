package com.suri.pipsurios.storage

import android.content.Context
import java.io.File
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.AtomicMoveNotSupportedException

class StorageRepository(private val file: File) {
    fun load(): Map<String, StorageLedgerEntry> {
        if (!file.isFile) return emptyMap()
        return runCatching { decode(file.readText(Charsets.UTF_8)) }.getOrDefault(emptyMap())
    }

    fun purchase(itemKey: String, logs: List<com.suri.pipsurios.data.OperationLog>): Boolean =
        increment(itemKey, purchase = true, logs)
    fun use(itemKey: String, logs: List<com.suri.pipsurios.data.OperationLog>): Boolean =
        increment(itemKey, purchase = false, logs)

    private fun increment(itemKey: String, purchase: Boolean, logs: List<com.suri.pipsurios.data.OperationLog>): Boolean {
        if (StorageCatalog.all.none { it.stableId == itemKey }) return false
        val ledger = load().toMutableMap()
        val current = ledger[itemKey] ?: StorageLedgerEntry()
        val updated = if (purchase) current.copy(purchase = current.purchase + BigDecimal.ONE)
        else current.copy(used = current.used + BigDecimal.ONE)
        val item = StorageCatalog.all.first { it.stableId == itemKey }
        val derived = StorageCalculator.balance(item, updated, logs)
        ledger[itemKey] = updated.copy(total = derived.total)
        return write(ledger)
    }

    fun reconcile(logs: List<com.suri.pipsurios.data.OperationLog>): Map<String, StorageLedgerEntry> {
        val ledger = load().toMutableMap()
        var changed = false
        ledger.toMap().forEach { (key, entry) ->
            val item = StorageCatalog.all.firstOrNull { it.stableId == key } ?: return@forEach
            val derived = StorageCalculator.balance(item, entry, logs).total
            if (entry.total?.compareTo(derived) != 0) {
                ledger[key] = entry.copy(total = derived)
                changed = true
            }
        }
        if (changed) write(ledger)
        return ledger
    }

    private fun write(ledger: Map<String, StorageLedgerEntry>): Boolean = runCatching {
        val directory = requireNotNull(file.parentFile) { "STORAGE DIRECTORY UNAVAILABLE" }
        directory.mkdirs()
        val temporary = Files.createTempFile(directory.toPath(), ".storage-", ".tmp")
        temporary.toFile().writeText(encode(ledger), Charsets.UTF_8)
        try {
            Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(temporary, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        true
    }.getOrDefault(false)

    private fun encode(ledger: Map<String, StorageLedgerEntry>): String = buildString {
        appendLine("{")
        appendLine("  \"entries\": [")
        val entries = ledger.entries.sortedBy { it.key }
        entries.forEachIndexed { index, (key, entry) ->
            append("    {\"key\":\"").append(escape(key)).append("\",\"purchase\":")
                .append(entry.purchase.toPlainString()).append(",\"used\":")
                .append(entry.used.toPlainString()).append(",\"total\":")
                .append((entry.total ?: BigDecimal.ZERO).toPlainString()).append('}')
            if (index < entries.lastIndex) append(',')
            appendLine()
        }
        appendLine("  ]")
        append('}')
    }

    private fun decode(json: String): Map<String, StorageLedgerEntry> {
        val current = json.split("\"key\"").drop(1).mapNotNull { entryTail ->
            val objectJson = entryTail.substringBefore('}')
            val key = quotedValueAfterColon(objectJson) ?: return@mapNotNull null
            val purchase = decimalField(objectJson, "purchase") ?: return@mapNotNull null
            val used = decimalField(objectJson, "used") ?: return@mapNotNull null
            val total = decimalField(objectJson, "total")
            unescape(key) to StorageLedgerEntry(purchase, used, total)
        }.toMap()
        if (current.isNotEmpty()) return current.filterKeys { key -> StorageCatalog.all.any { it.stableId == key } }
        val entryRegex = Regex("\\{\\\"key\\\":\\\"([^\\\"]+)\\\",\\\"initial\\\":([0-9.]+),\\\"transactions\\\":\\[(.*?)]}")
        val transactionRegex = Regex("\\{\\\"type\\\":\\\"(ADD|REMOVE)\\\",\\\"quantity\\\":([0-9.]+)}")
        return entryRegex.findAll(json).associate { match ->
            val transactions = transactionRegex.findAll(match.groupValues[3]).toList()
            val added = transactions.filter { it.groupValues[1] == "ADD" }.fold(BigDecimal.ZERO) { sum, value -> sum + value.groupValues[2].toBigDecimal() }
            val removed = transactions.filter { it.groupValues[1] == "REMOVE" }.fold(BigDecimal.ZERO) { sum, value -> sum + value.groupValues[2].toBigDecimal() }
            unescape(match.groupValues[1]) to StorageLedgerEntry(match.groupValues[2].toBigDecimal() + added, removed)
        }.filterKeys { key -> StorageCatalog.all.any { it.stableId == key } }
    }

    private fun escape(value: String) = value.replace("\\", "\\\\").replace("\"", "\\\"")
    private fun unescape(value: String) = value.replace("\\\"", "\"").replace("\\\\", "\\")

    private fun quotedValueAfterColon(value: String): String? {
        val afterColon = value.substringAfter(':', missingDelimiterValue = "").trimStart()
        if (!afterColon.startsWith('"')) return null
        return afterColon.drop(1).substringBefore('"', missingDelimiterValue = "").takeIf(String::isNotEmpty)
    }

    private fun decimalField(value: String, name: String): BigDecimal? {
        val marker = "\"$name\""
        val afterMarker = value.substringAfter(marker, missingDelimiterValue = "")
        if (afterMarker.isEmpty()) return null
        val raw = afterMarker.substringAfter(':', missingDelimiterValue = "").trimStart()
            .takeWhile { it == '-' || it == '.' || it.isDigit() }
        return raw.toBigDecimalOrNull()
    }

    companion object {
        fun from(context: Context) = StorageRepository(File(context.filesDir, "data/storage/ledger.json"))
    }
}
