package com.jj.templateproject.core.data.streak

import android.content.Context
import com.jj.templateproject.domain.streak.StreakStore

/**
 * The streak count, last check-in day, and reminder opt-in, in their own SharedPreferences file.
 * **Not** listed in `backup_rules.xml`/`data_extraction_rules.xml`, unlike this codebase's other
 * platform stores — see `StreakStore`'s own doc comment for why a streak is meant to travel.
 */
class SharedPreferencesStreakStore(
    context: Context,
) : StreakStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun readStreakCount(): Int = preferences.getInt(KEY_STREAK_COUNT, 0)

    override fun writeStreakCount(count: Int) {
        preferences.edit().putInt(KEY_STREAK_COUNT, count).apply()
    }

    override fun readLastCheckInEpochDay(): Long? =
        if (preferences.contains(KEY_LAST_CHECK_IN_EPOCH_DAY)) {
            preferences.getLong(KEY_LAST_CHECK_IN_EPOCH_DAY, 0L)
        } else {
            null
        }

    override fun writeLastCheckInEpochDay(epochDay: Long) {
        preferences.edit().putLong(KEY_LAST_CHECK_IN_EPOCH_DAY, epochDay).apply()
    }

    override fun readReminderEnabled(): Boolean = preferences.getBoolean(KEY_REMINDER_ENABLED, false)

    override fun writeReminderEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_REMINDER_ENABLED, enabled).apply()
    }

    private companion object {
        const val FILE_NAME = "streak"
        const val KEY_STREAK_COUNT = "streak_count"
        const val KEY_LAST_CHECK_IN_EPOCH_DAY = "last_check_in_epoch_day"
        const val KEY_REMINDER_ENABLED = "reminder_enabled"
    }
}
