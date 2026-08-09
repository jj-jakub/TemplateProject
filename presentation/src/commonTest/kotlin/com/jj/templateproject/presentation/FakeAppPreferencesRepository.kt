package com.jj.templateproject.presentation

import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** The same fake as `:domain`'s own `FakeAppPreferencesRepository`, duplicated rather than shared:
 *  test sources are not visible across module boundaries in this project's Gradle setup. */
class FakeAppPreferencesRepository(
    onboardingCompleted: Boolean = false,
    themeMode: ThemeMode = ThemeMode.SYSTEM,
) : AppPreferencesRepository {

    private val onboardingState = MutableStateFlow(onboardingCompleted)
    private val themeState = MutableStateFlow(themeMode)

    override val onboardingCompleted: Flow<Boolean> = onboardingState
    override val themeMode: Flow<ThemeMode> = themeState

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        onboardingState.value = completed
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        themeState.value = mode
    }
}
