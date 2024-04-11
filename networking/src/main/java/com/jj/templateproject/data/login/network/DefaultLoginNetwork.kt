package com.jj.templateproject.data.login.network

import com.jj.templateproject.data.login.LoginService
import com.jj.templateproject.data.login.model.RawLoginRequest
import com.jj.templateproject.data.utils.NetworkException
import com.jj.templateproject.domain.BaseResult

class DefaultLoginNetwork(
    private val loginService: LoginService,
) : LoginNetwork {
    override suspend fun loginUser(username: String, password: String): BaseResult<String> {
        val result = loginService.login(
            RawLoginRequest(
                username = username,
                password = password,
            )
        )
        return if (result.isSuccessful) {
            BaseResult.Success(result.code().toString())
        } else {
            BaseResult.Error(NetworkException(result.code(), result.message()))
        }
    }
}