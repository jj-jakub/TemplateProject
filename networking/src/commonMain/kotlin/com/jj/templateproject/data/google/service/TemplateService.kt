package com.jj.templateproject.data.google.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

/**
 * The raw endpoints, kept as thin one-line wrappers over [HttpClient] so [TemplateNetwork] (which
 * does the actual [BaseResult][com.jj.templateproject.domain.BaseResult] mapping) has one narrow
 * thing to call. Tested against a [io.ktor.client.engine.mock.MockEngine]-backed client rather
 * than behind an interface of its own: an HTTP call is the seam here, the same way it is for
 * [com.jj.templateproject.data.utils.safeApiCall]'s own tests, so a fake transport is more
 * representative than faking this class would be.
 *
 * `baseUrl` is not passed here: it is installed once on the client itself, by
 * [com.jj.templateproject.data.network.TemplateHttpClientFactory], via Ktor's `DefaultRequest`
 * plugin. `client.get("")` therefore resolves against that base — the same relative-path model
 * `@GET("/")` gave under Retrofit.
 */
class TemplateService(
    private val client: HttpClient,
) {
    suspend fun getGoogleData(): HttpResponse = client.get("")

    suspend fun getGoogleStatus(): HttpResponse = client.get("")
}
