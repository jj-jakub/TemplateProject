package com.jj.templateproject.core.data.preferences

import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSUserDefaults

/**
 * [AppPreferencesRepository] backed by `NSUserDefaults`, the iOS counterpart of `:app`'s DataStore-
 * backed implementation. `NSUserDefaults` has no observation API of its own (unlike DataStore's
 * `Flow`), so each preference is mirrored into a [MutableStateFlow] seeded from the store at
 * construction and updated on every write, the same read-through/write-through shape
 * `UserDefaultsLaunchAttemptStore` uses for a non-reactive value.
 */
class UserDefaultsAppPreferencesRepository(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : AppPreferencesRepository {

    private val onboardingCompletedState = MutableStateFlow(
        defaults.boolForKey(KEY_ONBOARDING_COMPLETED),
    )
    override val onboardingCompleted = onboardingCompletedState.asStateFlow()

    override suspend fun setOnboardingCompleted(completed: Boolean) {
        defaults.setBool(completed, KEY_ONBOARDING_COMPLETED)
        onboardingCompletedState.value = completed
    }

    private val themeModeState = MutableStateFlow(readThemeMode())
    override val themeMode = themeModeState.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        defaults.setObject(mode.name, KEY_THEME_MODE)
        themeModeState.value = mode
    }

    private fun readThemeMode(): ThemeMode =
        (defaults.stringForKey(KEY_THEME_MODE))
            ?.let { stored -> runCatching { ThemeMode.valueOf(stored) }.getOrNull() }
            ?: ThemeMode.SYSTEM

    private companion object {
        const val KEY_ONBOARDING_COMPLETED = "app_preferences.onboarding_completed"
        const val KEY_THEME_MODE = "app_preferences.theme_mode"
    }
}
