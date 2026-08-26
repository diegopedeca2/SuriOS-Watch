package com.suri.pipsurios.sonartesting

object CalibrationCsv {
    val columns = listOf(
        "session_id", "sample_id", "test_type", "target", "position_id", "north_m",
        "east_m", "condition", "timestamp_epoch_ms", "elapsed_ms", "rssi_raw",
        "rssi_smoothed", "category", "state", "contact_age_ms", "scan_count", "event", "notes"
    )

    fun encode(records: Iterable<CalibrationRecord>): String = buildString {
        appendLine(columns.joinToString(","))
        records.forEach { record ->
            appendLine(
                listOf(
                    record.sessionId, record.sampleId, record.testType.name, record.target,
                    record.positionId, record.northMeters, record.eastMeters, record.condition,
                    record.timestampEpochMillis, record.elapsedMillis, record.rawRssi,
                    record.smoothedRssi, record.category?.name?.replace('_', ' '), record.state?.name,
                    record.contactAgeMillis, record.scanCount, record.event.name, record.notes
                ).joinToString(",") { escape(it?.toString().orEmpty()) }
            )
        }
    }

    private fun escape(value: String): String = if (value.any { it == ',' || it == '"' || it == '\n' || it == '\r' }) {
        "\"${value.replace("\"", "\"\"")}\""
    } else value
}
