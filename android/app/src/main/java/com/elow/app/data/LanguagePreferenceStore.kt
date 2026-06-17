package com.elow.app.data

import android.content.Context
import com.elow.app.core.model.AppLanguage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface LanguagePreferenceStore {
    val language: StateFlow<AppLanguage>

    suspend fun setLanguage(language: AppLanguage)
}

class SharedPreferencesLanguagePreferenceStore(context: Context) : LanguagePreferenceStore {
    private val preferences = context.getSharedPreferences("elow_language", Context.MODE_PRIVATE)
    private val languageState = MutableStateFlow(AppLanguage.fromCode(preferences.getString(KEY_LANGUAGE, null)))

    override val language: StateFlow<AppLanguage> = languageState.asStateFlow()

    override suspend fun setLanguage(language: AppLanguage) {
        preferences.edit().putString(KEY_LANGUAGE, language.code).apply()
        languageState.value = language
    }

    private companion object {
        const val KEY_LANGUAGE = "language"
    }
}

class InMemoryLanguagePreferenceStore(
    initialLanguage: AppLanguage = AppLanguage.ENGLISH
) : LanguagePreferenceStore {
    private val languageState = MutableStateFlow(initialLanguage)

    override val language: StateFlow<AppLanguage> = languageState.asStateFlow()

    override suspend fun setLanguage(language: AppLanguage) {
        languageState.value = language
    }
}
