package com.jj.templateproject.data.login.network

import com.jj.templateproject.data.login.LoginService
import com.jj.templateproject.data.login.model.RawLoginRequest
import com.jj.templateproject.data.utils.NetworkCallResult
import com.jj.templateproject.data.utils.safeCall

class DefaultLoginNetwork(
    private val loginService: LoginService,
) : LoginNetwork {
    override suspend fun loginUser(username: String, password: String): NetworkCallResult<Unit> =
        safeCall {
            loginService.login(
                RawLoginRequest(
                    username = username,
                    password = password,
                )
            )
        }
}