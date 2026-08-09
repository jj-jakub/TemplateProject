package com.jj.templateproject.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json

/**
 * Builds the app's [HttpClient].
 *
 * The multiplatform counterpart of the old Retrofit-era `RetrofitFactory`: everything that used to
 * be an OkHttp interceptor is a Ktor client plugin here, and — unlike an interceptor — every plugin
 * is genuinely shared code. [createPlatformHttpClient] is the only piece left that has to differ
 * per platform (which engine actually runs the request).
 *
 * @param baseUrl every request is resolved against this.
 * @param headerProvider seam for per-request headers (e.g. an auth token). Ktor's `DefaultRequest`
 *   plugin runs its block fresh on every outgoing request, so this is invoked per request exactly
 *   like the interceptor it replaces, and can return freshly-read values.
 * @param maxRetries how many extra attempts [HttpRequestRetry] makes on a failure
 *   [classifyTransportFailure] recognises as transient.
 * @param logBody whether to log full request/response bodies. Callers must pass `false` for a
 *   release build — bodies may carry user data or tokens.
 */
object TemplateHttpClientFactory {

    fun create(
        baseUrl: String,
        headerProvider: () -> Map<String, String> = { emptyMap() },
        maxRetries: Int = DEFAULT_MAX_RETRIES,
        logBody: Boolean = false,
    ): HttpClient {
        val retryAttempts = maxRetries
        return createPlatformHttpClient {
            // Installed before HttpTimeout: Ktor's own retry documentation calls this order out
            // explicitly as what lets a retried request's timeout be tracked correctly.
            install(HttpRequestRetry) {
                this.maxRetries = retryAttempts
                // The plugin's own default retries certain non-2xx responses even with no
                // retryOnServerErrors()/retryIf() call of our own — retryIf and retryOnExceptionIf
                // are independent predicates, ORed together, so leaving the response side
                // unconfigured does not mean "off". The interceptor this replaces retried only on
                // a thrown IOException and never looked at a response's status at all, so the
                // response side is disabled explicitly to match.
                retryIf { _, _ -> false }
                // A failure the platform itself deems transient (see classifyTransportFailure) —
                // never a bare "retry everything", and never a retry on cancellation, matching the
                // interceptor this replaces (it only retried an IOException, not an arbitrary one).
                retryOnExceptionIf { _, cause ->
                    cause !is CancellationException && classifyTransportFailure(cause) != null
                }
                // No backoff: the interceptor it replaces retried immediately, synchronously.
                delayMillis { _ -> 0L }
            }
            install(HttpTimeout) {
                connectTimeoutMillis = CONNECT_TIMEOUT_MILLIS
                socketTimeoutMillis = SOCKET_TIMEOUT_MILLIS
                requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
            }
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
            install(Logging) {
                // Never log bodies in a release build: they may contain user data or tokens.
                level = if (logBody) LogLevel.BODY else LogLevel.NONE
            }
            install(DefaultRequest) {
                url(baseUrl)
                headerProvider().forEach { (name, value) -> header(name, value) }
            }
        }
    }

    private const val CONNECT_TIMEOUT_MILLIS = 15_000L
    private const val SOCKET_TIMEOUT_MILLIS = 30_000L
    private const val REQUEST_TIMEOUT_MILLIS = 60_000L
    private const val DEFAULT_MAX_RETRIES = 2
}
