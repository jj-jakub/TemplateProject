package com.jj.templateproject.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.crosspromo.CrossPromoConfig
import com.jj.templateproject.domain.crosspromo.GetCrossPromoConfigUseCase
import com.jj.templateproject.domain.crosspromo.UrlOpener
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainScreenViewModel(
    adManager: AdManager,
    private val getCrossPromoConfigUseCase: GetCrossPromoConfigUseCase,
    private val urlOpener: UrlOpener,
) : ViewModel() {

    // Held privately rather than reconstructed from the label in view state: the label is the only
    // part of the config a screen needs to render, but tapping the row needs the resolved target
    // URL too, and re-deriving that from a String would mean parsing it back out of the label.
    private var crossPromoConfig: CrossPromoConfig? = null

    private val _viewState = MutableStateFlow(MainScreenViewState(loading = true))
    val viewState = _viewState.asStateFlow()

    private val _navigation = MutableSharedFlow<MainScreenNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        adManager.incrementActionsForAd()
        crossPromoConfig = getCrossPromoConfigUseCase()
        _viewState.update { it.copy(crossPromoLabel = crossPromoConfig?.label) }
    }

    /** No-op when cross-promotion is off — the row that calls this doesn't render in that case. */
    fun onCrossPromoClicked() {
        crossPromoConfig?.let { urlOpener.open(it.target) }
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
