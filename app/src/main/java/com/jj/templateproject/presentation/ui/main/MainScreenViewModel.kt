package com.jj.templateproject.presentation.ui.main

import android.Manifest
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.core.data.google.GetGoogleDataUseCase
import com.jj.templateproject.core.data.google.GetGoogleStatusUseCase
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    versionTextProvider: VersionTextProvider,
    private val getGoogleStatusUseCase: GetGoogleStatusUseCase,
    private val getGoogleDataUseCase: GetGoogleDataUseCase,
    adManager: AdManager,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        MainScreenViewState(
            loading = true,
            requiredPermissions = getRequiredPermissions(),
        )
    )

    val viewState = _viewState.asStateFlow()

    init {
        _viewState.value = viewState.value.copy(
            text = versionTextProvider.getAboutVersionText(),
        )

        fetchGoogleData()
        adManager.incrementActionsForAd()
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

    private fun getRequiredPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            emptyList()
        }
}