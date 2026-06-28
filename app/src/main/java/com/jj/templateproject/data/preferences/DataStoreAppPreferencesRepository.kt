package com.jj.templateproject.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * [AppPreferencesRepository] backed by Jetpack [DataStore]. The [DataStore] is injected (rather
 * than built from a `Context` internally) so it can be pointed at a temp file in unit tests.
 */
class DataStoreAppPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) : AppPreferencesRepository {

    override val onboardingCompleted: Flow<Boolean> =
        dataStore.data.map { preferences -> preferences[ONBOARDING_COMPLETED] ?: false }

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { preferences -> preferences[ONBOARDING_COMPLETED] = completed }
    }

    private companion object {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
