package com.jj.templateproject.data.firebase

import android.content.Intent
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jj.templateproject.presentation.activities.MainActivity
import org.koin.android.ext.android.inject

class DefaultFirebaseMessagingService : FirebaseMessagingService() {

    private val notificationManager: com.jj.templateproject.domain.notifications.NotificationManager by inject()

    init {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                val token = task.result
                Log.d("ABAB", "token: $token")
            }
        )
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("ABAB", "token: $token")
        // TODO
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title.orEmpty()
        val body = remoteMessage.notification?.body.orEmpty()

        val appPackageNameParam = getAppPackageName(remoteMessage)
        if (appPackageNameParam != null) {
            notificationManager.showPushNotification(
                title = title,
                body = body,
                intent = getAppPageOpeningIntent(appPackageNameParam),
            )
        } else {
            notificationManager.showPushNotification(
                title = title,
                body = body,
                intent = Intent(this, MainActivity::class.java),
            )
        }
    }
}