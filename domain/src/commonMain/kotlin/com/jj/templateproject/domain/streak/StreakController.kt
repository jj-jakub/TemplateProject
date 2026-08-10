package com.jj.templateproject.domain.streak

import com.jj.templateproject.domain.time.Clock

/**
 * A daily check-in streak: consecutive UTC calendar days [recordCheckIn] was called, reset to 1 the
 * moment a day is skipped.
 *
 * UTC rather than the device's local calendar day, deliberately: a local-day boundary would depend
 * on the device's timezone setting at check-in time, which would make the streak's own day boundary
 * silently different for a traveling user, or after a timezone change. A fixed UTC boundary is
 * simpler and fully deterministic, at the cost of not exactly matching a "midnight where I am"
 * intuition — a branching app that wants the latter should convert [Clock.nowMillis] to local
 * calendar days before calling in here.
 */
class StreakController(
    private val store: StreakStore,
    private val clock: Clock,
    private val reminderScheduler: ReminderScheduler,
) {

    /**
     * The streak as of now, without recording a new check-in. Reads as `0` once more than a day has
     * passed since the last check-in — the streak is already broken, even though nothing has written
     * that to [store] yet, since only [recordCheckIn] writes.
     */
    fun currentStreak(): Int {
        val lastDay = store.readLastCheckInEpochDay() ?: return 0
        return if (currentEpochDay() - lastDay <= 1) store.readStreakCount() else 0
    }

    /**
     * Records today's check-in and returns the streak count after it. Safe to call more than once
     * on the same day — the count only changes on the first call of a given day.
     */
    fun recordCheckIn(): Int {
        val today = currentEpochDay()
        val lastDay = store.readLastCheckInEpochDay()
        val newCount = when (lastDay) {
            today -> store.readStreakCount()
            today - 1 -> store.readStreakCount() + 1
            else -> 1
        }
        store.writeStreakCount(newCount)
        store.writeLastCheckInEpochDay(today)
        return newCount
    }

    fun isReminderEnabled(): Boolean = store.readReminderEnabled()

    fun setReminderEnabled(enabled: Boolean) {
        store.writeReminderEnabled(enabled)
        if (enabled) reminderScheduler.scheduleDailyReminder() else reminderScheduler.cancelDailyReminder()
    }

    private fun currentEpochDay(): Long = clock.nowMillis() / MILLIS_PER_DAY

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
