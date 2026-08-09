package com.jj.templateproject.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.engine.okhttp.OkHttpConfig

actual fun createPlatformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = HttpClient(OkHttp) {
    @Suppress("UNCHECKED_CAST")
    val configure = block as HttpClientConfig<OkHttpConfig>.() -> Unit
    configure()
}
