package com.jj.templateproject.presentation.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.colorBackground
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
            .background(colorBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        Column {
            TextField(stringResource(R.string.navigation_testing))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_without_optional_args),
                        onClick = navigateWithoutOptionalArgs,
                    )
                    ActionButton(
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
                    ActionButton(
                        modifier = Modifier
                            .weight(1f)
                            .height(ACTION_BUTTON_HEIGHT.dp),
                        text = stringResource(R.string.navigate_with_second_optional_arg),
                        onClick = navigateWithSecondOptionalArg,
                    )
                    ActionButton(
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

@Composable
fun ActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        modifier = modifier,
    ) {
        Text(text = text)
    }
}

@Composable
private fun TextField(text: String) {
    Text(
        modifier = Modifier.padding(
            bottom = gridMultiple(i = 2)
        ),
        text = text,
    )
}

@Preview
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
