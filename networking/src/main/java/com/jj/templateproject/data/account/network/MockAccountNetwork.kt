package com.jj.templateproject.data.account.network

import com.jj.templateproject.data.utils.NetworkCallResult
import retrofit2.Response

class MockAccountNetwork : AccountNetwork {
    override suspend fun createAccount(
        username: String,
        password: String,
    ): NetworkCallResult<Unit> {
        val body: Unit? = null
        return NetworkCallResult.Success(Response.success(200, body))
    }
}