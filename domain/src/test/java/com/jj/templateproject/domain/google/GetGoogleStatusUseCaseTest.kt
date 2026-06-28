package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetGoogleStatusUseCaseTest {

    private val repository = mockk<TemplateRepository>()
    private val useCase = GetGoogleStatusUseCase(repository)

    @Test
    fun `invoke returns the repository result unchanged`() = runTest {
        val expected = BaseResult.Success<Unit, NetworkError>(Unit)
        coEvery { repository.getGoogleStatus() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository getGoogleStatus exactly once`() = runTest {
        coEvery { repository.getGoogleStatus() } returns BaseResult.Success(Unit)

        useCase()

        coVerify(exactly = 1) { repository.getGoogleStatus() }
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        val expected = BaseResult.Error<Unit, NetworkError>(NetworkError(404, "not found"))
        coEvery { repository.getGoogleStatus() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }
}
