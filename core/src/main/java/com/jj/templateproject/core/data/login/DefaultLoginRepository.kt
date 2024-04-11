package com.jj.templateproject.core.data.login

import com.jj.templateproject.domain.login.LoginRepository
import com.jj.templateproject.domain.login.model.LoginType

class DefaultLoginRepository: LoginRepository {
    override fun login(loginType: LoginType) {
        when(loginType) {
            is LoginType.LoginWithPassword -> loginWithPassword(loginType)
        }
    }

    private fun loginWithPassword(loginType: LoginType.LoginWithPassword) {
        TODO("Not yet implemented")
    }
}