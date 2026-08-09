package com.jj.templateproject.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

/**
 * Builds an [HttpClient] on the engine each platform actually ships: OkHttp on Android, Darwin on
 * iOS. [block] configures the client's plugins and is identical on every platform (see
 * [TemplateHttpClientFactory]) — this is the only piece of the client that genuinely has to differ
 * per platform, which is why the expect/actual boundary sits here and nowhere higher.
 *
 * Star-projected rather than `HttpClientConfig<out HttpClientEngineConfig>`: every plugin
 * installed inside [block] (`install(HttpRequestRetry)`, `install(HttpTimeout)`, …) is declared
 * generically over `HttpClientConfig<*>` and never touches the concrete engine-config type, so
 * nothing here needs to name it — which is what lets each actual apply [block] to its own
 * concretely-typed config (`HttpClientConfig<OkHttpConfig>`, `HttpClientConfig<DarwinClientEngineConfig>`)
 * with a single unchecked cast that is safe in practice, since [block] never reads that type either.
 */
expect fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient
