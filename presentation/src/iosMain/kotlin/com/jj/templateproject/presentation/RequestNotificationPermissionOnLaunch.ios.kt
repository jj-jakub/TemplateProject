package com.jj.templateproject.presentation

import androidx.compose.runtime.Composable

/**
 * No-op. iOS has no equivalent of Android 13's `POST_NOTIFICATIONS` runtime permission; a real
 * notification-permission ask there is `UNUserNotificationCenter.requestAuthorization`, which
 * belongs beside the rest of the deferred push-notification work (see
 * `NotificationManager`'s iOS binding in `:core`), not here.
 */
@Composable
actual fun RequestNotificationPermissionOnLaunch() = Unit
