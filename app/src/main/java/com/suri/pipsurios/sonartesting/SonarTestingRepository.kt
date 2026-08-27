package com.suri.pipsurios.sonartesting

import android.content.Context
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.Properties

class SonarTestingRepository(private val root: File) {
    @Synchronized
    fun createSession(nowEpochMillis: Long = System.currentTimeMillis()): CalibrationSession {
        root.mkdirs()
        val counterFile = File(root, "session-counter.txt")
        val stored = counterFile.takeIf { it.isFile }?.readText()?.trim()?.toIntOrNull() ?: 0
        val existingMax = root.listFiles()?.mapNotNull {
            SESSION_PATTERN.matchEntire(it.name)?.groupValues?.get(1)?.toIntOrNull()
        }?.maxOrNull() ?: 0
        val next = maxOf(stored, existingMax) + 1
        val id = "CAL-${next.toString().padStart(3, '0')}"
        val session = CalibrationSession(id, nowEpochMillis)
        val sessionDir = File(root, id).apply { mkdirs() }
        writePropertiesAtomic(
            File(sessionDir, "session.properties"),
            Properties().apply {
                setProperty("sessionId", id)
                setProperty("createdAtEpochMillis", nowEpochMillis.toString())
                setProperty("receiver", session.receiver)
            }
        )
        writeTextAtomic(counterFile, next.toString())
        return session
    }

    @Synchronized
    fun nextSampleId(sessionId: String): String {
        val sessionDir = requireSessionDirectory(sessionId)
        val counter = File(sessionDir, "sample-counter.txt")
        val next = (counter.takeIf { it.isFile }?.readText()?.trim()?.toIntOrNull() ?: 0) + 1
        writeTextAtomic(counter, next.toString())
        return "$sessionId-S${next.toString().padStart(3, '0')}"
    }

    @Synchronized
    fun append(record: CalibrationRecord) {
        val file = File(requireSessionDirectory(record.sessionId), "observations.csv")
        if (!file.exists()) {
            file.writeText(CalibrationCsv.columns.joinToString(",") + "\n", Charsets.UTF_8)
        }
        val row = CalibrationCsv.encode(listOf(record)).lineSequence().drop(1).first() + "\n"
        file.appendText(row, Charsets.UTF_8)
    }

    fun exportFile(sessionId: String): File? =
        File(requireSessionDirectory(sessionId), "observations.csv").takeIf { it.isFile }

    fun readSession(sessionId: String): CalibrationSession? {
        val file = File(root, "$sessionId/session.properties")
        if (!file.isFile) return null
        return runCatching {
            val properties = Properties().apply { file.inputStream().use(::load) }
            CalibrationSession(
                properties.getProperty("sessionId"),
                properties.getProperty("createdAtEpochMillis").toLong(),
                properties.getProperty("receiver")
            )
        }.getOrNull()
    }

    private fun requireSessionDirectory(sessionId: String): File {
        require(SESSION_PATTERN.matches(sessionId)) { "Invalid session ID" }
        return File(root, sessionId).also { require(it.isDirectory) { "Unknown session" } }
    }

    private fun writeTextAtomic(target: File, content: String) {
        target.parentFile?.mkdirs()
        val temporary = File.createTempFile(".sonar-testing-", ".tmp", target.parentFile)
        temporary.writeText(content, Charsets.UTF_8)
        Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    private fun writePropertiesAtomic(target: File, properties: Properties) {
        val temporary = File.createTempFile(".sonar-testing-", ".tmp", target.parentFile)
        temporary.outputStream().use { properties.store(it, "PIP-SuriOS SONAR-TESTING") }
        Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
    }

    companion object {
        private val SESSION_PATTERN = Regex("^CAL-(\\d{3,})$")
        fun from(context: Context) = SonarTestingRepository(File(context.filesDir, "sonar-testing"))
    }
}
