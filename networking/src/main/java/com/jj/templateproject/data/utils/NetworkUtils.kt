package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.model.NetworkException
import retrofit2.Response

suspend fun <T> safeCall(call: suspend () -> Response<T>): NetworkCallResult<T> = try {
    val response = call()
    if (response.isSuccessful) {
        NetworkCallResult.Success(response)
    } else {
        NetworkCallResult.Failure(
            NetworkException.FailedRequest(
                code = response.code(),
                message = response.message(),
            )
        )
    }
} catch (e: Exception) {
    NetworkCallResult.Failure(NetworkException.RequestException(e))
}