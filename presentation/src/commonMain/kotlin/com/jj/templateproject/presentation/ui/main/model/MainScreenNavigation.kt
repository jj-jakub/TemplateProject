package com.jj.templateproject.presentation.ui.main.model

import com.jj.templateproject.presentation.navigation.model.Route

sealed class MainScreenNavigation(val route: Route) {
    data class SecondaryScreen(
        val text: String,
        val secondaryText: String?,
        val tertiaryText: String?,
    ) : MainScreenNavigation(
        Route.SecondaryScreen(
            text = text,
            textSecondary = secondaryText,
            textTertiary = tertiaryText,
         )
    )
}
