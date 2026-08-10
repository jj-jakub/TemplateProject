package com.jj.templateproject.core.data.streak

import com.jj.templateproject.domain.streak.StreakStore
import platform.Foundation.NSUserDefaults

/**
 * The streak count, last check-in day, and reminder opt-in, in `NSUserDefaults` — same shape as
 * `UserDefaultsAchievementStore`: not excluded from anything, since this is the user's progress.
 *
 * The reminder flag persists here even though no `ReminderScheduler` actually schedules anything on
 * iOS yet (`NoOpReminderScheduler`) — so a toggle a user already turned on is remembered and takes
 * effect automatically once a real iOS scheduler is wired up, rather than silently reverting.
 */
class UserDefaultsStreakStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : StreakStore {

    override fun readStreakCount(): Int = defaults.integerForKey(KEY_STREAK_COUNT).toInt()

    override fun writeStreakCount(count: Int) {
        defaults.setInteger(count.toLong(), KEY_STREAK_COUNT)
    }

    override fun readLastCheckInEpochDay(): Long? =
        if (defaults.objectForKey(KEY_LAST_CHECK_IN_EPOCH_DAY) != null) {
            defaults.integerForKey(KEY_LAST_CHECK_IN_EPOCH_DAY)
        } else {
            null
        }

    override fun writeLastCheckInEpochDay(epochDay: Long) {
        defaults.setInteger(epochDay, KEY_LAST_CHECK_IN_EPOCH_DAY)
    }

    override fun readReminderEnabled(): Boolean = defaults.boolForKey(KEY_REMINDER_ENABLED)

    override fun writeReminderEnabled(enabled: Boolean) {
        defaults.setBool(enabled, KEY_REMINDER_ENABLED)
    }

    private companion object {
        const val KEY_STREAK_COUNT = "streak.count"
        const val KEY_LAST_CHECK_IN_EPOCH_DAY = "streak.last_check_in_epoch_day"
        const val KEY_REMINDER_ENABLED = "streak.reminder_enabled"
    }
}
