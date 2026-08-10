package com.jj.templateproject.core.data.streak

/**
 * Shared between [AndroidReminderScheduler] (the `PendingIntent` request code) and
 * [DailyReminderReceiver] (the notification id) — there is only ever one of these active at a
 * time, so one constant covers both. Clear of push's `NOTIFICATION_ID_BASE` (2000) range on
 * purpose (see `DefaultFirebaseMessagingService`).
 */
internal const val STREAK_REMINDER_ID = 3000
