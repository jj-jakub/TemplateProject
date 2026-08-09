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

/**
 * Production [DispatcherProvider] backed by the real [Dispatchers].
 *
 * `expect` rather than a plain class for one member: kotlinx-coroutines does not declare
 * `Dispatchers.IO` in its common source set, so the only way for shared code to name an IO
 * dispatcher at all is for each platform to supply its own. Everything else here is identical
 * across targets, which is exactly why the split is at the class rather than at every call site.
 */
expect class DefaultDispatcherProvider() : DispatcherProvider
