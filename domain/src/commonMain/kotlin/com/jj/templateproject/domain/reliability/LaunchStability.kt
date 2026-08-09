package com.jj.templateproject.domain.reliability

/** How the app should start: normally, or holding back whatever might be crashing it. */
enum class LaunchMode {
    NORMAL,

    /**
     * Two launches in a row failed to reach stability, so anything restored from disk is a suspect.
     * Whatever a feature rehydrates at startup (a cached response, a session, a serialized screen
     * state) should be skipped once, and rebuilt from scratch instead.
     */
    SAFE,
}

/**
 * Where the launch counter is kept. Deliberately not the app's ordinary preference store: this has
 * to be readable **synchronously**, before anything restores, and written before the risky work
 * begins, so an asynchronous store would record the attempt only after the crash it exists to catch.
 */
interface LaunchAttemptStore {
    fun readFailedLaunches(): Int
    fun writeFailedLaunches(count: Int)
}

/**
 * Counts launches that never reached stability, so a poisoned piece of persisted state cannot brick
 * an install.
 *
 * Every launch calls [beginLaunch], which banks one attempt; a launch that survives long enough to
 * show something calls [markStable], which clears the count. Once [SAFE_MODE_THRESHOLD] launches in
 * a row have failed to do that, the next one starts in [LaunchMode.SAFE].
 *
 * The failure this defends against is the one with no other remedy: state written by the app itself
 * that crashes the app while reading it back. Every subsequent launch reads the same bytes and dies
 * the same way, and the only fix left to the user is to clear the app's data or reinstall.
 *
 * Absent or malformed data reads as a clean history, so a first launch is never treated as a
 * failure.
 */
class LaunchStability(private val store: LaunchAttemptStore) {

    /** The mode this launch resolved to, for anything deciding whether to restore something. */
    var mode: LaunchMode = LaunchMode.NORMAL
        private set

    /** Banks this launch attempt and reports how it should proceed. */
    fun beginLaunch(): LaunchMode {
        val failures = store.readFailedLaunches().coerceAtLeast(0)
        store.writeFailedLaunches(failures + 1)
        mode = if (failures >= SAFE_MODE_THRESHOLD) LaunchMode.SAFE else LaunchMode.NORMAL
        return mode
    }

    /** The launch survived: clears the failure history. */
    fun markStable() {
        store.writeFailedLaunches(0)
    }

    companion object {
        /** Consecutive launches that never reached stability before the next one holds state back. */
        const val SAFE_MODE_THRESHOLD = 2
    }
}
