package com.jj.templateproject.data.network

import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.coroutines.DefaultDispatcherProvider
import com.jj.templateproject.domain.google.TemplateRepository
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Integration test of the real networking stack: RetrofitFactory (kotlinx-serialization converter
 * + OkHttp) -> TemplateService -> TemplateNetwork -> DefaultTemplateRepository, exercised end to
 * end against a MockWebServer.
 */
class NetworkingIntegrationTest {

    private lateinit var server: MockWebServer

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @AfterEach
    fun tearDown() {
        server.shutdown()
    }

    private fun repository(): TemplateRepository {
        val retrofit = RetrofitFactory().retrofit(server.url("/").toString())
        val service = retrofit.create(TemplateService::class.java)
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
        assertEquals(503, (result as BaseResult.Error).error.code)
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
        assertEquals(404, (result as BaseResult.Error).error.code)
    }

    @Test
    fun `requests are issued against the root path`() = runTest {
        server.enqueue(MockResponse().setResponseCode(200))

        repository().getGoogleData()

        val recorded = server.takeRequest()
        assertEquals("GET", recorded.method)
        assertEquals("/", recorded.path)
    }
}
