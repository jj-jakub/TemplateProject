package com.jj.templateproject.domain.notifications

import com.jj.templateproject.domain.push.PushMessage

/**
 * Shows a parsed [PushMessage] to the user.
 *
 * Takes a domain value rather than a platform `Intent`, which is what keeps this layer free of
 * Android types and lets the whole parse-and-route decision be tested without a device. Turning a
 * destination into something the system can launch is the implementation's job.
 */
interface NotificationManager {

    /**
     * @param notificationId which notification in the shade this occupies. Two messages with
     * different ids sit side by side; the same id replaces the earlier one.
     */
    fun show(message: PushMessage, notificationId: Int)
}

/** Does nothing, for a build or a test with no notification surface. */
class NoOpNotificationManager : NotificationManager {
    override fun show(message: PushMessage, notificationId: Int) = Unit
}
