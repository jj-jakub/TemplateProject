package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.SerializationException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class SafeApiCallTest {

    @Test
    fun `a 2xx response is mapped through onSuccess`() = runTest {
        val result = safeApiCall(apiCall = { Response.success(204, Unit) }) { it.code().toString() }

        assertTrue(result is BaseResult.Success)
        assertEquals("204", (result as BaseResult.Success).data)
    }

    @Test
    fun `a non-2xx response maps to Http with the code`() = runTest {
        val body = "".toResponseBody("text/plain".toMediaType())

        val result = safeApiCall(apiCall = { Response.error<Unit>(503, body) }) { }

        val error = (result as BaseResult.Error).error
        assertTrue(error is NetworkError.Http)
        assertEquals(503, (error as NetworkError.Http).code)
    }

    @Test
    fun `a socket timeout maps to Timeout`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw SocketTimeoutException() }) { }

        assertEquals(NetworkError.Timeout, (result as BaseResult.Error).error)
    }

    @Test
    fun `an unknown host maps to Connectivity`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw UnknownHostException() }) { }

        assertEquals(NetworkError.Connectivity, (result as BaseResult.Error).error)
    }

    @Test
    fun `a connect failure maps to Connectivity`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw ConnectException() }) { }

        assertEquals(NetworkError.Connectivity, (result as BaseResult.Error).error)
    }

    @Test
    fun `a generic IO failure maps to Connectivity`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw IOException("stream closed") }) { }

        assertEquals(NetworkError.Connectivity, (result as BaseResult.Error).error)
    }

    @Test
    fun `a serialization failure maps to Serialization`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw SerializationException("bad") }) { }

        assertTrue((result as BaseResult.Error).error is NetworkError.Serialization)
    }

    @Test
    fun `any other failure maps to Unknown`() = runTest {
        val result = safeApiCall<Unit, Unit>(apiCall = { throw IllegalStateException("weird") }) { }

        assertTrue((result as BaseResult.Error).error is NetworkError.Unknown)
    }

    @Test
    fun `cancellation is rethrown, not swallowed`() {
        assertThrows(CancellationException::class.java) {
            runBlocking {
                safeApiCall<Unit, Unit>(apiCall = { throw CancellationException("cancel") }) { }
            }
        }
    }
}
