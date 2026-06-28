package com.jj.templateproject.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.framework.navigation.MainNavGraph
import com.jj.templateproject.presentation.ui.ads.ComposeAdView

@Composable
fun MainRoot(
    navController: NavHostController,
    viewModel: MainRootViewModel,
) {
    val state by viewModel.viewState.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isInDarkMode = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    TemplateTheme(isInDarkMode = isInDarkMode) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            Column(
                // Inset the top (status bar) and sides (display cutout); the bottom
                // NavigationBar inside the Scaffold consumes the navigation-bar inset.
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ComposeAdView(
                    adUnitId = state.adMainUnitId,
                    onAdClicked = viewModel::onAdClicked,
                )
                MainNavGraph(
                    navController = navController,
                )
            }
        }
    }
}
