package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import retrofit2.Response

fun <T : Any> Response<T>.toResult(): BaseResult<T> {
    val body = body()
    return if (isSuccessful && body != null) {
        BaseResult.Success(body)
    } else {
        BaseResult.Error(NetworkException(code(), message()))
    }
}