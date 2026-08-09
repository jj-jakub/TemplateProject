package com.jj.templateproject.domain.lifecycle

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Whether the app as a whole is in front of the user.
 *
 * Process-wide, not per-activity, which is the distinction that matters: a rotation destroys and
 * recreates an activity without the app ever leaving the foreground, so anything that starts or
 * stops work on an activity callback does it needlessly on every rotation.
 */
interface AppLifecycle {
    val isInForeground: StateFlow<Boolean>
}

/** A lifecycle a test drives by hand. */
class FakeAppLifecycle(initiallyInForeground: Boolean = true) : AppLifecycle {

    private val state = MutableStateFlow(initiallyInForeground)

    override val isInForeground: StateFlow<Boolean> = state.asStateFlow()

    fun moveToForeground() {
        state.value = true
    }

    fun moveToBackground() {
        state.value = false
    }
}
