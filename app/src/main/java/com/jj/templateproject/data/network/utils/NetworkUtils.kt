package com.jj.templateproject.data.network.utils

import com.jj.templateproject.data.Result
import retrofit2.Response

fun <T : Any> Response<T>.toResult(): Result<T> {
    val body = body()
    return if (isSuccessful && body != null) {
        Result.Success(body)
    } else {
        Result.Error(NetworkException(code(), message()))
    }
}