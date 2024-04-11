package com.jj.templateproject.data.login.network

import com.jj.templateproject.domain.BaseResult
import kotlinx.coroutines.delay

class MockLoginNetwork : LoginNetwork {
    override suspend fun loginUser(username: String, password: String): BaseResult<String> {
        delay(2000L)
        return BaseResult.Success("200")
    }
}