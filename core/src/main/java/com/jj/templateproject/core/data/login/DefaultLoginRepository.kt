package com.jj.templateproject.core.data.login

import com.jj.templateproject.data.login.network.LoginNetwork
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.login.LoginRepository
import com.jj.templateproject.domain.login.model.LoginType

class DefaultLoginRepository(
    private val loginNetwork: LoginNetwork,
) : LoginRepository {
    override suspend fun login(loginType: LoginType): BaseResult<String> = when (loginType) {
        is LoginType.LoginWithPassword -> loginWithPassword(loginType)
    }

    private suspend fun loginWithPassword(loginType: LoginType.LoginWithPassword): BaseResult<String> =
        loginNetwork.loginUser(username = loginType.username, password = loginType.password)
}