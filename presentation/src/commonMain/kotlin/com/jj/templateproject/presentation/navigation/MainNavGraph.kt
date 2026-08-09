package com.jj.templateproject.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jj.templateproject.presentation.generated.resources.Res
import com.jj.templateproject.presentation.generated.resources.nav_home
import com.jj.templateproject.presentation.generated.resources.nav_secondary
import com.jj.templateproject.presentation.generated.resources.nav_settings
import com.jj.templateproject.presentation.navigation.model.Route
import com.jj.templateproject.presentation.navigation.model.findSelectedIndex
import com.jj.templateproject.presentation.ui.main.MainScreen
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreen
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreen
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
) {
    // Text-only labels rather than icons: material-icons-core's Compose Multiplatform release line
    // is not obviously version-aligned with the Compose Multiplatform version this module builds
    // against, and three demo nav-bar icons are not worth pinning an unverified pairing over. An
    // icon set can be reintroduced once a compatible one is confirmed.
    val navItems = listOf(
        NavItem(Route.MainScreen, stringResource(Res.string.nav_home)),
        NavItem(Route.SecondaryScreen(), stringResource(Res.string.nav_secondary)),
        NavItem(Route.SettingsScreen, stringResource(Res.string.nav_settings)),
    )

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedIndex = navItems.map { it.route }.findSelectedIndex(currentBackStackEntry)

    Scaffold(
        bottomBar = {
            NavigationBar {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {},
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Route.MainScreen,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Route.MainScreen> {
                val viewModel: MainScreenViewModel = koinViewModel()
                MainScreen(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
            composable<Route.SecondaryScreen> {
                val viewModel: SecondaryScreenViewModel = koinViewModel()
                SecondaryScreen(
                    viewModel = viewModel,
                )
            }
            composable<Route.SettingsScreen> {
                val viewModel: SettingsScreenViewModel = koinViewModel()
                SettingsScreen(
                    viewModel = viewModel,
                )
            }
        }
    }
}

private data class NavItem(val route: Route, val label: String)
