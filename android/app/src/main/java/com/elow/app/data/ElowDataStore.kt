package com.elow.app.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.elow.app.core.model.GoalSettings
import com.elow.app.core.model.IntakeRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.elowPreferences by preferencesDataStore(name = "elow")

class ElowDataStore(private val context: Context) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    val records: Flow<List<IntakeRecord>> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.records]
            ?.let { stored -> runCatching { json.decodeFromString<List<IntakeRecord>>(stored) }.getOrNull() }
            .orEmpty()
    }

    val goals: Flow<GoalSettings> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.goals]
            ?.let { stored -> runCatching { json.decodeFromString<GoalSettings>(stored) }.getOrNull() }
            ?: GoalSettings()
    }

    val onboardingComplete: Flow<Boolean> = context.elowPreferences.data.map { preferences ->
        preferences[Keys.onboardingComplete] ?: false
    }

    suspend fun saveRecords(records: List<IntakeRecord>) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.records] = json.encodeToString(records)
        }
    }

    suspend fun saveGoals(settings: GoalSettings) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.goals] = json.encodeToString(settings)
        }
    }

    suspend fun saveOnboardingComplete(complete: Boolean) {
        context.elowPreferences.edit { preferences ->
            preferences[Keys.onboardingComplete] = complete
        }
    }

    private object Keys {
        val records = stringPreferencesKey("records_json")
        val goals = stringPreferencesKey("goals_json")
        val onboardingComplete = booleanPreferencesKey("onboarding_complete")
    }
}

