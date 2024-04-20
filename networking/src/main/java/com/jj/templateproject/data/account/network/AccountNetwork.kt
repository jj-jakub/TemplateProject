package com.jj.templateproject.data.account.network

import com.jj.templateproject.data.utils.NetworkCallResult

interface AccountNetwork {
    suspend fun createAccount(username: String, password: String): NetworkCallResult<Unit>
}