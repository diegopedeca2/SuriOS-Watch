package com.suri.pipsurios.data

/**
 * Personal identity data shared by the application modules.
 *
 * This profile is intentionally independent from the current loadout. Future
 * screens can use it for login text, replicas, roles and operational labels
 * without coupling those screens to the operator setup UI.
 */
data class OperatorProfile(
    val id: String = "",
    val name: String = "",
    val callsign: String = "",
    val number: String = "",
    val country: String = "",
    val team: String = ""
) {
    fun normalized(): OperatorProfile = copy(
        id = id.trim(),
        name = name.trim(),
        callsign = callsign.trim(),
        number = number.trim(),
        country = country.trim(),
        team = team.trim()
    )

    fun update(field: OperatorField, value: String): OperatorProfile = when (field) {
        OperatorField.ID -> copy(id = value)
        OperatorField.NAME -> copy(name = value)
        OperatorField.CALLSIGN -> copy(callsign = value)
        OperatorField.NUMBER -> copy(number = value)
        OperatorField.COUNTRY -> copy(country = value)
        OperatorField.TEAM -> copy(team = value)
    }

    fun valueFor(field: OperatorField): String = when (field) {
        OperatorField.ID -> id
        OperatorField.NAME -> name
        OperatorField.CALLSIGN -> callsign
        OperatorField.NUMBER -> number
        OperatorField.COUNTRY -> country
        OperatorField.TEAM -> team
    }
}

enum class OperatorField {
    ID,
    NAME,
    CALLSIGN,
    NUMBER,
    COUNTRY,
    TEAM
}
