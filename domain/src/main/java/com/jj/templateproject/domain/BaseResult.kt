package com.jj.templateproject.domain

sealed interface BaseResult<out Data, out Err : BaseError> {
    data class Success<out Data, out Err : BaseError>(val data: Data) : BaseResult<Data, Err>
    data class Error<out Data, out Err : BaseError>(val error: Err) : BaseResult<Data, Err>
}
