package com.jj.templateproject.presentation.ui.settings.model

import com.jj.templateproject.presentation.ui.state.UiState

data class SettingsScreenViewState(
    val versionText: String = "",
    val apiState: UiState<ApiData> = UiState.Loading,
    val installedFromValidSource: Boolean? = null,
    val requiredPermissions: List<String> = emptyList(),
)

/** The data fetched from the API, shown once [SettingsScreenViewState.apiState] is a success. */
data class ApiData(
    val status: String,
    val data: String,
)
