package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TemplateNetworkTest {

    private fun networkRespondingWith(status: HttpStatusCode): Pair<TemplateNetwork, MutableList<HttpRequestData>> {
        val requests = mutableListOf<HttpRequestData>()
        val client = HttpClient(MockEngine) {
            engine {
                addHandler { request ->
                    requests += request
                    respond("", status, headersOf("Content-Type", "text/plain"))
                }
            }
        }
        return TemplateNetwork(TemplateService(client)) to requests
    }

    @Test
    fun `getGoogleData returns the http status code as success data`() = runTest {
        // A non-200 2xx pins the mapping to the real status value, not a hardcoded "200".
        val (network, _) = networkRespondingWith(HttpStatusCode.PartialContent)

        val result = network.getGoogleData()

        assertTrue(result is BaseResult.Success)
        assertEquals("206", (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleData maps an unsuccessful response to Error`() = runTest {
        val (network, _) = networkRespondingWith(HttpStatusCode.ServiceUnavailable)

        val result = network.getGoogleData()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(503, (error as NetworkError.Http).code)
    }

    @Test
    fun `getGoogleStatus returns Success of Unit when the call succeeds`() = runTest {
        val (network, _) = networkRespondingWith(HttpStatusCode.OK)

        val result = network.getGoogleStatus()

        assertTrue(result is BaseResult.Success)
        assertEquals(Unit, (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleStatus maps an unsuccessful response to Error`() = runTest {
        val (network, _) = networkRespondingWith(HttpStatusCode(418, "I'm a teapot"))

        val result = network.getGoogleStatus()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(418, (error as NetworkError.Http).code)
    }

    @Test
    fun `getGoogleStatus makes exactly one request`() = runTest {
        val (network, requests) = networkRespondingWith(HttpStatusCode.OK)

        network.getGoogleStatus()

        assertEquals(1, requests.size)
    }
}
