package com.jj.templateproject.data.network

import com.jj.templateproject.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds the app's [Retrofit] instance.
 *
 * @param headerProvider template seam for per-request headers (e.g. an auth token). It is invoked
 *   on every request, so it can return freshly-read values; defaults to no extra headers.
 * @param maxRetries how many extra attempts [RetryInterceptor] makes on a transient IO failure.
 */
class RetrofitFactory(
    private val headerProvider: () -> Map<String, String> = { emptyMap() },
    private val maxRetries: Int = DEFAULT_MAX_RETRIES,
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val contentType = "application/json".toMediaType()

    // Retrofit 2.6+/3.x supports `suspend` functions natively, so no call-adapter is needed.
    fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(json.asConverterFactory(contentType))
        .client(okHttp())
        .build()

    private fun okHttp(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .addInterceptor(headerInterceptor())
        .addInterceptor(RetryInterceptor(maxRetries))
        .addInterceptor(loggingInterceptor())
        .build()

    private fun headerInterceptor() = Interceptor { chain ->
        val headers = headerProvider()
        if (headers.isEmpty()) {
            chain.proceed(chain.request())
        } else {
            val request = chain.request().newBuilder().apply {
                headers.forEach { (name, value) -> header(name, value) }
            }.build()
            chain.proceed(request)
        }
    }

    private fun loggingInterceptor() = HttpLoggingInterceptor().apply {
        // Never log bodies in release builds — they may contain user data / tokens.
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private companion object {
        const val CONNECT_TIMEOUT_SECONDS = 15L
        const val READ_TIMEOUT_SECONDS = 30L
        const val WRITE_TIMEOUT_SECONDS = 30L
        const val CALL_TIMEOUT_SECONDS = 60L
        const val DEFAULT_MAX_RETRIES = 2
    }
}
