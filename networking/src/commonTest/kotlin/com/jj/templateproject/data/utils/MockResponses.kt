package com.jj.templateproject.data.utils

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf

/**
 * A client backed by [MockEngine] with a single canned reply, for a test that only cares about the
 * response side of [toResult]/[safeApiCall] and not what request produced it.
 */
private fun mockClient(handler: MockRequestHandler): HttpClient =
    HttpClient(MockEngine) { engine { addHandler(handler) } }

/**
 * Issues one GET through a [MockEngine] client and returns the resolved [HttpResponse] — the shape
 * [toResult] actually takes, since Ktor only exposes headers/status once a request has completed.
 */
suspend fun respondWith(status: HttpStatusCode, body: String = ""): HttpResponse =
    mockClient { respond(body, status, headersOf("Content-Type", "text/plain")) }.get("/")
