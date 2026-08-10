package com.jj.templateproject.core.data.streak

import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jj.templateproject.core.R
import android.app.NotificationManager as PlatformNotificationManager

/**
 * Fires once a day (see [AndroidReminderScheduler]) and posts the streak reminder. A plain
 * `BroadcastReceiver` rather than routing through the domain [com.jj.templateproject.domain.notifications.NotificationManager]
 * seam: that interface takes a `PushMessage`, and this notification has no server-originated
 * payload to parse — it is simpler to build directly, the same way [DailyReminderReceiver] owns its
 * own channel rather than sharing `AndroidNotificationManager`'s "announcements" one, so a user can
 * mute one without muting the other.
 */
class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val compat = NotificationManagerCompat.from(context)
        if (!compat.areNotificationsEnabled()) return
        createChannel(context)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.streak_reminder_title))
            .setContentText(context.getString(R.string.streak_reminder_body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            compat.notify(STREAK_REMINDER_ID, notification)
        } catch (_: SecurityException) {
            // Permission revoked between the enabled-check and the post: a lost reminder, nothing
            // more, and tomorrow's alarm will simply try again.
        }
    }

    companion object {
        const val CHANNEL_ID = "streak_reminder"
        private const val CHANNEL_NAME = "Streak reminders"

        fun createChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as PlatformNotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, PlatformNotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "A once-a-day nudge to keep your streak going"
                },
            )
        }
    }
}
