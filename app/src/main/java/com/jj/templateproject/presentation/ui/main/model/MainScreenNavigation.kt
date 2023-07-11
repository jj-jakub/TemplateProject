package com.jj.templateproject.presentation.ui.main.model

import com.jj.templateproject.navigation.model.Argument.SecondaryScreenArgument
import com.jj.templateproject.navigation.model.Route
import com.jj.templateproject.navigation.model.createPath

sealed class MainScreenNavigation(val route: String) {
    data class SecondaryScreen(
        val text: String,
        val secondaryText: String?,
        val tertiaryText: String?,
    ) : MainScreenNavigation(
        Route.SecondaryScreen.createPath(
            requiredArgsList = listOf(text),
            optionalArgsMap = mapOf(
                SecondaryScreenArgument.TextOptionalSecondary.name to secondaryText,
                SecondaryScreenArgument.TextOptionalTertiary.name to tertiaryText,
            ),
        )
    )
}
