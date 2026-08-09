package com.jj.templateproject.core.data.reliability

import com.jj.templateproject.domain.reliability.LaunchAttemptStore
import platform.Foundation.NSUserDefaults

/**
 * The launch counter, in `NSUserDefaults`.
 *
 * The Android side has to reach past DataStore for SharedPreferences' `commit()` to get a write
 * that is on disk before the crash it exists to record. `NSUserDefaults` has no such split to get
 * wrong: `setInteger` updates the in-memory store synchronously and the framework flushes it
 * without the caller choosing, so there is no async-write variant to accidentally pick. It is not a
 * guarantee that the bytes reached the file before the next line runs, which is worth saying out
 * loud rather than assuming, but no API here offers a stronger one, and the value is re-read on the
 * next launch rather than mid-flight.
 *
 * Prefixed key rather than its own suite: iOS has nothing like `backup_rules.xml`, so a separate
 * store would not keep the counter out of an iCloud restore the way the Android one does. The
 * counter is self-healing instead, since the first launch that reaches stability clears it (see
 * `LaunchStability.markStable`), so a restored count costs at most one cautious launch.
 */
class UserDefaultsLaunchAttemptStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : LaunchAttemptStore {

    /** An absent key reads as 0, which `LaunchStability` already treats as a clean history. */
    override fun readFailedLaunches(): Int = defaults.integerForKey(KEY_FAILED_LAUNCHES).toInt()

    override fun writeFailedLaunches(count: Int) {
        defaults.setInteger(count.toLong(), KEY_FAILED_LAUNCHES)
    }

    private companion object {
        /** Namespaced, because `standardUserDefaults` is shared with every other preference. */
        const val KEY_FAILED_LAUNCHES = "launch_stability.failed_launches"
    }
}
