package com.jj.templateproject.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * [AppPreferencesRepository] backed by Jetpack [DataStore]. The [DataStore] is injected (rather
 * than built from a `Context` internally) so it can be pointed at a temp file in unit tests.
 */
class DataStoreAppPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) : AppPreferencesRepository {

    override val onboardingCompleted: Flow<Boolean> =
        dataStore.data
            .recoverFromReadErrors()
            .map { preferences -> preferences[ONBOARDING_COMPLETED] ?: false }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences -> preferences[ONBOARDING_COMPLETED] = completed }
    }

    override val themeMode: Flow<ThemeMode> = dataStore.data
        .recoverFromReadErrors()
        .map { preferences ->
            preferences[THEME_MODE]
                ?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
                ?: ThemeMode.SYSTEM
        }

    override suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences -> preferences[THEME_MODE] = mode.name }
    }

    // A failed DataStore read (corruption / disk error) emits an IOException; fall back to empty
    // preferences (defaults) instead of terminating the collector and freezing the UI.
    private fun Flow<Preferences>.recoverFromReadErrors(): Flow<Preferences> = catch { error ->
        if (error is IOException) emit(emptyPreferences()) else throw error
    }

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
