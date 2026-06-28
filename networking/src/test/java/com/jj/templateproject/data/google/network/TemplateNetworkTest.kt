package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class TemplateNetworkTest {

    private val service = mockk<TemplateService>()
    private val network = TemplateNetwork(service)

    @Test
    fun `getGoogleData returns the http status code as success data`() = runTest {
        // A non-200 2xx pins the mapping to response.code(), not a hardcoded "200".
        coEvery { service.getGoogleData() } returns Response.success(206, Unit)

        val result = network.getGoogleData()

        assertTrue(result is BaseResult.Success)
        assertEquals("206", (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleData maps an unsuccessful response to Error`() = runTest {
        coEvery { service.getGoogleData() } returns errorResponse(503)

        val result = network.getGoogleData()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(503, (error as NetworkError.Http).code)
    }

    @Test
    fun `getGoogleStatus returns Success of Unit when the call succeeds`() = runTest {
        coEvery { service.getGoogleStatus() } returns Response.success(Unit)

        val result = network.getGoogleStatus()

        assertTrue(result is BaseResult.Success)
        assertEquals(Unit, (result as BaseResult.Success).data)
    }

    @Test
    fun `getGoogleStatus maps an unsuccessful response to Error`() = runTest {
        coEvery { service.getGoogleStatus() } returns errorResponse(418)

        val result = network.getGoogleStatus()

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(418, (error as NetworkError.Http).code)
    }

    @Test
    fun `getGoogleStatus queries the status endpoint and not the data endpoint`() = runTest {
        coEvery { service.getGoogleStatus() } returns Response.success(Unit)

        network.getGoogleStatus()

        coVerify(exactly = 1) { service.getGoogleStatus() }
        coVerify(exactly = 0) { service.getGoogleData() }
    }

    private fun errorResponse(code: Int): Response<Unit> =
        Response.error(code, "".toResponseBody("text/plain".toMediaType()))
}
