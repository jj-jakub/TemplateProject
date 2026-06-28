package com.jj.templateproject.presentation.ui.state

import com.jj.templateproject.domain.BaseError
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

private data class SampleError(val why: String) : BaseError

class UiStateTest {

    @Test
    fun `isLoading and isSuccess reflect the state`() {
        assertTrue(UiState.Loading.isLoading)
        assertFalse(UiState.Loading.isSuccess)
        assertTrue(UiState.Success(1).isSuccess)
        assertFalse(UiState.Success(1).isLoading)
    }

    @Test
    fun `dataOrNull returns the value only for success`() {
        assertEquals(5, UiState.Success(5).dataOrNull())
        assertNull(UiState.Loading.dataOrNull())
        assertNull(UiState.Empty.dataOrNull())
        assertNull(UiState.Error("x").dataOrNull())
    }

    @Test
    fun `map transforms success and passes other states through`() {
        assertEquals(UiState.Success("2"), UiState.Success(2).map { it.toString() })
        assertEquals(UiState.Loading, UiState.Loading.map { it })
        assertEquals(UiState.Empty, UiState.Empty.map { it })
        assertEquals(UiState.Error("boom"), UiState.Error("boom").map { it })
    }

    @Test
    fun `toUiState bridges a networking result`() {
        val success: BaseResult<String, NetworkError> = BaseResult.Success("hi")
        val failure: BaseResult<String, NetworkError> = BaseResult.Error(NetworkError.Timeout)

        assertEquals(UiState.Success("hi"), success.toUiState())
        assertEquals(UiState.Error("The request timed out"), failure.toUiState())
    }

    @Test
    fun `toUiState bridges any result with a custom error mapper`() {
        val failure: BaseResult<Int, SampleError> = BaseResult.Error(SampleError("nope"))

        val state = failure.toUiState { it.why.uppercase() }

        assertEquals(UiState.Error("NOPE"), state)
    }
}
