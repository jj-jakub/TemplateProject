package com.jj.templateproject.domain.coroutines

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class DefaultDispatcherProviderTest {

    private val provider = DefaultDispatcherProvider()

    @Test
    fun `maps each role to the corresponding kotlinx dispatcher`() {
        assertSame(Dispatchers.Main, provider.main)
        assertSame(Dispatchers.IO, provider.io)
        assertSame(Dispatchers.Default, provider.default)
        assertSame(Dispatchers.Unconfined, provider.unconfined)
    }
}
