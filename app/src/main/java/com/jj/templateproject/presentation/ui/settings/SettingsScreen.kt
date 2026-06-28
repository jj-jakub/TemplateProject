package com.jj.templateproject.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.design.components.BodyText
import com.jj.templateproject.design.components.PrimaryButton
import com.jj.templateproject.design.components.SecondaryButton
import com.jj.templateproject.design.components.SectionHeader
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.state.UiState
import com.jj.templateproject.presentation.ui.state.UiStateContent

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()
    val permissionState = rememberMultiplePermissionsState(
        state.requiredPermissions,
        onPermissionsResult = { _: Map<String, Boolean> ->
            // Template seam: react to the grant/denial result here, e.g. forward it to the
            // ViewModel to update UI state or show a rationale. Intentionally a no-op.
        },
    )
    LaunchedEffect(key1 = Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    SettingsScreenViewContent(
        versionText = state.versionText,
        apiState = state.apiState,
        installedFromValidSource = state.installedFromValidSource,
        themeMode = state.themeMode,
        onRetry = viewModel::retry,
        onSelectTheme = viewModel::setThemeMode,
    )
}

@Composable
private fun SettingsScreenViewContent(
    versionText: String,
    apiState: UiState<ApiData>,
    installedFromValidSource: Boolean?,
    themeMode: ThemeMode,
    onRetry: () -> Unit,
    onSelectTheme: (ThemeMode) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            SettingsTextField(text = stringResource(R.string.version, versionText))

            UiStateContent(state = apiState, onRetry = onRetry) { apiData ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    SettingsTextField(text = stringResource(R.string.api_call_status, apiData.status))
                    SettingsTextField(text = stringResource(R.string.api_call_data, apiData.data))
                }
            }

            SettingsTextField(
                text = stringResource(
                    R.string.installed_from_valid_source_value,
                    installedFromValidSource?.toString() ?: stringResource(R.string.loading),
                ),
            )

            ThemeSelector(selected = themeMode, onSelect = onSelectTheme)
        }
    }
}

@Composable
private fun ThemeSelector(
    selected: ThemeMode,
    onSelect: (ThemeMode) -> Unit,
) {
    val options = listOf(
        ThemeMode.SYSTEM to R.string.theme_system,
        ThemeMode.LIGHT to R.string.theme_light,
        ThemeMode.DARK to R.string.theme_dark,
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp),
    ) {
        SectionHeader(text = stringResource(R.string.theme_section))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { (mode, labelRes) ->
                val label = stringResource(labelRes)
                if (mode == selected) {
                    PrimaryButton(text = label, onClick = { onSelect(mode) })
                } else {
                    SecondaryButton(text = label, onClick = { onSelect(mode) })
                }
            }
        }
    }
}

@Composable
private fun SettingsTextField(text: String) {
    BodyText(
        text = text,
        modifier = Modifier.padding(
            bottom = gridMultiple(i = 1)
        ),
    )
}

@ThemePreviews
@Composable
fun PreviewSettingsScreenViewContent() {
    TemplateTheme {
        SettingsScreenViewContent(
            versionText = "Version text",
            apiState = UiState.Success(ApiData(status = "Ok", data = "200")),
            installedFromValidSource = null,
            themeMode = ThemeMode.SYSTEM,
            onRetry = {},
            onSelectTheme = {},
        )
    }
}
