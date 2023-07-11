package com.jj.templateproject.core.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.jj.templateproject.core.R
import com.jj.templateproject.core.data.notifications.model.PushMessageKeys.APP_PACKAGE_PENDING_INTENT_REQUEST_CODE
import com.jj.templateproject.core.data.notifications.model.PushMessageKeys.PUSH_NOTIFICATION_CHANNEL_ID
import com.jj.templateproject.core.data.notifications.model.PushMessageKeys.PUSH_NOTIFICATION_CHANNEL_TITLE
import com.jj.templateproject.core.data.notifications.model.PushMessageKeys.PUSH_NOTIFICATION_ID

class AndroidNotificationManager(
    private val context: Context,
) : com.jj.templateproject.domain.notifications.NotificationManager {

    override fun showPushNotification(
        title: String,
        body: String,
        intent: Intent,
    ) {
        val notificationBuilder = createNotificationBuilder(
            title = title,
            body = body,
        )
        val pendingIntent = PendingIntent.getActivity(
            /* context = */ context,
            /* requestCode = */ APP_PACKAGE_PENDING_INTENT_REQUEST_CODE,
            /* intent = */ intent,
            /* flags = */ PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        notificationBuilder.setContentIntent(pendingIntent)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationBuilder, notificationManager)
        notificationManager.notify(PUSH_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationBuilder(
        title: String,
        body: String,
    ) = NotificationCompat.Builder(
        context,
        PUSH_NOTIFICATION_CHANNEL_ID,
    ).apply {
        setContentTitle(title)
        setContentText(body)
        priority = NotificationCompat.PRIORITY_DEFAULT
        setStyle(NotificationCompat.BigTextStyle())
        setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        setSmallIcon(R.drawable.ic_launcher_background)
        setAutoCancel(true)
    }

    private fun createNotificationChannel(
        notificationBuilder: NotificationCompat.Builder,
        notificationManager: NotificationManager
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                PUSH_NOTIFICATION_CHANNEL_ID,
                PUSH_NOTIFICATION_CHANNEL_TITLE,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
            notificationBuilder.setChannelId(PUSH_NOTIFICATION_CHANNEL_ID)
        }
    }
}