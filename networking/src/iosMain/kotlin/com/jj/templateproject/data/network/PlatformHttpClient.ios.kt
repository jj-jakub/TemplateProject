package com.jj.templateproject.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.engine.darwin.DarwinClientEngineConfig

actual fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = HttpClient(Darwin) {
    @Suppress("UNCHECKED_CAST")
    val configure = block as HttpClientConfig<DarwinClientEngineConfig>.() -> Unit
    configure()
}
