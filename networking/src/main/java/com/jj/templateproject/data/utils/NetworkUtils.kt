package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import retrofit2.Response

/**
 * Maps a Retrofit [Response] to a [BaseResult], deriving the success value from [onSuccess].
 *
 * Pass a transform (e.g. `{ it.code().toString() }`) for endpoints that return data, or `{ }`
 * for endpoints where only success/failure matters (a [Unit] result).
 */
inline fun <T, R> Response<T>.toResult(onSuccess: (Response<T>) -> R): BaseResult<R, NetworkError> =
    if (isSuccessful) {
        BaseResult.Success(onSuccess(this))
    } else {
        BaseResult.Error(NetworkError(code(), message()))
    }
