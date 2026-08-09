package com.jj.templateproject.data.network

import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.coroutines.DefaultDispatcherProvider
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Integration test of the real networking stack: `TemplateHttpClientFactory` (the real OkHttp
 * engine, every plugin installed) -> `TemplateService` -> `TemplateNetwork` ->
 * `DefaultTemplateRepository`, exercised end to end against a `MockWebServer`.
 *
 * This is what the `MockEngine`-based tests in `commonTest` cannot be: those pin the plugin
 * *logic* (what `safeApiCall` does with a resolved response or a thrown exception) without ever
 * running a real engine, so an engine wiring mistake — the plugin install order, whether OkHttp
 * actually retries, whether `DefaultRequest`'s base URL is applied — would pass every one of them
 * and only show up here, against a real socket.
 */
class NetworkingIntegrationTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun repository(headerProvider: () -> Map<String, String> = { emptyMap() }): TemplateRepository {
        val client = TemplateHttpClientFactory.create(
            baseUrl = server.url("/").toString(),
            headerProvider = headerProvider,
        )
        val service = TemplateService(client)
        return DefaultTemplateRepository(TemplateNetwork(service), DefaultDispatcherProvider())
    }

    @Test
    fun `getGoogleData returns Success with the status code on a 2xx response`() = runTest {
        // 206 (not the default 200) pins the result to the real response code.
        server.enqueue(MockResponse().setResponseCode(206).setBody("<html>ok</html>"))

        val result = repository().getGoogleData()

        assertTrue(result is BaseResult.Success)
        assertEquals("206", (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleData returns Error carrying the http code on a 5xx response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(503))

        val result = repository().getGoogleData()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(503, (error as NetworkError.Http).code)
    }

    @Test
    fun `getGoogleStatus returns Success of Unit on a 2xx response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(204))

        val result = repository().getGoogleStatus()

        assertTrue(result is BaseResult.Success)
        assertEquals(Unit, (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleStatus returns Error on a 4xx response`() = runTest {
        server.enqueue(MockResponse().setResponseCode(404))

        val result = repository().getGoogleStatus()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(404, (error as NetworkError.Http).code)
    }

    @Test
    fun `requests are issued against the root path`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))

        repository().getGoogleData()

        val recorded = server.takeRequest()
        assertEquals("GET", recorded.method)
        assertEquals("/", recorded.path)
    }

    @Test
    fun `a transient connection failure is retried through the real engine and then succeeds`() = runTest {
        // First attempt drops the connection (an IOException the HttpRequestRetry plugin retries
        // via classifyTransportFailure), second is a 2xx.
        server.enqueue(MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AT_START })
        server.enqueue(MockResponse().setResponseCode(200))

        val result = repository().getGoogleData()

        assertTrue(result is BaseResult.Success)
        assertEquals("200", (result as BaseResult.Success).data)
    }

    @Test
    fun `an HTTP error response is not retried`() = runTest {
        server.enqueue(MockResponse().setResponseCode(503))

        val result = repository().getGoogleData()

        assertTrue(result is BaseResult.Error)
        assertEquals(503, ((result as BaseResult.Error).error as NetworkError.Http).code)
        // The retry plugin only retries a transport failure (see classifyTransportFailure), never
        // a non-2xx response.
        assertEquals(1, server.requestCount)
    }

    @Test
    fun `the header provider seam attaches headers to outgoing requests`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))

        repository(headerProvider = { mapOf("Authorization" to "Bearer abc") }).getGoogleData()

        assertEquals("Bearer abc", server.takeRequest().getHeader("Authorization"))
    }
}
