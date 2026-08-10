package com.suri.pipsurios

import com.suri.pipsurios.data.OperationConsumables
import com.suri.pipsurios.data.OperationInputValidator
import com.suri.pipsurios.data.OperationJsonCodec
import com.suri.pipsurios.data.OperationLoadoutSnapshot
import com.suri.pipsurios.data.OperationLog
import com.suri.pipsurios.data.OperationRepository
import com.suri.pipsurios.data.SaveOperationResult
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.state.LoadoutConfiguration
import java.math.BigDecimal
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class OperationDataTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun dateValidationRequiresExactRealDate() {
        assertTrue(OperationInputValidator.isValidDate("10/08/2026"))
        assertTrue(OperationInputValidator.isValidDate("29/02/2024"))
        assertFalse(OperationInputValidator.isValidDate("29/02/2025"))
        assertFalse(OperationInputValidator.isValidDate("1/08/2026"))
        assertFalse(OperationInputValidator.isValidDate("10-08-2026"))
    }

    @Test
    fun dateConvertsToAuthorizedFilenameBase() {
        assertEquals("20260810", OperationInputValidator.dateToFilenameBase("10/08/2026"))
        assertEquals(null, OperationInputValidator.dateToFilenameBase("31/02/2026"))
        assertEquals("31/12/2099", OperationInputValidator.formatDateInput("31122099"))
    }

    @Test
    fun operationLogSerializesAndDeserializesWithoutDataLoss() {
        val original = sampleLog(location = "VALLE \"NORTE\"")

        val json = OperationJsonCodec.serialize(original)
        val restored = OperationJsonCodec.deserialize(json)

        assertEquals(original, restored)
        assertTrue(json.contains("\"primaryMag\": 2.50"))
    }

    @Test
    fun loadoutSnapshotDoesNotChangeWithActiveLoadout() {
        val initial = LoadoutConfiguration(
            primaryWeapon = InventoryItem.L96,
            secondaryWeapon = InventoryItem.DESERT_EAGLE,
            accesories = setOf(InventoryItem.DETON_A),
            headgearProfile = "SURI-14",
            frontPanelRole = "SNIPER - ASSAULT"
        )
        val snapshot = OperationLoadoutSnapshot.from(initial)
        val changed = initial.copy(primaryWeapon = InventoryItem.MCX, accesories = emptySet())

        assertEquals("L96", snapshot.primaryWeapon)
        assertEquals(listOf("DETON-A"), snapshot.accesories)
        assertNotEquals(changed.primaryWeapon?.displayName, snapshot.primaryWeapon)
        assertEquals("SURI-14", snapshot.headgear)
    }

    @Test
    fun consumablesAcceptCommaOrPointAndMaximumTwoDecimals() {
        assertEquals(BigDecimal("0"), OperationInputValidator.parseDecimal("0"))
        assertEquals(BigDecimal("2.5"), OperationInputValidator.parseDecimal("2,5"))
        assertEquals(BigDecimal("3.25"), OperationInputValidator.parseDecimal("3.25"))
        assertEquals(null, OperationInputValidator.parseDecimal("1.234"))
        assertEquals(null, OperationInputValidator.parseDecimal(""))
    }

    @Test
    fun repositoryPreventsSameDateOverwriteAndCanReadAndListLogs() {
        val directory = temporaryFolder.newFolder("data", "operations")
        val repository = OperationRepository(directory)
        val first = sampleLog(location = "VALLE ARENA")
        val second = sampleLog(location = "OTHER LOCATION")

        val firstResult = repository.save(first)
        val secondResult = repository.save(second)

        assertTrue(firstResult is SaveOperationResult.Saved)
        assertTrue(secondResult is SaveOperationResult.AlreadyExists)
        assertEquals(listOf("20260810.json"), repository.listLogs())
        assertEquals(first, repository.read("20260810.json"))
        assertEquals("VALLE ARENA", repository.read("20260810.json")?.location)
    }

    @Test
    fun repositoryEnumeratesAndLoadsLogsNewestFirst() {
        val directory = temporaryFolder.newFolder("ordered", "operations")
        val repository = OperationRepository(directory)
        val oldest = sampleLog(date = "19/07/2026", location = "OLD")
        val middle = sampleLog(date = "03/08/2026", location = "MIDDLE")
        val newest = sampleLog(date = "10/08/2026", location = "NEWEST")

        repository.save(oldest)
        repository.save(newest)
        repository.save(middle)

        assertEquals(
            listOf("20260810.json", "20260803.json", "20260719.json"),
            repository.listLogs()
        )
        val collection = repository.loadAll()
        assertEquals(listOf(newest, middle, oldest), collection.entries.map { it.log })
        assertEquals(0, collection.unreadableFileCount)
    }

    @Test
    fun repositoryReturnsEmptyCollectionWhenNoLogsExist() {
        val repository = OperationRepository(temporaryFolder.newFolder("empty"))

        assertTrue(repository.listLogs().isEmpty())
        assertTrue(repository.loadAll().entries.isEmpty())
        assertEquals(0, repository.loadAll().unreadableFileCount)
    }

    @Test
    fun repositorySkipsInvalidLogAndPreservesValidHistoricalSnapshot() {
        val directory = temporaryFolder.newFolder("mixed", "operations")
        val repository = OperationRepository(directory)
        val historical = sampleLog(location = "HISTORICAL")
        repository.save(historical)
        val invalid = File(directory, "20260809.json")
        invalid.writeText("{not-valid-json")

        val collection = repository.loadAll()

        assertEquals(listOf(historical), collection.entries.map { it.log })
        assertEquals(historical.loadout, collection.entries.single().log.loadout)
        assertEquals(historical.consumables, collection.entries.single().log.consumables)
        assertEquals(1, collection.unreadableFileCount)
        assertTrue(invalid.exists())
    }

    private fun sampleLog(date: String = "10/08/2026", location: String) = OperationLog(
        date = date,
        location = location,
        loadout = OperationLoadoutSnapshot(
            primaryWeapon = "MCX",
            secondaryWeapon = "MK23",
            accesories = listOf("DETON-A", "TANTO"),
            headgear = "SURI-14",
            frontPanel = "LIGHT ASSAULT"
        ),
        consumables = OperationConsumables(
            primaryMag = BigDecimal("2.50"),
            secondaryMag = BigDecimal("1"),
            grenades40mm = BigDecimal("3.25"),
            grenades9mm = BigDecimal("0"),
            grenadesCo2 = BigDecimal("2"),
            primaryHpa = BigDecimal("1.5"),
            secondaryHpa = BigDecimal("0")
        )
    )
}
