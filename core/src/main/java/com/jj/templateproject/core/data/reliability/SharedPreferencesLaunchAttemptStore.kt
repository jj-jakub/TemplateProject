package com.jj.templateproject.core.data.reliability

import android.content.Context
import com.jj.templateproject.domain.reliability.LaunchAttemptStore

/**
 * The launch counter, in its own SharedPreferences file.
 *
 * SharedPreferences rather than this app's DataStore, deliberately and against the usual advice.
 * Two properties are needed here that DataStore does not offer: a synchronous read, because the
 * answer is needed before anything else at startup, and a synchronous write, because an attempt
 * recorded asynchronously may never reach disk before the crash it exists to record.
 *
 * `commit()` for the same reason: `apply()` writes on a background thread, and a process that dies
 * moments later takes the unwritten value with it, which is precisely the launch this is counting.
 *
 * Its own file so backup rules can exclude it by name: a restored counter would drop a brand-new
 * install straight into safe mode (see backup_rules.xml).
 */
class SharedPreferencesLaunchAttemptStore(
    context: Context,
) : LaunchAttemptStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun readFailedLaunches(): Int = preferences.getInt(KEY_FAILED_LAUNCHES, 0)

    override fun writeFailedLaunches(count: Int) {
        preferences.edit().putInt(KEY_FAILED_LAUNCHES, count).commit()
    }

    companion object {
        /** Named in backup_rules.xml and data_extraction_rules.xml; keep the three in step. */
        const val FILE_NAME = "launch_stability"
        private const val KEY_FAILED_LAUNCHES = "failed_launches"
    }
}
