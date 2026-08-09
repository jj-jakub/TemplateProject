package com.jj.templateproject.presentation.navigation.model

import kotlinx.serialization.Serializable

sealed interface GraphNavigation {
    @Serializable
    data object MainNavGraph : GraphNavigation
}
