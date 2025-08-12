package com.jj.templateproject.presentation.ui.settings

import android.Manifest
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    versionTextProvider: VersionTextProvider,
    private val getGoogleStatusUseCase: GetGoogleStatusUseCase,
    private val getGoogleDataUseCase: GetGoogleDataUseCase,
    private val getIsInstalledFromValidSource: GetIsInstalledFromValidSource,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsScreenViewState(
            loading = true,
            requiredPermissions = getRequiredPermissions(),
        )
    )
    val viewState: StateFlow<SettingsScreenViewState> = _viewState.asStateFlow()

    init {
        _viewState.value = viewState.value.copy(
            versionText = versionTextProvider.getAboutVersionText(),
        )

        fetchGoogleData()
        fetchInstallationValidity()
    }

    private fun fetchGoogleData() {
        viewModelScope.launch {
            val status = when (val result = getGoogleStatusUseCase.invoke()) {
                is BaseResult.Error -> result.error.message
                is BaseResult.Success -> "Ok"
            }

            _viewState.value = viewState.value.copy(
                apiCallStatus = status,
            )

            val data = when (val result = getGoogleDataUseCase.invoke()) {
                is BaseResult.Error -> "Error"
                is BaseResult.Success -> result.data
            }

            _viewState.value = viewState.value.copy(
                apiCallData = data,
                loading = false,
            )
        }
    }

    private fun fetchInstallationValidity() {
        viewModelScope.launch {
            _viewState.value = viewState.value.copy(
                installedFromValidSource = getIsInstalledFromValidSource()
            )
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
