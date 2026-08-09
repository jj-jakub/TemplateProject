package com.jj.templateproject.presentation.navigation.model

import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object MainScreen : Route

    @Serializable
    data class SecondaryScreen(
        val text: String = "",
        val textSecondary: String? = null,
        val textTertiary: String? = null,
    ) : Route

    @Serializable
    data object SettingsScreen : Route
}

// Helper function to check if current navigation entry matches this route type
fun Route.matchesCurrentEntry(currentEntry: NavBackStackEntry?): Boolean {
    val currentRoute = currentEntry?.destination?.route ?: return false
    val routeClassName = this::class.simpleName ?: return false
    return currentRoute.contains(routeClassName)
}

// Helper function to find matching nav item index
fun List<Route>.findSelectedIndex(currentEntry: NavBackStackEntry?): Int {
    return this.indexOfFirst { it.matchesCurrentEntry(currentEntry) }.takeIf { it >= 0 } ?: 0
}
