package com.jj.templateproject.presentation.ui.login.model

data class LoginScreenViewState(
    val isLoading: Boolean,
    val error: LoginScreenErrorType,
    val username: String,
    val password: String,
)
