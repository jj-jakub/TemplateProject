package com.jj.templateproject.data.account.network

import com.jj.templateproject.domain.BaseResult

interface AccountNetwork {
    suspend fun createAccount(username: String, password: String): BaseResult<String>
}