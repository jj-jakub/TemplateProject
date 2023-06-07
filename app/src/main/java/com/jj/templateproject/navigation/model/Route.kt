package com.jj.templateproject.navigation.model

sealed class Route(val route: String) {
    object MainScreen : Route("main_screen")
}