package com.jj.templateproject.presentation.ui.main

import androidx.lifecycle.ViewModel
import com.jj.templateproject.data.text.VersionTextProvider
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel(
    versionTextProvider: VersionTextProvider,
) : ViewModel() {

    private val _viewState = MutableStateFlow(MainScreenViewState())
    val viewState = _viewState.asStateFlow()

    init {
        _viewState.value = viewState.value.copy(
            text = versionTextProvider.getAboutVersionText(),
        )
    }
}