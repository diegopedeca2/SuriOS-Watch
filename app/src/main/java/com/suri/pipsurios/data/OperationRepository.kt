package com.suri.pipsurios.data

import android.content.Context
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.nio.file.FileAlreadyExistsException
import java.nio.file.Files
import java.nio.file.StandardOpenOption

sealed interface SaveOperationResult {
    data class Saved(val file: File) : SaveOperationResult
    data class AlreadyExists(val file: File) : SaveOperationResult
    data class Failure(val message: String) : SaveOperationResult
}

data class OperationLogEntry(
    val filename: String,
    val log: OperationLog
)

data class OperationLogCollection(
    val entries: List<OperationLogEntry>,
    val unreadableFileCount: Int
)

class OperationRepository(private val operationsDirectory: File) {
    fun save(log: OperationLog): SaveOperationResult {
        val filenameBase = OperationInputValidator.dateToFilenameBase(log.date)
            ?: return SaveOperationResult.Failure("INVALID DATE")
        if (!operationsDirectory.exists() && !operationsDirectory.mkdirs()) {
            return SaveOperationResult.Failure("STORAGE UNAVAILABLE")
        }
        val file = File(operationsDirectory, "$filenameBase.json")
        return try {
            Files.writeString(
                file.toPath(),
                OperationJsonCodec.serialize(log),
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE_NEW,
                StandardOpenOption.WRITE
            )
            SaveOperationResult.Saved(file)
        } catch (_: FileAlreadyExistsException) {
            SaveOperationResult.AlreadyExists(file)
        } catch (error: IOException) {
            SaveOperationResult.Failure(error.message ?: "SAVE FAILED")
        }
    }

    fun read(filename: String): OperationLog? {
        if (!filename.matches(Regex("^\\d{8}\\.json$"))) return null
        val file = File(operationsDirectory, filename)
        if (!file.isFile) return null
        return runCatching { OperationJsonCodec.deserialize(file.readText(Charsets.UTF_8)) }.getOrNull()
    }

    fun listLogs(): List<String> = operationsDirectory.listFiles()
        ?.asSequence()
        ?.filter { it.isFile && it.name.matches(Regex("^\\d{8}\\.json$")) }
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

    companion object {
        fun from(context: Context): OperationRepository = OperationRepository(
            File(context.filesDir, "data/operations")
        )
    }
}
