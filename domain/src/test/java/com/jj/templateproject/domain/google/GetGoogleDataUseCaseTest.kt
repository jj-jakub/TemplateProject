package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetGoogleDataUseCaseTest {

    private val repository = mockk<TemplateRepository>()
    private val useCase = GetGoogleDataUseCase(repository)

    @Test
    fun `invoke returns the repository result unchanged`() = runTest {
        val expected = BaseResult.Success<String, NetworkError>("200")
        coEvery { repository.getGoogleData() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository getGoogleData exactly once`() = runTest {
        coEvery { repository.getGoogleData() } returns BaseResult.Success("200")

        useCase()

        coVerify(exactly = 1) { repository.getGoogleData() }
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        val expected = BaseResult.Error<String, NetworkError>(NetworkError.Http(500, "boom"))
        coEvery { repository.getGoogleData() } returns expected

        val result = useCase()

        assertEquals(expected, result)
    }
}
