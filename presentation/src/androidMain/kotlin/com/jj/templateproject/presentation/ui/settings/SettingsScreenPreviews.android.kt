package com.jj.templateproject.presentation.ui.settings

import androidx.compose.runtime.Composable
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import com.jj.templateproject.presentation.ui.state.UiState

@ThemePreviews
@Composable
private fun PreviewSettingsScreenViewContent() {
    TemplateTheme {
        SettingsScreenViewContent(
            state = SettingsScreenViewState(
                versionText = "Version text",
                apiState = UiState.Success(ApiData(status = "Ok", data = "200")),
                unlockedAchievements = setOf(Achievement.FIRST_SAVE),
                currentStreak = 3,
            ),
            onRetry = {},
            onSelectTheme = {},
            onSaveProgress = {},
            onCheckIn = {},
            onToggleReminder = {},
        )
    }
}
