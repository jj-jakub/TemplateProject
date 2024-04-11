package com.jj.templateproject.domain.login

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.login.model.LoginType

interface LoginRepository {
    suspend fun login(loginType: LoginType): BaseResult<String>
}