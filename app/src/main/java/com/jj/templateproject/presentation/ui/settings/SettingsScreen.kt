package com.jj.templateproject.presentation.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.colorBackground
import com.jj.templateproject.design.components.BodyText
import com.jj.templateproject.design.gridMultiple

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
        loading = state.loading,
        versionText = state.versionText,
        apiCallStatus = state.apiCallStatus,
        apiCallData = state.apiCallData,
        installedFromValidSource = state.installedFromValidSource,
    )
}

@Composable
private fun SettingsScreenViewContent(
    loading: Boolean,
    versionText: String,
    apiCallStatus: String,
    apiCallData: String,
    installedFromValidSource: Boolean?,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(colorBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            SettingsTextField(text = stringResource(R.string.version, versionText))
            SettingsTextField(text = stringResource(R.string.api_call_status, apiCallStatus))
            SettingsTextField(text = stringResource(R.string.api_call_data, apiCallData))
            SettingsTextField(
                text = stringResource(R.string.installed_from_valid_source) + ": " +
                    (installedFromValidSource?.toString() ?: stringResource(R.string.loading)),
            )
        }
        CircularProgressIndicator(
            modifier = Modifier
                .alpha(if (loading) 1f else 0f)
                .padding(top = 24.dp)
        )
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

@Preview
@Composable
fun PreviewSettingsScreenViewContent() {
    TemplateTheme {
        SettingsScreenViewContent(
            loading = true,
            apiCallData = "Data",
            apiCallStatus = "Status",
            versionText = "Version text",
            installedFromValidSource = null,
        )
    }
}
