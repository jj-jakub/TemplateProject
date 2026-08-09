package com.jj.templateproject.core.data.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.jj.templateproject.domain.lifecycle.AppLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks the foreground state of the whole process, via `ProcessLifecycleOwner`.
 *
 * That owner is what makes this survive a rotation without a spurious background/foreground pair:
 * it only reports a background transition once every activity has stopped and stayed stopped, rather
 * than the instant one is destroyed.
 *
 * Observation starts on construction, so this must be created while the process is starting up (the
 * Koin graph is built in `Application.onCreate`), not lazily from a screen.
 */
class ProcessAppLifecycle : AppLifecycle, DefaultLifecycleObserver {

    private val state = MutableStateFlow(false)

    override val isInForeground: StateFlow<Boolean> = state.asStateFlow()

    init {
        // Replays the current state on registration, so a late observer is not stuck reporting
        // background for an app that is plainly on screen.
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        state.value = true
    }

    override fun onStop(owner: LifecycleOwner) {
        state.value = false
    }
}
