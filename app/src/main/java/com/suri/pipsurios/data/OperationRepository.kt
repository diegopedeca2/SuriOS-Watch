package com.suri.pipsurios.data

import android.content.Context
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.StandardCopyOption
import java.nio.file.Path

sealed interface SaveOperationResult {
    data class Saved(val file: File) : SaveOperationResult
    data class AlreadyExists(val file: File) : SaveOperationResult
    data class Failure(val message: String) : SaveOperationResult
}

sealed interface DeleteOperationResult {
    data object Deleted : DeleteOperationResult
    data object NotFound : DeleteOperationResult
    data class Failure(val message: String) : DeleteOperationResult
}

sealed interface UpdateOperationResult {
    data class Updated(val filename: String) : UpdateOperationResult
    data class Conflict(val filename: String) : UpdateOperationResult
    data object OriginalNotFound : UpdateOperationResult
    data class Failure(val message: String) : UpdateOperationResult
}

data class OperationLogEntry(
    val filename: String,
    val log: OperationLog
)

data class OperationLogCollection(
    val entries: List<OperationLogEntry>,
    val unreadableFileCount: Int
)

class OperationRepository(
    private val operationsDirectory: File,
    private val saveWriter: (Path, String) -> Unit = { path, content ->
        path.toFile().writeText(content, StandardCharsets.UTF_8)
    },
    private val updateWriter: (Path, String) -> Unit = { path, content ->
        path.toFile().writeText(content, StandardCharsets.UTF_8)
    }
) {
    private val validFilename = Regex("^\\d{8}\\.json$")

    fun save(log: OperationLog): SaveOperationResult {
        val filenameBase = OperationInputValidator.dateToFilenameBase(log.date)
            ?: return SaveOperationResult.Failure("INVALID DATE")
        if (!operationsDirectory.exists() && !operationsDirectory.mkdirs()) {
            return SaveOperationResult.Failure("STORAGE UNAVAILABLE")
        }
        val file = File(operationsDirectory, "$filenameBase.json")
        if (file.exists()) return SaveOperationResult.AlreadyExists(file)
        val temporaryFile = runCatching {
            Files.createTempFile(operationsDirectory.toPath(), ".operation-save-", ".tmp")
        }.getOrElse { error ->
            return SaveOperationResult.Failure(error.message ?: "SAVE FAILED")
        }
        return try {
            saveWriter(temporaryFile, OperationJsonCodec.serialize(log))
            // The temporary file is in the same directory, so the filesystem rename
            // keeps the destination from ever being observed as a partial JSON file.
            // Do not use REPLACE_EXISTING: same-date saves are an intentional conflict.
            Files.move(temporaryFile, file.toPath())
            SaveOperationResult.Saved(file)
        } catch (_: FileAlreadyExistsException) {
            SaveOperationResult.AlreadyExists(file)
        } catch (error: IOException) {
            SaveOperationResult.Failure(error.message ?: "SAVE FAILED")
        } catch (error: SecurityException) {
            SaveOperationResult.Failure(error.message ?: "SAVE FAILED")
        } finally {
            Files.deleteIfExists(temporaryFile)
        }
    }

    fun read(filename: String): OperationLog? {
        if (!filename.matches(validFilename)) return null
        val file = File(operationsDirectory, filename)
        if (!file.isFile) return null
        return runCatching { OperationJsonCodec.deserialize(file.readText(Charsets.UTF_8)) }.getOrNull()
    }

    fun listLogs(): List<String> = operationsDirectory.listFiles()
        ?.asSequence()
        ?.filter { it.isFile && it.name.matches(validFilename) }
        ?.map { it.name }
        ?.sortedDescending()
        ?.toList()
        .orEmpty()

    fun loadAll(): OperationLogCollection {
        val entries = mutableListOf<OperationLogEntry>()
        var unreadableFileCount = 0
        listLogs().forEach { filename ->
            val log = read(filename)
            if (log == null) {
                unreadableFileCount++
            } else {
                entries += OperationLogEntry(filename, log)
            }
        }
        return OperationLogCollection(entries, unreadableFileCount)
    }

    fun delete(filename: String): DeleteOperationResult {
        if (!filename.matches(validFilename)) return DeleteOperationResult.NotFound
        val file = File(operationsDirectory, filename)
        if (!file.isFile) return DeleteOperationResult.NotFound
        return try {
            if (Files.deleteIfExists(file.toPath())) {
                DeleteOperationResult.Deleted
            } else {
                DeleteOperationResult.NotFound
            }
        } catch (error: IOException) {
            DeleteOperationResult.Failure(error.message ?: "DELETE FAILED")
        } catch (error: SecurityException) {
            DeleteOperationResult.Failure(error.message ?: "DELETE FAILED")
        }
    }

    fun update(originalFilename: String, updatedLog: OperationLog): UpdateOperationResult {
        if (!originalFilename.matches(validFilename)) return UpdateOperationResult.OriginalNotFound
        val originalFile = File(operationsDirectory, originalFilename)
        if (!originalFile.isFile) return UpdateOperationResult.OriginalNotFound
        val updatedBase = OperationInputValidator.dateToFilenameBase(updatedLog.date)
            ?: return UpdateOperationResult.Failure("INVALID DATE")
        val updatedFilename = "$updatedBase.json"
        val updatedFile = File(operationsDirectory, updatedFilename)
        if (updatedFilename != originalFilename && updatedFile.exists()) {
            return UpdateOperationResult.Conflict(updatedFilename)
        }

        val temporaryFile = runCatching {
            Files.createTempFile(operationsDirectory.toPath(), ".operation-update-", ".tmp")
        }.getOrElse { error ->
            return UpdateOperationResult.Failure(error.message ?: "UPDATE FAILED")
        }

        return try {
            updateWriter(temporaryFile, OperationJsonCodec.serialize(updatedLog))
            moveFile(temporaryFile, updatedFile.toPath(), updatedFilename == originalFilename)
            if (updatedFilename != originalFilename) {
                try {
                    Files.delete(originalFile.toPath())
                } catch (error: Exception) {
                    Files.deleteIfExists(updatedFile.toPath())
                    return UpdateOperationResult.Failure(error.message ?: "UPDATE FAILED")
                }
            }
            UpdateOperationResult.Updated(updatedFilename)
        } catch (error: Exception) {
            Files.deleteIfExists(temporaryFile)
            UpdateOperationResult.Failure(error.message ?: "UPDATE FAILED")
        }
    }

    private fun moveFile(source: java.nio.file.Path, target: java.nio.file.Path, replace: Boolean) {
        val options = if (replace) {
            arrayOf(StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        } else {
            arrayOf(StandardCopyOption.ATOMIC_MOVE)
        }
        try {
            Files.move(source, target, *options)
        } catch (_: AtomicMoveNotSupportedException) {
            val fallback = if (replace) arrayOf(StandardCopyOption.REPLACE_EXISTING) else emptyArray()
            Files.move(source, target, *fallback)
        }
    }

    companion object {
        fun from(context: Context): OperationRepository = OperationRepository(
            File(context.filesDir, "data/operations")
        )
    }
}
