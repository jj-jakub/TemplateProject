package com.jj.templateproject.domain.streak

/** Schedules (or cancels) a daily local notification nudging the user back to check in. */
interface ReminderScheduler {
    fun scheduleDailyReminder()
    fun cancelDailyReminder()
}

/** No local-notification scheduling wired up for this platform yet (today: iOS). */
object NoOpReminderScheduler : ReminderScheduler {
    override fun scheduleDailyReminder() = Unit
    override fun cancelDailyReminder() = Unit
}
