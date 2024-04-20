package com.jj.templateproject.domain.model

import java.io.IOException

sealed class NetworkException(override val message: String?) : IOException(message) {
    data class FailedRequest(val code: Int, override val message: String) :
        NetworkException(message)

    data class RequestException(val exception: Exception) : NetworkException(exception.message)
}
