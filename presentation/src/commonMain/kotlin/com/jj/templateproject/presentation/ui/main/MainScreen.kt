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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jj.templateproject.design.components.PrimaryButton
import com.jj.templateproject.design.components.SecondaryButton
import com.jj.templateproject.design.components.SectionHeader
import com.jj.templateproject.design.gridMultiple
import com.jj.templateproject.presentation.RequestNotificationPermissionOnLaunch
import com.jj.templateproject.presentation.generated.resources.Res
import com.jj.templateproject.presentation.generated.resources.navigate_with_all_optional_args
import com.jj.templateproject.presentation.generated.resources.navigate_with_first_optional_arg
import com.jj.templateproject.presentation.generated.resources.navigate_with_second_optional_arg
import com.jj.templateproject.presentation.generated.resources.navigate_without_optional_args
import com.jj.templateproject.presentation.generated.resources.navigation_testing
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
import org.jetbrains.compose.resources.stringResource

private const val ACTION_BUTTON_HEIGHT = 80

@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainScreenViewModel,
) {
    RequestNotificationPermissionOnLaunch()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigation.collect { navigation ->
            when (navigation) {
                is MainScreenNavigation.SecondaryScreen -> navController.navigate(
                    route = navigation.route,
                )
            }
        }
    }

    val state by viewModel.viewState.collectAsState()

    MainScreenViewContent(
        navigateWithoutOptionalArgs = viewModel::navigateWithoutOptionalArgs,
        navigateWithFirstOptionalArg = viewModel::navigateWithFirstOptionalArg,
        navigateWithSecondOptionalArg = viewModel::navigateWithSecondOptionalArg,
        navigateWithAllOptionalArgs = viewModel::navigateWithAllOptionalArgs,
        crossPromoLabel = state.crossPromoLabel,
        onCrossPromoClicked = viewModel::onCrossPromoClicked,
    )
}

// internal, not private: the Android-only @ThemePreviews composable that renders this lives in a
// separate androidMain file (previews are Android Studio/Xcode Previews tooling, not shared
// runtime code — see :design's own ThemePreviews doc comment), and Kotlin's file-scoped `private`
// would keep that file from calling this.
@Composable
internal fun MainScreenViewContent(
    navigateWithoutOptionalArgs: () -> Unit,
    navigateWithFirstOptionalArg: () -> Unit,
    navigateWithSecondOptionalArg: () -> Unit,
    navigateWithAllOptionalArgs: () -> Unit,
    crossPromoLabel: String?,
    onCrossPromoClicked: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
    ) {
        NavigationTestingSection(
            navigateWithoutOptionalArgs = navigateWithoutOptionalArgs,
            navigateWithFirstOptionalArg = navigateWithFirstOptionalArg,
            navigateWithSecondOptionalArg = navigateWithSecondOptionalArg,
            navigateWithAllOptionalArgs = navigateWithAllOptionalArgs,
        )

        if (crossPromoLabel != null) {
            SecondaryButton(text = crossPromoLabel, onClick = onCrossPromoClicked)
        }
    }
}

@Composable
private fun NavigationTestingSection(
    navigateWithoutOptionalArgs: () -> Unit,
    navigateWithFirstOptionalArg: () -> Unit,
    navigateWithSecondOptionalArg: () -> Unit,
    navigateWithAllOptionalArgs: () -> Unit,
) {
    Column {
        SectionHeader(
            text = stringResource(Res.string.navigation_testing),
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
                    text = stringResource(Res.string.navigate_without_optional_args),
                    onClick = navigateWithoutOptionalArgs,
                )
                PrimaryButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(ACTION_BUTTON_HEIGHT.dp),
                    text = stringResource(Res.string.navigate_with_first_optional_arg),
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
                    text = stringResource(Res.string.navigate_with_second_optional_arg),
                    onClick = navigateWithSecondOptionalArg,
                )
                PrimaryButton(
                    modifier = Modifier
                        .weight(1f)
                        .height(ACTION_BUTTON_HEIGHT.dp),
                    text = stringResource(Res.string.navigate_with_all_optional_args),
                    onClick = navigateWithAllOptionalArgs,
                )
            }
        }
    }
}
