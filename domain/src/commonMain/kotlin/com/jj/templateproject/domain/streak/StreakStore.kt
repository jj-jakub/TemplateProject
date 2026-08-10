package com.jj.templateproject.domain.streak

/**
 * Where the streak count, the day of the last check-in, and the reminder opt-in flag are kept.
 * Deliberately **not** excluded from backup, unlike `LaunchAttemptStore`/`ReviewPromptStore`/
 * `InstallIdStore`: a streak is the user's progress, the same reasoning `AchievementStore`'s doc
 * comment gives for its own opt-out.
 */
interface StreakStore {
    fun readStreakCount(): Int
    fun writeStreakCount(count: Int)

    /** UTC epoch day (see `StreakController`), or `null` before the first ever check-in. */
    fun readLastCheckInEpochDay(): Long?
    fun writeLastCheckInEpochDay(epochDay: Long)

    fun readReminderEnabled(): Boolean
    fun writeReminderEnabled(enabled: Boolean)
}
