package com.jj.templateproject.presentation.ui.secondary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jj.templateproject.navigation.model.Argument.SecondaryScreenArgument
import com.jj.templateproject.presentation.ui.secondary.model.SecondaryScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SecondaryScreenViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val name = savedStateHandle.get<String>(SecondaryScreenArgument.Text.name) ?: ""
    private val secondaryName =
        savedStateHandle.get<String>(SecondaryScreenArgument.TextOptionalSecondary.name) ?: ""
    private val tertiaryName =
        savedStateHandle.get<String>(SecondaryScreenArgument.TextOptionalTertiary.name) ?: ""

    private val _viewState = MutableStateFlow(
        SecondaryScreenViewState(
            text = name,
            secondaryText = secondaryName,
            tertiaryText = tertiaryName,
        )
    )
    val viewState = _viewState.asStateFlow()
}