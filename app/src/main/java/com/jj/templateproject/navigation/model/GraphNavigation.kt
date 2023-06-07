package com.jj.templateproject.navigation.model

sealed class GraphNavigation(val route: String) {
    object MainNavGraph : GraphNavigation(route = "main_nav_graph")
}