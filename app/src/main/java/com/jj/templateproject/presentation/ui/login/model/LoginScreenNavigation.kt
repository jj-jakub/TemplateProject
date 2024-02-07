package com.jj.templateproject.presentation.ui.login.model

import com.jj.templateproject.navigation.model.Route
import com.jj.templateproject.navigation.model.createPath

sealed class LoginScreenNavigation(val route: String) {
    data object MainScreen : LoginScreenNavigation(
        Route.MainScreen.createPath()
    )

    data object CreateAccountScreen : LoginScreenNavigation(
        Route.MainScreen.createPath()
    )
}
