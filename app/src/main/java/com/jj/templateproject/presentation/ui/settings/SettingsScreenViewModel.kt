package com.jj.templateproject.presentation.ui.settings

import android.Manifest
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import com.jj.templateproject.presentation.ui.state.UiState
import com.jj.templateproject.presentation.ui.state.map
import com.jj.templateproject.presentation.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    versionTextProvider: VersionTextProvider,
    private val getGoogleStatusUseCase: GetGoogleStatusUseCase,
    private val getGoogleDataUseCase: GetGoogleDataUseCase,
    private val getIsInstalledFromValidSource: GetIsInstalledFromValidSource,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsScreenViewState(
            versionText = versionTextProvider.getAboutVersionText(),
            requiredPermissions = getRequiredPermissions(),
        )
    )
    val viewState: StateFlow<SettingsScreenViewState> = _viewState.asStateFlow()

    init {
        fetchApiData()
        fetchInstallationValidity()
    }

    /** Re-runs the API fetch; wired to the error state's Retry action. */
    fun retry() {
        fetchApiData()
    }

    private fun fetchApiData() {
        _viewState.update { it.copy(apiState = UiState.Loading) }
        viewModelScope.launch {
            val apiState = loadApiData()
            _viewState.update { it.copy(apiState = apiState) }
        }
    }

    private suspend fun loadApiData(): UiState<ApiData> {
        val statusResult = getGoogleStatusUseCase()
        if (statusResult is BaseResult.Error) {
            return UiState.Error(statusResult.error.message)
        }
        return getGoogleDataUseCase().toUiState().map { data ->
            ApiData(status = "Ok", data = data)
        }
    }

    private fun fetchInstallationValidity() {
        viewModelScope.launch {
            _viewState.update { it.copy(installedFromValidSource = getIsInstalledFromValidSource()) }
        }
    }

    private fun getRequiredPermissions(): List<String> {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                listOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                )
            }
            else -> emptyList()
        }
    }
}
