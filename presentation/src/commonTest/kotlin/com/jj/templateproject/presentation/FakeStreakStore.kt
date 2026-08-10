package com.jj.templateproject.presentation

import com.jj.templateproject.domain.streak.StreakStore

class FakeStreakStore(
    var count: Int = 0,
    var lastCheckInEpochDay: Long? = null,
    var reminderEnabled: Boolean = false,
) : StreakStore {
    override fun readStreakCount() = count
    override fun writeStreakCount(count: Int) {
        this.count = count
    }

    override fun readLastCheckInEpochDay() = lastCheckInEpochDay
    override fun writeLastCheckInEpochDay(epochDay: Long) {
        lastCheckInEpochDay = epochDay
    }

    override fun readReminderEnabled() = reminderEnabled
    override fun writeReminderEnabled(enabled: Boolean) {
        reminderEnabled = enabled
    }
}
