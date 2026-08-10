package com.jj.templateproject.presentation.ui.settings.model

import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.domain.game.SavedGameState
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.state.UiState

data class SettingsScreenViewState(
    val versionText: String = "",
    val apiState: UiState<ApiData> = UiState.Loading,
    val installedFromValidSource: Boolean? = null,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    /** `null` means "nothing saved to the demo slot yet", not "still loading". */
    val savedGameState: SavedGameState? = null,
    val unlockedAchievements: Set<Achievement> = emptySet(),
    val currentStreak: Int = 0,
    val reminderEnabled: Boolean = false,
)

/** The data fetched from the API, shown once [SettingsScreenViewState.apiState] is a success. */
data class ApiData(
    val status: String,
    val data: String,
)
