package com.jj.templateproject.domain.login

import com.jj.templateproject.domain.login.model.LoginType

interface LoginRepository {
    fun login(loginType: LoginType)
}