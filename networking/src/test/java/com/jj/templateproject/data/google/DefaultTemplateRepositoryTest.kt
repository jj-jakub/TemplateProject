package com.jj.templateproject.data.google

import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultTemplateRepositoryTest {

    private val network = mockk<TemplateNetwork>()
    private val repository = DefaultTemplateRepository(network)

    @Test
    fun `getGoogleData delegates to the network layer`() = runTest {
        val expected = BaseResult.Success<String, NetworkError>("200")
        coEvery { network.getGoogleData() } returns expected

        val result = repository.getGoogleData()

        assertEquals(expected, result)
        coVerify(exactly = 1) { network.getGoogleData() }
    }

    @Test
    fun `getGoogleStatus delegates to the network layer`() = runTest {
        val expected = BaseResult.Success<Unit, NetworkError>(Unit)
        coEvery { network.getGoogleStatus() } returns expected

        val result = repository.getGoogleStatus()

        assertEquals(expected, result)
        coVerify(exactly = 1) { network.getGoogleStatus() }
    }
}
