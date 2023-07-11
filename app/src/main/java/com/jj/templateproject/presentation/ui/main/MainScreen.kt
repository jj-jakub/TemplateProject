package com.jj.templateproject.presentation.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel,
) {
    val state by viewModel.viewState.collectAsState()
    val permissionState = rememberMultiplePermissionsState(
        state.requiredPermissions,
        onPermissionsResult = {
            // TODO
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
        loading = state.loading,
        text = state.text,
        status = state.status,
        data = state.data,
        navigateWithoutOptionalArgs = viewModel::navigateWithoutOptionalArgs,
        navigateWithFirstOptionalArg = viewModel::navigateWithFirstOptionalArg,
        navigateWithSecondOptionalArg = viewModel::navigateWithSecondOptionalArg,
        navigateWithAllOptionalArgs = viewModel::navigateWithAllOptionalArgs,
    )
}

@Composable
private fun MainScreenViewContent(
    loading: Boolean,
    text: String,
    status: String,
    data: String,
    navigateWithoutOptionalArgs: () -> Unit,
    navigateWithFirstOptionalArg: () -> Unit,
    navigateWithSecondOptionalArg: () -> Unit,
    navigateWithAllOptionalArgs: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            modifier = Modifier.padding(
                bottom = gridMultiple(i = 2)
            ),
            text = text,
        )
        Text(
            modifier = Modifier.padding(
                bottom = gridMultiple(i = 2)
            ),
            text = status,
        )
        Text(
            modifier = Modifier.padding(
                bottom = gridMultiple(i = 2)
            ),
            text = data,
        )
        Button(
            onClick = { navigateWithoutOptionalArgs() }
        ) {
            Text(
                text = "Navigate without optional args"
            )
        }
        Button(
            onClick = { navigateWithFirstOptionalArg() }
        ) {
            Text(
                text = "Navigate with first optional arg"
            )
        }
        Button(
            onClick = { navigateWithSecondOptionalArg() }
        ) {
            Text(
                text = "Navigate with second optional arg"
            )
        }
        Button(
            onClick = { navigateWithAllOptionalArgs() }
        ) {
            Text(
                text = "Navigate with all optional args"
            )
        }
        CircularProgressIndicator(
            modifier = Modifier.alpha(if (loading) 1f else 0f)
        )
    }
}

@Preview
@Composable
fun PreviewMainScreenViewContent() {
    TemplateTheme {
        MainScreenViewContent(
            loading = true,
            data = "Data",
            status = "Status",
            text = "Sample text",
            navigateWithoutOptionalArgs = {},
            navigateWithFirstOptionalArg = {},
            navigateWithSecondOptionalArg = {},
            navigateWithAllOptionalArgs = {},
        )
    }
}

