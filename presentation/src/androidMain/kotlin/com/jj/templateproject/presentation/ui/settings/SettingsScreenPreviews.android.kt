package com.jj.templateproject.presentation.ui.settings

import androidx.compose.runtime.Composable
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.state.UiState

@ThemePreviews
@Composable
private fun PreviewSettingsScreenViewContent() {
    TemplateTheme {
        SettingsScreenViewContent(
            versionText = "Version text",
            apiState = UiState.Success(ApiData(status = "Ok", data = "200")),
            installedFromValidSource = null,
            themeMode = ThemeMode.SYSTEM,
            savedGameState = null,
            onRetry = {},
            onSelectTheme = {},
            onSaveProgress = {},
        )
    }
}
