package com.suri.pipsurios

import com.suri.pipsurios.data.OperationConsumables
import com.suri.pipsurios.data.OperationInputValidator
import com.suri.pipsurios.data.OperationEditDraft
import com.suri.pipsurios.data.OperationJsonCodec
import com.suri.pipsurios.data.OperationLoadoutSnapshot
import com.suri.pipsurios.data.OperationLog
import com.suri.pipsurios.data.OperationRepository
import com.suri.pipsurios.data.DeleteOperationResult
import com.suri.pipsurios.data.SaveOperationResult
import com.suri.pipsurios.data.UpdateOperationResult
import com.suri.pipsurios.data.StatisticsCalculator
import java.io.IOException
import com.suri.pipsurios.ui.screens.InventoryItem
import com.suri.pipsurios.ui.screens.PrimaryWeaponRole
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
            primaryRole = PrimaryWeaponRole.SNIPER,
            primaryWeapon = InventoryItem.L96,
            secondaryType = "HANDGUN",
            secondaryWeapon = InventoryItem.DESERT_EAGLE,
            accesories = setOf(InventoryItem.DETON_A),
            headgearProfile = "SURI-14",
            headgearComponents = setOf("VYPER", "DYE MASK"),
            customAccesories = setOf("UTILITY POUCH"),
            frontPanelRole = "SNIPER - ASSAULT",
            uniform = "MCBCK - SUMMER"
        )
        val snapshot = OperationLoadoutSnapshot.from(initial)
        val changed = initial.copy(primaryWeapon = InventoryItem.MCX, accesories = emptySet())

        assertEquals("SNIPER - L96", snapshot.primaryWeapon)
        assertEquals("HANDGUN - DESERT EAGLE", snapshot.secondaryWeapon)
        assertEquals(listOf("DETON-A", "UTILITY POUCH"), snapshot.accesories)
        assertEquals(listOf("DYE MASK", "VYPER"), snapshot.headgearComponents)
        assertNotEquals(changed.primaryWeapon?.displayName, snapshot.primaryWeapon)
        assertEquals("SURI-14", snapshot.headgear)
        assertEquals("MCBCK - SUMMER", snapshot.uniform)
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
    fun repositoryWriteFailureLeavesNoPartialSaveFile() {
        val directory = temporaryFolder.newFolder("save-write-failure")
        val repository = OperationRepository(directory, saveWriter = { _, _ -> throw IOException("CONTROLLED") })

        val result = repository.save(sampleLog(location = "FAILURE TEST"))

        assertTrue(result is SaveOperationResult.Failure)
        assertTrue(repository.listLogs().isEmpty())
        assertTrue(directory.listFiles().orEmpty().none { it.name.startsWith(".operation-save-") })
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

    @Test
    fun repositoryDeletesOnlyTheExactExistingLogAndPersistsDeletion() {
        val directory = temporaryFolder.newFolder("delete", "operations")
        val repository = OperationRepository(directory)
        val deletedLog = sampleLog(date = "10/08/2026", location = "DELETE ME")
        val retainedLog = sampleLog(date = "11/08/2026", location = "KEEP ME")
        repository.save(deletedLog)
        repository.save(retainedLog)

        assertEquals(DeleteOperationResult.Deleted, repository.delete("20260810.json"))
        assertEquals(listOf("20260811.json"), repository.listLogs())
        assertEquals(null, repository.read("20260810.json"))
        assertEquals(retainedLog, repository.read("20260811.json"))

        val reopenedRepository = OperationRepository(directory)
        assertEquals(listOf(retainedLog), reopenedRepository.loadAll().entries.map { it.log })
    }

    @Test
    fun repositoryReportsMissingLogWithoutChangingOtherLogs() {
        val directory = temporaryFolder.newFolder("delete-missing", "operations")
        val repository = OperationRepository(directory)
        val retainedLog = sampleLog(date = "11/08/2026", location = "KEEP ME")
        repository.save(retainedLog)

        assertEquals(DeleteOperationResult.NotFound, repository.delete("20260810.json"))
        assertEquals(DeleteOperationResult.NotFound, repository.delete("../20260811.json"))
        assertEquals(listOf(retainedLog), repository.loadAll().entries.map { it.log })
    }

    @Test
    fun repositoryListIsEmptyAfterDeletingTheLastLog() {
        val directory = temporaryFolder.newFolder("delete-last", "operations")
        val repository = OperationRepository(directory)
        repository.save(sampleLog(location = "ONLY LOG"))

        assertEquals(DeleteOperationResult.Deleted, repository.delete("20260810.json"))
        assertTrue(repository.listLogs().isEmpty())
        assertTrue(repository.loadAll().entries.isEmpty())
    }

    @Test
    fun editDraftPreloadsEveryHistoricalValueWithoutMutatingTheLog() {
        val base = sampleLog(location = "HISTORICAL")
        val original = base.copy(loadout = base.loadout.copy(uniform = "MCBCK - SUMMER"))
        val entry = com.suri.pipsurios.data.OperationLogEntry("20260810.json", original)

        val draft = OperationEditDraft.from(entry)
        val changed = draft.copy(
            location = "CHANGED",
            loadout = draft.loadout.copy(headgear = "BROTHERHOOD", uniform = "DESERT"),
            consumables = draft.consumables.copy(primaryMag = BigDecimal("9.5"))
        )

        assertEquals(entry.filename, draft.originalFilename)
        assertEquals(original, draft.toOperationLog())
        assertEquals("HISTORICAL", original.location)
        assertEquals("SURI-14", original.loadout.headgear)
        assertEquals(BigDecimal("2.50"), original.consumables.primaryMag)
        assertEquals("MCBCK - SUMMER", draft.loadout.uniform)
        assertEquals("CHANGED", changed.location)
        assertEquals("DESERT", changed.loadout.uniform)
    }

    @Test
    fun operationDraftRetainsUniformSnapshot() {
        val configuration = LoadoutConfiguration(uniform = "DESERT")
        val draft = com.suri.pipsurios.data.OperationDraft(
            loadout = OperationLoadoutSnapshot.from(configuration)
        )

        assertEquals("DESERT", draft.loadout?.uniform)
    }

    @Test
    fun repositoryUpdatesSameDateInPlaceIncludingLoadoutAndConsumables() {
        val directory = temporaryFolder.newFolder("update-same-date")
        val repository = OperationRepository(directory)
        repository.save(sampleLog(location = "OLD"))
        val updated = sampleLog(location = "NEW").copy(
            loadout = sampleLog(location = "NEW").loadout.copy(
                primaryWeapon = "L96",
                secondaryWeapon = "AAP-01C",
                headgear = "BROTHERHOOD",
                uniform = "MCBCK - LONG"
            ),
            consumables = sampleLog(location = "NEW").consumables.copy(
                primaryMag = BigDecimal("7.25"),
                grenadesCo2 = BigDecimal("8")
            )
        )

        assertEquals(
            UpdateOperationResult.Updated("20260810.json"),
            repository.update("20260810.json", updated)
        )
        assertEquals(listOf("20260810.json"), repository.listLogs())
        assertEquals(updated, repository.read("20260810.json"))
    }

    @Test
    fun repositoryDateChangeWritesNewFilenameBeforeRemovingOriginalAndPersists() {
        val directory = temporaryFolder.newFolder("update-date")
        val repository = OperationRepository(directory)
        repository.save(sampleLog(location = "OLD"))
        val updated = sampleLog(date = "12/08/2026", location = "NEW DATE")

        assertEquals(
            UpdateOperationResult.Updated("20260812.json"),
            repository.update("20260810.json", updated)
        )
        assertEquals(null, repository.read("20260810.json"))
        assertEquals(updated, repository.read("20260812.json"))
        assertEquals(listOf(updated), OperationRepository(directory).loadAll().entries.map { it.log })
    }

    @Test
    fun repositoryDateConflictPreservesBothExistingLogs() {
        val directory = temporaryFolder.newFolder("update-conflict")
        val repository = OperationRepository(directory)
        val original = sampleLog(location = "ORIGINAL")
        val conflict = sampleLog(date = "12/08/2026", location = "CONFLICT")
        repository.save(original)
        repository.save(conflict)

        assertEquals(
            UpdateOperationResult.Conflict("20260812.json"),
            repository.update("20260810.json", conflict.copy(location = "MUST NOT WRITE"))
        )
        assertEquals(original, repository.read("20260810.json"))
        assertEquals(conflict, repository.read("20260812.json"))
    }

    @Test
    fun repositoryWriteFailurePreservesOriginalAndCreatesNoReplacement() {
        val directory = temporaryFolder.newFolder("update-write-failure")
        val normalRepository = OperationRepository(directory)
        val original = sampleLog(location = "ORIGINAL")
        normalRepository.save(original)
        val failingRepository = OperationRepository(directory) { _, _ -> throw IOException("CONTROLLED") }

        val result = failingRepository.update(
            "20260810.json",
            sampleLog(date = "12/08/2026", location = "SHOULD FAIL")
        )

        assertTrue(result is UpdateOperationResult.Failure)
        assertEquals(original, normalRepository.read("20260810.json"))
        assertEquals(null, normalRepository.read("20260812.json"))
    }

    @Test
    fun abandoningAnEditDraftDoesNotTouchRepository() {
        val directory = temporaryFolder.newFolder("edit-cancel")
        val repository = OperationRepository(directory)
        val original = sampleLog(location = "UNCHANGED")
        repository.save(original)
        val draft = OperationEditDraft.from(repository.loadAll().entries.single())
            .copy(location = "UNCONFIRMED")

        assertEquals("UNCONFIRMED", draft.location)
        assertEquals(original, repository.read("20260810.json"))
    }

    @Test
    fun statisticsReadUpdatedHistoricalValuesFromRepository() {
        val directory = temporaryFolder.newFolder("update-statistics")
        val repository = OperationRepository(directory)
        repository.save(sampleLog(location = "OLD"))
        val updated = sampleLog(location = "NEW").copy(
            loadout = sampleLog(location = "NEW").loadout.copy(
                primaryWeapon = "L96",
                headgear = "BROTHERHOOD",
                uniform = "DESERT"
            )
        )
        repository.update("20260810.json", updated)
        val persisted = repository.loadAll().entries.map { it.log }

        val primary = StatisticsCalculator.percentageDistribution(
            listOf("L96", "MCX"), persisted.map { it.loadout.primaryWeapon }
        )
        val headgear = StatisticsCalculator.percentageDistribution(
            listOf("SURI-14", "BROTHERHOOD"), persisted.map { it.loadout.headgear }
        )
        val locations = StatisticsCalculator.locationDistribution(persisted.map { it.location })

        assertEquals("100%", StatisticsCalculator.formatPercentage(primary.entries[0].percentage))
        assertEquals("100%", StatisticsCalculator.formatPercentage(headgear.entries[1].percentage))
        assertEquals("NEW", locations.entries.single().option)
    }

    @Test
    fun operationLogSerializesAndDeserializesUniform() {
        val original = sampleLog(location = "UNIFORM").copy(
            loadout = sampleLog(location = "UNIFORM").loadout.copy(uniform = "MCBCK - LONG")
        )

        val json = OperationJsonCodec.serialize(original)
        val restored = OperationJsonCodec.deserialize(json)

        assertTrue(json.contains("\"uniform\": \"MCBCK - LONG\""))
        assertEquals("MCBCK - LONG", restored.loadout.uniform)
        assertEquals(original, restored)
    }

    @Test
    fun historicalJsonWithoutUniformRemainsReadableAndIsNotModifiedOnRead() {
        val directory = temporaryFolder.newFolder("legacy-uniform")
        val repository = OperationRepository(directory)
        val legacyJson = OperationJsonCodec.serialize(sampleLog(location = "LEGACY"))
            .replace(",\n    \"uniform\": null", "")
        val legacyFile = File(directory, "20260810.json")
        legacyFile.writeText(legacyJson)
        val beforeRead = legacyFile.readBytes()

        val restored = repository.read("20260810.json")

        assertEquals(null, restored?.loadout?.uniform)
        assertTrue(beforeRead.contentEquals(legacyFile.readBytes()))
        assertEquals(1, repository.loadAll().entries.size)
        assertEquals(0, repository.loadAll().unreadableFileCount)
    }

    @Test
    fun legacyEditDraftCanSelectUniformAndRetainsItAcrossStepCopies() {
        val legacy = sampleLog(location = "LEGACY EDIT")
        val initial = OperationEditDraft.from(
            com.suri.pipsurios.data.OperationLogEntry("20260810.json", legacy)
        )

        val selected = initial.copy(loadout = initial.loadout.copy(uniform = "DESERT"))
        val afterDateStep = selected.copy(location = "LEGACY EDIT")
        val afterConsumablesStep = afterDateStep.copy(consumables = afterDateStep.consumables.copy())

        assertEquals(null, initial.loadout.uniform)
        assertEquals("DESERT", afterDateStep.loadout.uniform)
        assertEquals("DESERT", afterConsumablesStep.loadout.uniform)
    }

    @Test
    fun uniformEditIsPersistedOnlyAfterExplicitRepositoryUpdate() {
        val directory = temporaryFolder.newFolder("edit-uniform-explicit")
        val repository = OperationRepository(directory)
        val legacy = sampleLog(location = "LEGACY")
        repository.save(legacy)
        val selectedDraft = OperationEditDraft.from(repository.loadAll().entries.single()).copy(
            loadout = legacy.loadout.copy(uniform = "DESERT")
        )

        assertEquals(null, repository.read("20260810.json")?.loadout?.uniform)
        assertEquals(
            UpdateOperationResult.Updated("20260810.json"),
            repository.update(selectedDraft.originalFilename, selectedDraft.toOperationLog())
        )
        assertEquals("DESERT", repository.read("20260810.json")?.loadout?.uniform)
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
