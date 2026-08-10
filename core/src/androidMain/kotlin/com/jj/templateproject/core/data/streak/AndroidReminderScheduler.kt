package com.jj.templateproject.core.data.streak

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.jj.templateproject.domain.streak.ReminderScheduler
import java.util.Calendar

/**
 * A daily inexact alarm targeting [DailyReminderReceiver], rescheduling itself via
 * `AlarmManager.INTERVAL_DAY` rather than a one-shot the receiver has to re-arm — simpler, at the
 * cost of the OS being free to batch/delay delivery by a few minutes (`setInexactRepeating`, not
 * `setExactAndAllowWhileIdle`), which is fine for a "sometime today" reminder and does not need the
 * `SCHEDULE_EXACT_ALARM` permission an exact alarm would.
 */
class AndroidReminderScheduler(
    private val context: Context,
) : ReminderScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleDailyReminder() {
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            nextTriggerAtMillis(),
            AlarmManager.INTERVAL_DAY,
            pendingIntent(),
        )
    }

    override fun cancelDailyReminder() {
        alarmManager.cancel(pendingIntent())
    }

    private fun pendingIntent(): PendingIntent {
        val intent = Intent(context, DailyReminderReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            STREAK_REMINDER_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    /** The next occurrence of [REMINDER_HOUR]:00 local time, today if it hasn't passed yet. */
    private fun nextTriggerAtMillis(): Long {
        val trigger = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, REMINDER_HOUR)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (trigger.timeInMillis <= System.currentTimeMillis()) {
            trigger.add(Calendar.DAY_OF_YEAR, 1)
        }
        return trigger.timeInMillis
    }

    private companion object {
        const val REMINDER_HOUR = 19
    }
}
