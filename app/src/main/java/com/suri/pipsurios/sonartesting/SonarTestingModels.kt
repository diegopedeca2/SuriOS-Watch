package com.suri.pipsurios.sonartesting

import com.suri.pipsurios.sonar.ContactState
import com.suri.pipsurios.sonar.ProximityCategory

object SonarTestingTuning {
    const val STATIC_SAMPLE_DURATION_MILLIS = 30_000L
}

enum class CalibrationTarget(val label: String, val optional: Boolean = false) {
    FLIP_6("FLIP 6"), HONOR_8("HONOR 8"), MOTOROLA("MOTOROLA"), WATCH_2("WATCH 2"),
    CHECHU("CHECHU", optional = true)
}

data class CalibrationPosition(
    val id: String,
    val northMeters: Int,
    val eastMeters: Int,
    val condition: String
)

object CalibrationPositions {
    val all = listOf(
        CalibrationPosition("D1", 2, 0, "Vista despejada"),
        CalibrationPosition("D2", 5, 3, "Vista despejada"),
        CalibrationPosition("D3", 5, -3, "Una persona entre A56 y objetivo"),
        CalibrationPosition("D4", 0, 5, "Vista despejada"),
        CalibrationPosition("D5", 0, -5, "Dispositivo dentro de mochila"),
        CalibrationPosition("D6", 7, -8, "Dispositivo detrás de pared"),
        CalibrationPosition("D7", 10, 8, "Vista despejada"),
        CalibrationPosition("D8", -10, 5, "Una persona entre A56 y objetivo"),
        CalibrationPosition("D9", 15, 0, "Vista despejada"),
        CalibrationPosition("D10", 20, 0, "Vista despejada")
    )
}

data class ManualCalibrationPosition(
    val northMeters: Int = 0,
    val southMeters: Int = 0,
    val eastMeters: Int = 0,
    val westMeters: Int = 0,
    val condition: String = ""
) {
    val isValid: Boolean get() = northMeters >= 0 && southMeters >= 0 && eastMeters >= 0 && westMeters >= 0 &&
        !(northMeters > 0 && southMeters > 0) && !(eastMeters > 0 && westMeters > 0)
    val signedNorthMeters get() = northMeters - southMeters
    val signedEastMeters get() = eastMeters - westMeters
    fun display(): String {
        val vertical = if (signedNorthMeters >= 0) "N${signedNorthMeters}" else "S${-signedNorthMeters}"
        val horizontal = if (signedEastMeters >= 0) "E${signedEastMeters}" else "O${-signedEastMeters}"
        return "$vertical / $horizontal"
    }
    fun toCalibrationPosition() = CalibrationPosition("CUSTOM", signedNorthMeters, signedEastMeters, condition)
}

enum class SonarTestingPhase { SET_TEST, IDENTIFY, PLACE_TARGET, RUNNING, RESULT }

object SonarIdentificationTuning {
    const val WINDOW_MILLIS = 5_000L
    const val MIN_OBSERVATIONS = 2
    const val DOMINANCE_MARGIN_DB = 6f
    const val NEW_CONTACT_BONUS_DB = 4f
    const val STRONG_RSSI_THRESHOLD = -55
    const val MAX_AMBIGUOUS_CANDIDATES = 3
}

data class IdentificationCandidate(
    val temporaryId: String,
    val averageRssi: Float,
    val observationCount: Int,
    val newDuringWindow: Boolean
) {
    val score: Float get() = averageRssi + if (newDuringWindow) SonarIdentificationTuning.NEW_CONTACT_BONUS_DB else 0f
}

sealed interface IdentificationResult {
    data class Dominant(val candidate: IdentificationCandidate) : IdentificationResult
    data class Ambiguous(val candidates: List<IdentificationCandidate>) : IdentificationResult
    data object None : IdentificationResult
}

object SonarTargetIdentifier {
    fun identify(observations: Map<String, List<Int>>, baselineIds: Set<String>): IdentificationResult {
        val candidates = observations.mapNotNull { (id, values) ->
            if (values.size < SonarIdentificationTuning.MIN_OBSERVATIONS) null
            else IdentificationCandidate(id, values.average().toFloat(), values.size, id !in baselineIds)
        }.sortedByDescending { it.score }
        if (candidates.isEmpty()) return IdentificationResult.None
        val top = candidates.first()
        val second = candidates.getOrNull(1)
        return if (second == null || top.score - second.score >= SonarIdentificationTuning.DOMINANCE_MARGIN_DB) {
            IdentificationResult.Dominant(top)
        } else IdentificationResult.Ambiguous(candidates.take(SonarIdentificationTuning.MAX_AMBIGUOUS_CANDIDATES))
    }
}

enum class CalibrationTestType { STATIC, MOVEMENT }
enum class CalibrationEvent { OBSERVATION, CONTACT_LOST, CONTACT_RECOVERED, SAMPLE_COMPLETE }

data class CalibrationSession(
    val sessionId: String,
    val createdAtEpochMillis: Long,
    val receiver: String = "Samsung Galaxy A56 — SONAR"
)

data class CalibrationSample(
    val sampleId: String,
    val sessionId: String,
    val type: CalibrationTestType,
    val target: CalibrationTarget,
    val position: CalibrationPosition?,
    val temporaryContactId: String,
    val startedAtEpochMillis: Long,
    val notes: String
)

data class CalibrationRecord(
    val sessionId: String,
    val sampleId: String,
    val testType: CalibrationTestType,
    val target: String,
    val positionId: String?,
    val northMeters: Int?,
    val eastMeters: Int?,
    val condition: String?,
    val timestampEpochMillis: Long,
    val elapsedMillis: Long,
    val rawRssi: Int?,
    val smoothedRssi: Float?,
    val category: ProximityCategory?,
    val state: ContactState?,
    val contactAgeMillis: Long?,
    val scanCount: Int,
    val event: CalibrationEvent,
    val notes: String
)
