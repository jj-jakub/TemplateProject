package com.jj.templateproject.data.firebase

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jj.templateproject.data.config.BuildProfile

/**
 * Joins the audience a broadcast campaign is sent to, and makes this device addressable while
 * developing.
 *
 * Unlike analytics, this runs in **every** build including debug: a test send has to be able to
 * reach a development device, and a campaign aimed at everyone is aimed at a topic rather than at a
 * list of tokens.
 */
object PushRegistrar {

    /** The topic every install subscribes to, and what a broadcast campaign is addressed to. */
    const val BROADCAST_TOPIC = "all"

    /** Shared with the token print so both ends are one `adb logcat` filter. */
    const val TAG = "push"

    /**
     * Idempotent, and safe to call on every launch: that is what lets an install that predates a
     * topic still join it.
     */
    fun register(context: Context) {
        // Firebase is absent until a google-services.json is bundled, and asking FirebaseMessaging
        // for an instance without it throws, so this check is not optional.
        if (FirebaseApp.getApps(context).isEmpty()) return
        FirebaseMessaging.getInstance().subscribeToTopic(BROADCAST_TOPIC)

        // Printed on every debug launch rather than only when the token changes: logcat's ring
        // buffer evicts a registration line within minutes on a busy device, so aiming a test send
        // at this device would otherwise mean reinstalling to force a fresh one.
        if (BuildProfile.isDebugBuild) {
            FirebaseMessaging.getInstance().token.addOnSuccessListener(::printToken)
        }
    }

    /** Called from the messaging service when FCM rotates the token. */
    fun printTokenIfDebuggable(token: String) {
        if (BuildProfile.isDebugBuild) printToken(token)
    }

    // Never on a build a user could be running: a token is a per-device address.
    private fun printToken(token: String) = Log.d(TAG, "device token: $token")
}
