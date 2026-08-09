package com.jj.templateproject.domain.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class DefaultDispatcherProvider actual constructor() : DispatcherProvider {
    override val main: CoroutineDispatcher get() = Dispatchers.Main

    /**
     * There is no public IO dispatcher on Kotlin/Native: `Dispatchers.IO` is `internal` there, so
     * [Dispatchers.Default] is what an iOS build can actually name.
     *
     * Less of a compromise than it sounds. The JVM's IO pool exists to park threads blocked on
     * syscalls, and shared code reaching this dispatcher on iOS is calling Ktor's Darwin engine or
     * a suspending platform API, neither of which blocks a thread. What the caller is promised —
     * that `io` takes work off the main thread — still holds.
     */
    override val io: CoroutineDispatcher get() = Dispatchers.Default

    override val default: CoroutineDispatcher get() = Dispatchers.Default
    override val unconfined: CoroutineDispatcher get() = Dispatchers.Unconfined
}
