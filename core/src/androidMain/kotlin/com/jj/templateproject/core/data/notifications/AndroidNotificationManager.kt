package com.jj.templateproject.core.data.notifications

import android.app.NotificationChannel
import android.content.Context
import android.media.AudioAttributes
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jj.templateproject.core.R
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.push.PushAction
import com.jj.templateproject.domain.push.PushDestination
import com.jj.templateproject.domain.push.PushMessage
import com.jj.templateproject.domain.push.PushRequestCodes
import android.app.NotificationManager as PlatformNotificationManager

/**
 * Posts a parsed [PushMessage] as a notification, with one action button per [PushAction].
 */
class AndroidNotificationManager(
    private val context: Context,
) : NotificationManager {

    override fun show(message: PushMessage, notificationId: Int) {
        val compat = NotificationManagerCompat.from(context)
        // Nothing below throws when notifications are off, it just silently does nothing, so the
        // check is here to make that outcome explicit rather than a mystery.
        if (!compat.areNotificationsEnabled()) return
        createChannel(context)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(message.title)
            .setContentText(message.body)
            // Without this a body longer than one line is silently truncated, which is most of them.
            .setStyle(NotificationCompat.BigTextStyle().bigText(message.body))
            .setContentIntent(
                pendingIntent(message.tap, PushRequestCodes.of(notificationId, PushRequestCodes.TAP)),
            )
            // The channel governs sound and heads-up from API 26 on, which is our whole range;
            // priority is set anyway because some OEM shades still read it when ranking what to peek.
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        message.actions.forEachIndexed { index, action ->
            builder.addAction(
                NotificationCompat.Action.Builder(
                    /* icon = */ 0,
                    action.label,
                    pendingIntent(
                        action.destination,
                        PushRequestCodes.of(notificationId, PushRequestCodes.actionSlot(index)),
                    ),
                ).build(),
            )
        }

        try {
            compat.notify(notificationId, builder.build())
        } catch (_: SecurityException) {
            // Permission revoked between the enabled-check and the post: a lost notification,
            // nothing more, and never worth taking down a background service for.
        }
    }

    private fun pendingIntent(destination: PushDestination, requestCode: Int) =
        PushIntents.pendingIntent(context, destination, requestCode)

    companion object {
        /** Also named in the manifest, so a message that slips past our parser still lands here. */
        const val CHANNEL_ID = "announcements"
        private const val CHANNEL_NAME = "News and updates"

        /**
         * Creates the channel a campaign arrives on. **IMPORTANCE_HIGH**, which buys the two things
         * a user actually notices: a heads-up banner and a sound. IMPORTANCE_DEFAULT posts silently
         * into the shade, so a notification sent while the app is open lands correctly and is never
         * seen.
         *
         * A channel's importance is fixed at creation: `createNotificationChannel` will not raise an
         * existing one, and only the user may change it (downward) in system settings. So changing
         * this later means a NEW channel id and deleting the old one, or every install that already
         * ran the earlier build keeps the old behaviour forever.
         *
         * Safe to call repeatedly, and worth calling on app start as well as before posting, so the
         * channel named in the manifest exists before the SDK ever needs its fallback.
         */
        fun createChannel(context: Context) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as PlatformNotificationManager
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, PlatformNotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Announcements, offers and other news"
                    enableVibration(true)
                    // Explicit rather than relying on the channel default, so the sound is a decision
                    // in the code rather than a platform behaviour that could differ per OEM.
                    setSound(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build(),
                    )
                },
            )
        }
    }
}
