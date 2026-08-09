package com.jj.templateproject.core.data.crosspromo

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.jj.templateproject.domain.crosspromo.UrlOpener

/**
 * `ACTION_VIEW` at a `https://play.google.com/…` URL, which the Play Store app itself intercepts
 * when installed (opening its own listing UI rather than a browser tab). `FLAG_ACTIVITY_NEW_TASK`
 * because this is started from application [Context], not an `Activity` — the same reason
 * `AndroidContentSharer`'s share intent needs it.
 */
class AndroidUrlOpener(
    private val context: Context,
) : UrlOpener {

    override fun open(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(intent)
        } catch (_: ActivityNotFoundException) {
            // No browser and no Play Store app to hand this to: nothing else to fall back to.
        }
    }
}
