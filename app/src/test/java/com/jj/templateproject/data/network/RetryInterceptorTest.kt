package com.jj.templateproject.data.network

import okhttp3.Call
import okhttp3.Connection
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import java.io.IOException
import java.util.concurrent.TimeUnit

class RetryInterceptorTest {

    private val request = Request.Builder().url("https://example.com/").build()

    @Test
    fun `retries on IOException and returns the eventual success`() {
        var attempts = 0
        val chain = FakeChain(request) { req ->
            attempts++
            if (attempts < 3) throw IOException("flaky") else okResponse(req)
        }

        val response = RetryInterceptor(maxRetries = 2).intercept(chain)

        assertEquals(200, response.code)
        assertEquals(3, attempts)
    }

    @Test
    fun `gives up and rethrows after exhausting retries`() {
        var attempts = 0
        val chain = FakeChain(request) {
            attempts++
            throw IOException("always failing")
        }

        assertThrows(IOException::class.java) {
            RetryInterceptor(maxRetries = 2).intercept(chain)
        }
        assertEquals(3, attempts) // 1 initial attempt + 2 retries
    }

    @Test
    fun `does not retry a successful call`() {
        var attempts = 0
        val chain = FakeChain(request) { req ->
            attempts++
            okResponse(req)
        }

        RetryInterceptor(maxRetries = 2).intercept(chain)

        assertEquals(1, attempts)
    }

    private fun okResponse(forRequest: Request): Response = Response.Builder()
        .request(forRequest)
        .protocol(Protocol.HTTP_1_1)
        .code(200)
        .message("OK")
        .body("ok".toResponseBody())
        .build()

    /** Minimal [Interceptor.Chain] whose [proceed] delegates to [onProceed]. */
    private class FakeChain(
        private val request: Request,
        private val onProceed: (Request) -> Response,
    ) : Interceptor.Chain {
        override fun request(): Request = request
        override fun proceed(request: Request): Response = onProceed(request)
        override fun connection(): Connection? = null
        override fun call(): Call = throw UnsupportedOperationException()
        override fun connectTimeoutMillis(): Int = 0
        override fun withConnectTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun readTimeoutMillis(): Int = 0
        override fun withReadTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
        override fun writeTimeoutMillis(): Int = 0
        override fun withWriteTimeout(timeout: Int, unit: TimeUnit): Interceptor.Chain = this
    }
}
