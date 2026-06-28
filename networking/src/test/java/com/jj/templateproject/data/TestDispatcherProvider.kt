package com.jj.templateproject.data

import com.jj.templateproject.domain.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * A [DispatcherProvider] that routes every dispatcher to a single (test) dispatcher, so coroutine
 * code under test runs deterministically. Defaults to [Dispatchers.Unconfined] for simple cases.
 */
class TestDispatcherProvider(
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
) : DispatcherProvider {
    override val main: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val unconfined: CoroutineDispatcher = dispatcher
}
