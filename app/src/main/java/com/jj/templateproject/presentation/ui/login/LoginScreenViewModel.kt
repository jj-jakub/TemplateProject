package com.jj.templateproject.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.login.LoginWithPasswordUseCase
import com.jj.templateproject.presentation.ui.login.model.LoginScreenNavigation
import com.jj.templateproject.presentation.ui.login.model.LoginScreenViewState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginScreenViewModel(
    private val loginWithPasswordUseCase: LoginWithPasswordUseCase,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        LoginScreenViewState(
            isLoading = true,
            username = "",
            password = "",
        )
    )
    val viewState = _viewState.asStateFlow()

    private val _navigation = MutableSharedFlow<LoginScreenNavigation>()
    val navigation = _navigation.asSharedFlow()

    init {
        viewModelScope.launch {
            delay(2000L)
            _viewState.value = viewState.value.copy(isLoading = false)
        }
    }

    fun onUsernameChanged(newValue: String) {
        _viewState.value = viewState.value.copy(username = newValue)
    }

    fun onPasswordChanged(newValue: String) {
        _viewState.value = viewState.value.copy(password = newValue)
    }

    fun onLoginClicked() {
        _viewState.update { viewState.value.copy(isLoading = true) }
        viewModelScope.launch {
            val state = viewState
            when (loginWithPasswordUseCase(
                LoginWithPasswordUseCase.LoginWithPasswordParams(
                    state.value.username,
                    state.value.password,
                )
            )) {
                is BaseResult.Error -> TODO()
                is BaseResult.Success -> navigate(LoginScreenNavigation.MainScreen)
            }
        }
        _viewState.update { viewState.value.copy(isLoading = false) }
    }

    fun onSignInClicked() {
        navigate(LoginScreenNavigation.CreateAccountScreen)
    }

    private fun navigate(event: LoginScreenNavigation) {
        viewModelScope.launch { _navigation.emit(event) }
    }
}