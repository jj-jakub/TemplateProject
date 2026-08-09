package com.jj.templateproject.domain.theme

import com.jj.templateproject.domain.preferences.AppPreferencesRepository

/** Persists the user's [ThemeMode] choice. */
class SetThemeModeUseCase(
    private val appPreferencesRepository: AppPreferencesRepository,
) {
    suspend operator fun invoke(mode: ThemeMode) = appPreferencesRepository.setThemeMode(mode)
}
