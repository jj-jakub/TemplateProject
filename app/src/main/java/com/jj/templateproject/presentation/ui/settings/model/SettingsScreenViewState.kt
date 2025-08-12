package com.jj.templateproject.presentation.ui.settings.model

data class SettingsScreenViewState(
    val loading: Boolean = false,
    val versionText: String = "",
    val apiCallStatus: String = "",
    val apiCallData: String = "",
    val installedFromValidSource: Boolean? = null,
    val requiredPermissions: List<String> = emptyList(),
)
