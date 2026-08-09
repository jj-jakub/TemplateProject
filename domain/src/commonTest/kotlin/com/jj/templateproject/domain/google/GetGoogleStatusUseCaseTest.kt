package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetGoogleStatusUseCaseTest {

    private val repository = FakeTemplateRepository()
    private val useCase = GetGoogleStatusUseCase(repository)

    @Test
    fun `invoke returns the repository result unchanged`() = runTest {
        val expected = BaseResult.Success<Unit, NetworkError>(Unit)
        repository.statusResult = expected

        val result = useCase()

        assertEquals(expected, result)
    }

    @Test
    fun `invoke delegates to repository getGoogleStatus exactly once`() = runTest {
        useCase()

        assertEquals(1, repository.statusCallCount)
    }

    @Test
    fun `invoke propagates repository errors`() = runTest {
        val expected = BaseResult.Error<Unit, NetworkError>(NetworkError.Http(404, "not found"))
        repository.statusResult = expected

        val result = useCase()

        assertEquals(expected, result)
    }
}
