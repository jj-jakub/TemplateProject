package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * The platform-specific half of this classification (what OkHttp/Darwin throw for "no
 * connection") is pinned in each platform's own test source set instead — see
 * `classifyTransportFailure` and its actuals. What belongs here is everything genuinely
 * multiplatform: the HTTP-response side, Ktor's own timeout exceptions, and the fallbacks.
 */
class SafeApiCallTest {

    @Test
    fun `a 2xx response is mapped through onSuccess`() = runTest {
        val result = safeApiCall(apiCall = { respondWith(HttpStatusCode.NoContent) }) { it.status.value.toString() }

        assertTrue(result is BaseResult.Success)
        assertEquals("204", (result as BaseResult.Success).data)
    }

    @Test
    fun `a non-2xx response maps to Http with the code`() = runTest {
        val result = safeApiCall(apiCall = { respondWith(HttpStatusCode.ServiceUnavailable) }) { }

        val error = (result as BaseResult.Error).error
        assertTrue(error is NetworkError.Http)
        assertEquals(503, (error as NetworkError.Http).code)
    }

    @Test
    fun `a Ktor request timeout maps to Timeout`() = runTest {
        val result = safeApiCall<Unit>(
            apiCall = { throw HttpRequestTimeoutException(HttpRequestBuilder()) },
        ) { }

        assertEquals(NetworkError.Timeout, (result as BaseResult.Error).error)
    }

    @Test
    fun `a Ktor connect timeout maps to Timeout`() = runTest {
        val result = safeApiCall<Unit>(
            apiCall = { throw ConnectTimeoutException("connect timed out") },
        ) { }

        assertEquals(NetworkError.Timeout, (result as BaseResult.Error).error)
    }

    @Test
    fun `a Ktor socket timeout maps to Timeout`() = runTest {
        val result = safeApiCall<Unit>(
            apiCall = { throw SocketTimeoutException("socket timed out") },
        ) { }

        assertEquals(NetworkError.Timeout, (result as BaseResult.Error).error)
    }

    @Test
    fun `a serialization failure maps to Serialization`() = runTest {
        val result = safeApiCall<Unit>(apiCall = { throw SerializationException("bad") }) { }

        assertTrue((result as BaseResult.Error).error is NetworkError.Serialization)
    }

    @Test
    fun `any other failure maps to Unknown`() = runTest {
        val result = safeApiCall<Unit>(apiCall = { throw IllegalStateException("weird") }) { }

        assertTrue((result as BaseResult.Error).error is NetworkError.Unknown)
    }

    @Test
    fun `cancellation is rethrown rather than swallowed`() = runTest {
        assertFailsWith<CancellationException> {
            safeApiCall<Unit>(apiCall = { throw CancellationException("cancel") }) { }
        }
    }
}
