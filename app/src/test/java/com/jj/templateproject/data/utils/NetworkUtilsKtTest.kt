package com.jj.templateproject.data.utils

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.Response

class NetworkUtilsKtTest {
    @Test
    fun `safeCall returns Success when network call is successful`() = runTest {
        val networkCall: suspend () -> Response<Unit> = {
            Response.success(Unit)
        }

        val result = safeCall { networkCall() }

        assertTrue(result is NetworkCallResult.Success)
    }

    @Test
    fun `safeCall returns Failure when network call throws exception`() = runTest {
        val exception = Exception("Network error")
        val networkCall: suspend () -> Response<Unit> = {
            throw exception
        }

        val result = safeCall { networkCall() }

        assertTrue(result is NetworkCallResult.Failure)
    }
}
