package com.jj.templateproject.domain.streak

import com.jj.templateproject.domain.time.FixedClock
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

class StreakControllerTest {

    private class FakeStreakStore(
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

    private class RecordingScheduler : ReminderScheduler {
        var scheduleCount = 0
            private set
        var cancelCount = 0
            private set

        override fun scheduleDailyReminder() {
            scheduleCount++
        }

        override fun cancelDailyReminder() {
            cancelCount++
        }
    }

    private val millisPerDay = 24L * 60L * 60L * 1000L

    @Test
    fun `the first ever check-in starts a streak of 1`() {
        val controller = StreakController(FakeStreakStore(), FixedClock(now = 0L), NoOpReminderScheduler)

        assertEquals(1, controller.recordCheckIn())
    }

    @Test
    fun `checking in again the same day does not change the count`() {
        val clock = FixedClock(now = 0L)
        val controller = StreakController(FakeStreakStore(), clock, NoOpReminderScheduler)
        controller.recordCheckIn()

        val second = controller.recordCheckIn()

        assertEquals(1, second)
    }

    @Test
    fun `checking in the next day increments the streak`() {
        val clock = FixedClock(now = 0L)
        val controller = StreakController(FakeStreakStore(), clock, NoOpReminderScheduler)
        controller.recordCheckIn()

        clock.advance(millisPerDay)
        val second = controller.recordCheckIn()

        assertEquals(2, second)
    }

    @Test
    fun `skipping a day resets the streak to 1`() {
        val clock = FixedClock(now = 0L)
        val controller = StreakController(FakeStreakStore(), clock, NoOpReminderScheduler)
        controller.recordCheckIn()

        clock.advance(millisPerDay * 2)
        val afterGap = controller.recordCheckIn()

        assertEquals(1, afterGap)
    }

    @Test
    fun `currentStreak reflects a broken streak even before the next check-in writes it`() {
        val clock = FixedClock(now = 0L)
        val store = FakeStreakStore()
        val controller = StreakController(store, clock, NoOpReminderScheduler)
        controller.recordCheckIn()

        clock.advance(millisPerDay * 3)

        assertEquals(0, controller.currentStreak())
        // The store itself is untouched until recordCheckIn() runs again.
        assertEquals(1, store.count)
    }

    @Test
    fun `currentStreak is 0 before any check-in ever happened`() {
        val controller = StreakController(FakeStreakStore(), FixedClock(now = 0L), NoOpReminderScheduler)

        assertEquals(0, controller.currentStreak())
    }

    @Test
    fun `enabling the reminder schedules it and persists the flag`() {
        val store = FakeStreakStore()
        val scheduler = RecordingScheduler()
        val controller = StreakController(store, FixedClock(now = 0L), scheduler)

        controller.setReminderEnabled(true)

        assertTrue(store.reminderEnabled)
        assertEquals(1, scheduler.scheduleCount)
        assertEquals(0, scheduler.cancelCount)
    }

    @Test
    fun `disabling the reminder cancels it and persists the flag`() {
        val store = FakeStreakStore(reminderEnabled = true)
        val scheduler = RecordingScheduler()
        val controller = StreakController(store, FixedClock(now = 0L), scheduler)

        controller.setReminderEnabled(false)

        assertFalse(store.reminderEnabled)
        assertEquals(1, scheduler.cancelCount)
        assertEquals(0, scheduler.scheduleCount)
    }
}
