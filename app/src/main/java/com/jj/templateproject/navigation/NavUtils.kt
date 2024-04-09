package com.jj.templateproject.navigation

import androidx.navigation.NavHostController

fun NavHostController.navigateAndClose(route: String) {
    navigate(route) {
        currentBackStackEntry?.destination?.route?.let { currentRoute ->
            popUpTo(currentRoute) { inclusive = true }
        }
    }
}