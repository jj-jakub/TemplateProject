package com.jj.templateproject.data.google

import com.jj.templateproject.data.TestDispatcherProvider
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DefaultTemplateRepositoryTest {

    private val network = FakeTemplateNetwork()
    private val repository = DefaultTemplateRepository(network, TestDispatcherProvider())

    @Test
    fun `getGoogleData delegates to the network layer`() = runTest {
        val expected = BaseResult.Success<String, NetworkError>("200")
        network.dataResult = expected

        val result = repository.getGoogleData()

        assertEquals(expected, result)
        assertEquals(1, network.dataCallCount)
    }

    @Test
    fun `getGoogleStatus delegates to the network layer`() = runTest {
        val expected = BaseResult.Success<Unit, NetworkError>(Unit)
        network.statusResult = expected

        val result = repository.getGoogleStatus()

        assertEquals(expected, result)
        assertEquals(1, network.statusCallCount)
    }
}
