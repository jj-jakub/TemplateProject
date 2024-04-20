package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.model.NetworkException
import retrofit2.Response

sealed interface NetworkCallResult<T> {
    data class Success<T>(val response: Response<T>) : NetworkCallResult<T>
    data class Failure<T>(val exception: NetworkException) : NetworkCallResult<T>
}

fun <T: Any>NetworkCallResult<T>.toBaseResult(): BaseResult<T> = when(this) {
    is NetworkCallResult.Failure -> BaseResult.Error(exception)
    is NetworkCallResult.Success<T> -> BaseResult.Success(this.response.body())
}