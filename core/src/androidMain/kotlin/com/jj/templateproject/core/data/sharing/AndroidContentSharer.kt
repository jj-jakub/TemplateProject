package com.jj.templateproject.core.data.sharing

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import com.jj.templateproject.domain.sharing.ContentSharer

class AndroidContentSharer(
    private val context: Context,
) : ContentSharer {

    override fun shareText(text: String, subject: String?) {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = MIME_TEXT
            putExtra(Intent.EXTRA_TEXT, text)
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
        }
        // NEW_TASK because this is held as a singleton over the application context, which has no
        // task of its own to start the chooser in.
        val chooser = Intent.createChooser(send, null).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(chooser)
        } catch (_: ActivityNotFoundException) {
            // A device with nothing at all able to receive text. Nothing to fall back to, and
            // certainly not worth taking the caller down for.
        }
    }

    private companion object {
        const val MIME_TEXT = "text/plain"
    }
}
