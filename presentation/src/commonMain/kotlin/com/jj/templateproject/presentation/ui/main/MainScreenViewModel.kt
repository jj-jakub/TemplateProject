package com.jj.templateproject.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    adManager: AdManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(MainScreenViewState(loading = true))
    val viewState = _viewState.asStateFlow()

    private val _navigation = MutableSharedFlow<MainScreenNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        adManager.incrementActionsForAd()
    }

    fun navigateWithoutOptionalArgs() {
        navigate(
            MainScreenNavigation.SecondaryScreen(
                text = "First text1",
                secondaryText = null,
                tertiaryText = null,
            )
        )
    }

    fun navigateWithFirstOptionalArg() {
        navigate(
            MainScreenNavigation.SecondaryScreen(
                text = "First text1",
                secondaryText = "Secondary text2",
                tertiaryText = null,
            )
        )
    }

    fun navigateWithSecondOptionalArg() {
        navigate(
            MainScreenNavigation.SecondaryScreen(
                text = "First text1",
                secondaryText = null,
                tertiaryText = "Tertiary text3",
            )
        )
    }

    fun navigateWithAllOptionalArgs() {
        navigate(
            MainScreenNavigation.SecondaryScreen(
                text = "First text1",
                secondaryText = "Secondary text2",
                tertiaryText = "Tertiary text3",
            )
        )
    }

    private fun navigate(mainScreenNavigation: MainScreenNavigation) {
        viewModelScope.launch {
            _navigation.emit(mainScreenNavigation)
        }
    }
}
