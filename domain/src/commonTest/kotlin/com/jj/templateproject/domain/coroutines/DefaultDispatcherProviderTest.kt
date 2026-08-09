package com.jj.templateproject.domain.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class DefaultDispatcherProviderTest {

    private val provider = DefaultDispatcherProvider()

    @Test
    fun `maps each shared role to the corresponding kotlinx dispatcher`() {
        // Only these three are declared in kotlinx-coroutines' common source set. The IO role is
        // per-platform by necessity, so it is asserted from the Android source set instead.
        assertSame(Dispatchers.Main, provider.main)
        assertSame(Dispatchers.Default, provider.default)
        assertSame(Dispatchers.Unconfined, provider.unconfined)
    }

    @Test
    fun `io is a dispatcher of its own rather than an alias for the main thread`() {
        // The property worth holding across every target: switching to `io` must take work off the
        // main thread, whatever each platform's IO dispatcher turns out to be.
        assertNotSame(Dispatchers.Main, provider.io)
    }
}
