package com.suri.pipsurios

import com.suri.pipsurios.data.OperationConsumables
import com.suri.pipsurios.data.OperationLoadoutSnapshot
import com.suri.pipsurios.data.OperationLog
import com.suri.pipsurios.storage.StorageCalculator
import com.suri.pipsurios.storage.StorageCatalog
import com.suri.pipsurios.storage.StorageLedgerEntry
import com.suri.pipsurios.storage.StorageRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.math.BigDecimal

class StorageTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun purchaseAndUsedIncrementAndPersist() {
        val file = temporaryFolder.root.resolve("ledger.json")
        val key = StorageCatalog.grenades.first().stableId
        assertTrue(StorageRepository(file).purchase(key, emptyList()))
        assertTrue(StorageRepository(file).purchase(key, emptyList()))
        assertTrue(StorageRepository(file).use(key, emptyList()))
        val entry = StorageRepository(file).load().getValue(key)
        assertEquals(BigDecimal("2"), entry.purchase)
        assertEquals(BigDecimal.ONE, entry.used)
        assertEquals(BigDecimal.ONE, entry.total)
    }

    @Test fun multipleItemsSurviveMultilineRoundTripAndIndependentUpdates() {
        val file = temporaryFolder.root.resolve("multiple.json")
        val first = StorageCatalog.bbs.first().stableId
        val second = StorageCatalog.gas.first().stableId
        repeat(5) { assertTrue(StorageRepository(file).purchase(first, emptyList())) }
        assertTrue(StorageRepository(file).use(first, emptyList()))
        repeat(12) { assertTrue(StorageRepository(file).purchase(second, emptyList())) }
        repeat(3) { assertTrue(StorageRepository(file).use(second, emptyList())) }
        val reopened = StorageRepository(file).load()
        assertEquals(BigDecimal("5"), reopened.getValue(first).purchase)
        assertEquals(BigDecimal.ONE, reopened.getValue(first).used)
        assertEquals(BigDecimal("12"), reopened.getValue(second).purchase)
        assertEquals(BigDecimal("3"), reopened.getValue(second).used)
        assertTrue(file.readText().contains("\n"))
    }

    @Test fun reconcilePreservesManualValuesForEveryStableId() {
        val file = temporaryFolder.root.resolve("preserve.json")
        val first = StorageCatalog.grenades.first().stableId
        val second = StorageCatalog.grenades.last().stableId
        repeat(3) { StorageRepository(file).purchase(first, emptyList()) }
        StorageRepository(file).use(first, emptyList())
        repeat(2) { StorageRepository(file).purchase(second, emptyList()) }
        val reconciled = StorageRepository(file).reconcile(listOf(log("0", "1", "1")))
        assertEquals(BigDecimal("3"), reconciled.getValue(first).purchase)
        assertEquals(BigDecimal.ONE, reconciled.getValue(first).used)
        assertEquals(BigDecimal("2"), reconciled.getValue(second).purchase)
        assertEquals(BigDecimal.ZERO, reconciled.getValue(second).used)
    }

    @Test fun stableIdsAreUniqueDeterministicAndIndependentFromDisplayCase() {
        assertEquals(StorageCatalog.all.size, StorageCatalog.all.map { it.stableId }.toSet().size)
        assertEquals("bbs:random", StorageCatalog.bbs.first().stableId)
        assertEquals("gas:06 kg", StorageCatalog.gas.first().stableId)
    }

    @Test fun totalIsPurchaseMinusUsedMinusConsumedAndMayBeNegative() {
        val item = StorageCatalog.grenades.first()
        val positive = StorageCalculator.balance(item, StorageLedgerEntry(BigDecimal.TEN, BigDecimal("2")), listOf(log("0", "3", "0")))
        val negative = StorageCalculator.balance(item, StorageLedgerEntry(BigDecimal.ONE, BigDecimal("2")), listOf(log("0", "3", "0")))
        assertEquals(BigDecimal("5"), positive.total)
        assertEquals(BigDecimal("-4"), negative.total)
    }

    @Test fun editAndDeleteRecalculateConsumedFromCurrentLogs() {
        val item = StorageCatalog.grenades.first()
        val ledger = StorageLedgerEntry(BigDecimal.TEN)
        assertEquals(BigDecimal("2"), StorageCalculator.balance(item, ledger, listOf(log("0", "2", "0"))).consumed)
        assertEquals(BigDecimal("4"), StorageCalculator.balance(item, ledger, listOf(log("0", "4", "0"))).consumed)
        assertEquals(BigDecimal.ZERO, StorageCalculator.balance(item, ledger, emptyList()).consumed)
    }

    @Test fun fortyMillimeterDoesNotExistOrAffectStorage() {
        assertFalse(StorageCatalog.all.any { it.displayName == "40mm GRENADES" })
        val operation = log("99", "0", "0")
        StorageCatalog.all.forEach { item ->
            assertEquals(BigDecimal.ZERO, StorageCalculator.balance(item, null, listOf(operation)).consumed)
        }
    }

    @Test fun bbsAndGasHaveNoAutomaticConsumption() {
        val operation = log("4", "5", "6")
        assertEquals(BigDecimal.ZERO, StorageCalculator.balance(StorageCatalog.bbs.first(), null, listOf(operation)).consumed)
        assertEquals(BigDecimal.ZERO, StorageCalculator.balance(StorageCatalog.gas.first(), null, listOf(operation)).consumed)
    }

    @Test fun legacyProvisionalLedgerMigratesAddAndRemoveSemantics() {
        val file = temporaryFolder.newFile("legacy.json")
        file.writeText("""{"entries":[{"key":"grenades:9mm grenades","initial":0,"transactions":[{"type":"ADD","quantity":5},{"type":"REMOVE","quantity":2}]}]}""")
        val entry = StorageRepository(file).load().getValue("grenades:9mm grenades")
        assertEquals(BigDecimal("5"), entry.purchase)
        assertEquals(BigDecimal("2"), entry.used)
    }

    @Test fun currentLedgerIsReadAfterRestart() {
        val file = temporaryFolder.newFile("current.json")
        file.writeText("""{"entries":[{"key":"grenades:9mm grenades","purchase":7,"used":3}]}""")
        val entry = StorageRepository(file).load().getValue("grenades:9mm grenades")
        assertEquals(BigDecimal("7"), entry.purchase)
        assertEquals(BigDecimal("3"), entry.used)
        assertEquals(null, entry.total)
    }

    @Test fun reconcileAddsMissingTotalAndCorrectsStaleTotal() {
        val file = temporaryFolder.newFile("reconcile.json")
        file.writeText("""{"entries":[{"key":"grenades:9mm grenades","purchase":7,"used":1,"total":99}]}""")
        val repository = StorageRepository(file)
        val corrected = repository.reconcile(listOf(log("0", "2", "0"))).getValue("grenades:9mm grenades")
        assertEquals(BigDecimal("4"), corrected.total)
        assertEquals(BigDecimal("7"), corrected.purchase)
        assertEquals(BigDecimal.ONE, corrected.used)
        assertEquals(BigDecimal("4"), StorageRepository(file).load().getValue("grenades:9mm grenades").total)
    }

    @Test fun negativeTotalIsPersistedAndChangesWithLogCreationEditAndDelete() {
        val file = temporaryFolder.newFile("negative.json")
        file.writeText("""{"entries":[{"key":"grenades:9mm grenades","purchase":1,"used":0}]}""")
        val repository = StorageRepository(file)
        assertEquals(BigDecimal("-1"), repository.reconcile(listOf(log("0", "2", "0"))).values.single().total)
        assertEquals(BigDecimal("-3"), repository.reconcile(listOf(log("0", "4", "0"))).values.single().total)
        assertEquals(BigDecimal.ONE, repository.reconcile(emptyList()).values.single().total)
    }

    private fun log(g40: String, g9: String, co2: String) = OperationLog(
        "12/08/2026", "TEST",
        OperationLoadoutSnapshot(null, null, emptyList(), null, null),
        OperationConsumables(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal(g40), BigDecimal(g9), BigDecimal(co2), BigDecimal.ZERO, BigDecimal.ZERO)
    )
}
