package com.jj.templateproject.data.network

import com.jj.templateproject.domain.google.exception.NetworkError
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException

/**
 * OkHttp's own exception vocabulary for "the network is unreachable", ported unchanged from the
 * Retrofit-era classification.
 *
 * [InterruptedIOException] is checked first and mapped to [NetworkError.Timeout], not
 * [NetworkError.Connectivity]: `SocketTimeoutException` extends it, and OkHttp's own call-level
 * timeout throws a bare `InterruptedIOException("timeout")` rather than a `SocketTimeoutException`.
 * [safeApiCall] already checks Ktor's own timeout exceptions before reaching this function, but
 * Ktor's OkHttp engine does not reliably translate every OkHttp-level timeout into one of those, so
 * this is the fallback that keeps the distinction correct either way.
 */
actual fun classifyTransportFailure(throwable: Throwable): NetworkError? = when (throwable) {
    is InterruptedIOException -> NetworkError.Timeout
    is UnknownHostException -> NetworkError.Connectivity
    is ConnectException -> NetworkError.Connectivity
    is IOException -> NetworkError.Connectivity
    else -> null
}
