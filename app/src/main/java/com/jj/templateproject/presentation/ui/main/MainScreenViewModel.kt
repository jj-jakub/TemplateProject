package com.jj.templateproject.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.data.google.GetGoogleDataUseCase
import com.jj.templateproject.data.google.GetGoogleStatusUseCase
import com.jj.templateproject.data.text.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    versionTextProvider: VersionTextProvider,
    private val getGoogleStatusUseCase: GetGoogleStatusUseCase,
    private val getGoogleDataUseCase: GetGoogleDataUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        MainScreenViewState(
            loading = true,
        )
    )
    val viewState = _viewState.asStateFlow()

    init {
        _viewState.value = viewState.value.copy(
            text = versionTextProvider.getAboutVersionText(),
        )

        fetchGoogleData()
    }

    private fun fetchGoogleData() {
        viewModelScope.launch {
            val status = when (val result = getGoogleStatusUseCase.invoke()) {
                is BaseResult.Error -> result.exception.message ?: "Failure"
                is BaseResult.Success -> "Ok"
            }

            _viewState.value = viewState.value.copy(
                status = status,
            )

            val data = when (val result = getGoogleDataUseCase.invoke()) {
                is BaseResult.Error -> "Error"
                is BaseResult.Success -> result.data
            }

            _viewState.value = viewState.value.copy(
                data = data,
                loading = false,
            )
        }
    }
}