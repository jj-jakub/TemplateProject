package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetGoogleDataUseCaseTest {

    private val repository = FakeTemplateRepository()
    private val useCase = GetGoogleDataUseCase(repository)

    @Test
    fun `invoke returns the repository result unchanged`() = runTest {
        val expected = BaseResult.Success<String, NetworkError>("200")
        repository.dataResult = expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository getGoogleData exactly once`() = runTest {
        useCase()

        assertEquals(1, repository.dataCallCount)
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        val expected = BaseResult.Error<String, NetworkError>(NetworkError.Http(500, "boom"))
        repository.dataResult = expected

        val result = useCase()

        assertEquals(expected, result)
    }
}
