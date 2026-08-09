package com.jj.templateproject.core.data.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jj.templateproject.domain.push.PushDestination
import com.jj.templateproject.domain.push.PushDestinations

/**
 * Turns a [PushDestination] into something the system can launch, and reads it back when the launch
 * lands in the activity.
 *
 * In-app destinations go through this app's own launcher activity, which hands them on;
 * a [PushDestination.PlayStoreApp] is a plain view intent instead. That asymmetry is deliberate: an
 * external destination should open its target directly, not flash our app on the way. It also cannot
 * be done any other way — since Android 12 a notification may not start an activity indirectly
 * through a receiver or service, so the view intent has to be the PendingIntent the notification
 * itself holds.
 *
 * The activity is resolved from the package rather than named as a class, so this stays in the
 * platform layer without depending on the app module that owns the activity.
 */
object PushIntents {

    const val EXTRA_DESTINATION = "com.jj.templateproject.push.DESTINATION"

    fun pendingIntent(
        context: Context,
        destination: PushDestination,
        requestCode: Int,
    ): PendingIntent = when (destination) {
        is PushDestination.PlayStoreApp -> PendingIntent.getActivity(
            context,
            requestCode,
            Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl(destination.packageName))),
            FLAGS,
        )

        else -> PendingIntent.getActivity(context, requestCode, appIntent(context, destination), FLAGS)
    }

    /** The destination carried by the intent that started (or resumed) the activity, if any. */
    fun destinationOf(intent: Intent?): PushDestination? =
        PushDestinations.parse(intent?.getStringExtra(EXTRA_DESTINATION))

    private fun appIntent(context: Context, destination: PushDestination): Intent =
        (context.packageManager.getLaunchIntentForPackage(context.packageName) ?: Intent()).apply {
            putExtra(EXTRA_DESTINATION, PushDestinations.token(destination))
            // Resume the running task rather than stacking a second copy of the app. The launcher
            // activity is declared singleTop, so an already-live one receives this through
            // onNewIntent instead of being recreated.
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

    // The web form rather than market://, which resolves to nothing on a device with no store app
    // and would leave the tap doing nothing at all.
    private fun storeUrl(packageName: String) = "https://play.google.com/store/apps/details?id=$packageName"

    // Mutable PendingIntents are refused on API 31+, and every intent here is fully specified anyway.
    // UPDATE_CURRENT so a newer campaign's extras replace an older one holding the same request code.
    private const val FLAGS = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}
