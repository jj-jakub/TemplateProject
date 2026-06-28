package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class ToResultTest {

    @Test
    fun `successful response maps to Success using the onSuccess transform`() {
        val response = Response.success(Unit)

        val result = response.toResult { it.code().toString() }

        assertTrue(result is BaseResult.Success)
        assertEquals("200", (result as BaseResult.Success).data)
    }

    @Test
    fun `successful response with empty transform maps to Success of Unit`() {
        val response = Response.success(Unit)

        val result = response.toResult { }

        assertTrue(result is BaseResult.Success)
        assertEquals(Unit, (result as BaseResult.Success).data)
    }

    @Test
    fun `error response maps to Error carrying the http code`() {
        val body = "nope".toResponseBody("text/plain".toMediaType())
        val response = Response.error<Unit>(404, body)

        val result = response.toResult { }

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertEquals(404, error.code)
        assertEquals(response.message(), error.message)
    }

    @Test
    fun `error response does not invoke the onSuccess transform`() {
        val body = "nope".toResponseBody("text/plain".toMediaType())
        val response = Response.error<Unit>(500, body)

        var transformCalled = false
        response.toResult { transformCalled = true }

        assertEquals(false, transformCalled)
    }
}
