package com.jj.templateproject.presentation.ui.login.model

sealed interface LoginScreenErrorType {
    data object None : LoginScreenErrorType
    data class GenericError(val exception: Exception) : LoginScreenErrorType
}
