package com.jj.templateproject.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews
import com.jj.templateproject.design.components.PrimaryButton
import com.jj.templateproject.design.components.SectionHeader
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation

private const val ACTION_BUTTON_HEIGHT = 80

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel,
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

    LaunchedEffect(key1 = Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                is MainScreenNavigation.SecondaryScreen -> navController.navigate(
                    route = navigation.route,
                )
            }
        }
    }

    MainScreenViewContent(
        navigateWithoutOptionalArgs = viewModel::navigateWithoutOptionalArgs,
        navigateWithFirstOptionalArg = viewModel::navigateWithFirstOptionalArg,
        navigateWithSecondOptionalArg = viewModel::navigateWithSecondOptionalArg,
        navigateWithAllOptionalArgs = viewModel::navigateWithAllOptionalArgs,
    )
}

@Composable
private fun MainScreenViewContent(
    navigateWithoutOptionalArgs: () -> Unit,
    navigateWithFirstOptionalArg: () -> Unit,
    navigateWithSecondOptionalArg: () -> Unit,
    navigateWithAllOptionalArgs: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column {
            SectionHeader(
                text = stringResource(R.string.navigation_testing),
                modifier = Modifier.padding(bottom = gridMultiple(i = 2)),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrimaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_without_optional_args),
                        onClick = navigateWithoutOptionalArgs,
                    )
                    PrimaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_with_first_optional_arg),
                        onClick = navigateWithFirstOptionalArg,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrimaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_with_second_optional_arg),
                        onClick = navigateWithSecondOptionalArg,
                    )
                    PrimaryButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_with_all_optional_args),
                        onClick = navigateWithAllOptionalArgs,
                    )
                }
            }
        }
    }
}

@ThemePreviews
@Composable
fun PreviewMainScreenViewContent() {
    TemplateTheme {
        MainScreenViewContent(
            navigateWithoutOptionalArgs = {},
            navigateWithFirstOptionalArg = {},
            navigateWithSecondOptionalArg = {},
            navigateWithAllOptionalArgs = {},
        )
    }
}
