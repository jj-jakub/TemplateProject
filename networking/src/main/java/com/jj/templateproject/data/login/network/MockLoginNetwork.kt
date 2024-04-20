package com.jj.templateproject.data.login.network

import com.jj.templateproject.data.utils.NetworkCallResult
import com.jj.templateproject.domain.model.NetworkException
import kotlinx.coroutines.delay
import retrofit2.Response

class MockLoginNetwork : LoginNetwork {
    private var shouldFail = false
    override suspend fun loginUser(username: String, password: String): NetworkCallResult<Unit> {
        delay(2000L)
        shouldFail = !shouldFail
        return if (shouldFail) {
            val body: Unit? = null
            NetworkCallResult.Success(Response.success(200, body))
        } else {
            NetworkCallResult.Failure(
                NetworkException.FailedRequest(
                    code = 401,
                    message = "Wrong credentials",
                )
            )
        }
    }
}