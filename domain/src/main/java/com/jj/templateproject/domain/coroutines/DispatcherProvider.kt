package com.jj.templateproject.domain.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Indirection over [Dispatchers] so coroutine code never hard-codes a dispatcher and can be
 * driven by a test dispatcher in unit tests (no `Dispatchers.setMain` hacks needed).
 *
 * Inject this as a `private val` into repositories/use cases/ViewModels and switch threads with
 * `withContext(dispatcherProvider.io) { … }`.
 */
interface DispatcherProvider {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}

/** Production [DispatcherProvider] backed by the real [Dispatchers]. */
class DefaultDispatcherProvider : DispatcherProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main
    override val io: CoroutineDispatcher get() = Dispatchers.IO
    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
}
