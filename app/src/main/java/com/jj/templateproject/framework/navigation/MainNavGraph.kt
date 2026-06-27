package com.jj.templateproject.framework.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.jj.templateproject.framework.navigation.model.Route
import com.jj.templateproject.framework.navigation.model.findSelectedIndex
import com.jj.templateproject.presentation.ui.main.MainScreen
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreen
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreen
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainNavGraph(
    navController: NavHostController,
) {
    val navItems = listOf(
        NavItem(Route.MainScreen, "Home", Icons.Default.Home),
        NavItem(Route.SecondaryScreen(), "Secondary", Icons.Default.MailOutline),
        NavItem(Route.SettingsScreen, "Settings", Icons.Default.Settings),
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
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label
                            )
                        },
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

private data class NavItem(val route: Route, val label: String, val icon: ImageVector)
