package com.jj.templateproject.data.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.push.PushPayload
import org.koin.android.ext.android.inject

/**
 * Receives push campaigns and shows them.
 *
 * Reached for **data-only** messages in every app state — foreground, background, and after the
 * process was killed, which FCM starts us for. A message carrying a `notification` block would be
 * drawn by the SDK itself while backgrounded, without our action buttons and without ever calling
 * this code, which is why PUSH.md's sends are data-only.
 *
 * Whatever the sender wrote is remote input parsed inside a background service, so an unusable
 * payload is dropped silently rather than crashing an app the user may not even have open.
 */
class DefaultFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: NotificationManager by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        val push = PushPayload.parse(message.data) ?: return
        notificationManager.show(push, NOTIFICATION_ID_BASE + push.notificationSlot)
    }

    override fun onNewToken(token: String) {
        // The topic subscription is what campaigns are actually sent to (see PushRegistrar), and FCM
        // re-registers it itself, so nothing has to be uploaded here. A per-device token is only
        // useful for aiming a test send at this device, so it is printed for a developer on a debug
        // build and never sent anywhere.
        PushRegistrar.printTokenIfDebuggable(token)
    }

    private companion object {
        /** Leaves the low ids free for any notification the app posts itself. */
        const val NOTIFICATION_ID_BASE = 2000
    }
}
