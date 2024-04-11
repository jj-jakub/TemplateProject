package com.jj.templateproject.data.login.network

import com.jj.templateproject.domain.BaseResult

interface LoginNetwork {
    suspend fun loginUser(username: String, password: String): BaseResult<String>
}