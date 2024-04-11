package com.jj.templateproject.domain.login.model

sealed interface LoginType {
    data class LoginWithPassword(val username: String, val password: String) : LoginType
}