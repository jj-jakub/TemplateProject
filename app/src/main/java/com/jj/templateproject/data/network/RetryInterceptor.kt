package com.jj.templateproject.data.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Retries a request up to [maxRetries] additional times when the call fails with an [IOException]
 * (a transient connection problem). The last failure is rethrown if every attempt fails.
 *
 * Note: this retries the request as-is, so keep [maxRetries] modest and be mindful of
 * non-idempotent calls. It complements OkHttp's own `retryOnConnectionFailure`.
 */
class RetryInterceptor(private val maxRetries: Int) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var lastError: IOException? = null
        repeat(maxRetries + 1) {
            try {
                return chain.proceed(request)
            } catch (error: IOException) {
                lastError = error
            }
        }
        throw lastError ?: IOException("Request failed after $maxRetries retries")
    }
}
