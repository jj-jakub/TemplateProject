package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ToResultTest {

    @Test
    fun `successful response maps to Success using the onSuccess transform`() = runTest {
        val response = respondWith(HttpStatusCode.OK)

        val result = response.toResult { it.status.value.toString() }

        assertTrue(result is BaseResult.Success)
        assertEquals("200", (result as BaseResult.Success).data)
    }

    @Test
    fun `successful response with empty transform maps to Success of Unit`() = runTest {
        val response = respondWith(HttpStatusCode.OK)

        val result = response.toResult { }

        assertTrue(result is BaseResult.Success)
        assertEquals(Unit, (result as BaseResult.Success).data)
    }

    @Test
    fun `error response maps to a typed Http error carrying the code`() = runTest {
        val response = respondWith(HttpStatusCode.NotFound, "nope")

        val result = response.toResult { }

        assertTrue(result is BaseResult.Error)
        val error = (result as BaseResult.Error).error
        assertTrue(error is NetworkError.Http)
        assertEquals(404, (error as NetworkError.Http).code)
    }

    @Test
    fun `error response does not invoke the onSuccess transform`() = runTest {
        val response = respondWith(HttpStatusCode.InternalServerError, "nope")

        var transformCalled = false
        response.toResult { transformCalled = true }

        assertEquals(false, transformCalled)
    }
}
