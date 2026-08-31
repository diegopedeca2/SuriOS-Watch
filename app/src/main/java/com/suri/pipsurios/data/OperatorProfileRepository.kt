package com.suri.pipsurios.data

import android.content.Context
import androidx.core.content.edit

/** Private, durable storage for the operator profile. */
class OperatorProfileRepository private constructor(
    private val preferences: android.content.SharedPreferences
) {
    fun load(): OperatorProfile = OperatorProfile(
        id = preferences.getString(KEY_ID, "").orEmpty(),
        name = preferences.getString(KEY_NAME, "").orEmpty(),
        callsign = preferences.getString(KEY_CALLSIGN, "").orEmpty(),
        number = preferences.getString(KEY_NUMBER, "").orEmpty(),
        country = preferences.getString(KEY_COUNTRY, "").orEmpty(),
        team = preferences.getString(KEY_TEAM, "").orEmpty()
    )

    fun save(profile: OperatorProfile) {
        val normalized = profile.normalized()
        preferences.edit {
            putString(KEY_ID, normalized.id)
            putString(KEY_NAME, normalized.name)
            putString(KEY_CALLSIGN, normalized.callsign)
            putString(KEY_NUMBER, normalized.number)
            putString(KEY_COUNTRY, normalized.country)
            putString(KEY_TEAM, normalized.team)
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "operator_profile"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_CALLSIGN = "callsign"
        private const val KEY_NUMBER = "number"
        private const val KEY_COUNTRY = "country"
        private const val KEY_TEAM = "team"

        fun from(context: Context): OperatorProfileRepository = OperatorProfileRepository(
            context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        )
    }
}
