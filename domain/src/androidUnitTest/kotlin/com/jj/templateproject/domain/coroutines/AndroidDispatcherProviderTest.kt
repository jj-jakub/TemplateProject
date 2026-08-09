package com.jj.templateproject.domain.coroutines

import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * The half of the dispatcher mapping that cannot be asserted in common code, because
 * `Dispatchers.IO` is not declared in kotlinx-coroutines' common source set. Each platform's actual
 * gets to answer this question for itself, so each platform's tests have to ask it.
 */
class AndroidDispatcherProviderTest {

    @Test
    fun `io maps to the JVM IO dispatcher`() {
        assertSame(Dispatchers.IO, DefaultDispatcherProvider().io)
    }
}
