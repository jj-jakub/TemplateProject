package com.jj.templateproject.domain.preferences

import com.jj.templateproject.domain.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * In-memory stand-in for [AppPreferencesRepository]: writing a value makes the matching flow emit
 * it, which is the behaviour a real preference store has and the one a test usually wants to
 * observe. Hand-written rather than mocked so the tests using it run on every target.
 */
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
