package com.jj.templateproject.domain.theme

import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import kotlinx.coroutines.flow.Flow

/** Observes the persisted [ThemeMode], defaulting to [ThemeMode.SYSTEM]. */
class GetThemeModeUseCase(
    private val appPreferencesRepository: AppPreferencesRepository,
) {
    operator fun invoke(): Flow<ThemeMode> = appPreferencesRepository.themeMode
}
