package com.jj.templateproject.core.data.back4app

import android.content.Context
import android.util.Log
import com.jj.templateproject.core.R
import com.parse.Parse
import com.parse.ParseObject

class InitializeBack4App(
    private val applicationContext: Context,
) {
    operator fun invoke() {
        Parse.initialize(
            Parse.Configuration.Builder(applicationContext)
                .applicationId(applicationContext.getString(R.string.back4app_app_id))
                .clientKey(applicationContext.getString(R.string.back4app_client_key)) // TODO move to BuildConfig files
                .server(applicationContext.getString(R.string.back4app_server_url))
                .build()
        )
        sendTestMessage()
    }

    private fun sendTestMessage() {
        val firstObject = ParseObject("FirstClass")
        firstObject.put("message", "Hey! First message from android. Parse is now connected.")
        firstObject.saveInBackground { e ->
            if (e != null) {
                // localizedMessage comes from Java and is genuinely nullable; a failure with no
                // message is exactly the case worth logging, so it gets a stand-in rather than
                // being swallowed by a null.
                Log.e(TAG, e.localizedMessage ?: "Parse save failed without a message")
            } else {
                Log.d(TAG, "Object saved.")
            }
        }
    }

    private companion object {
        const val TAG = "Back4App"
    }
}
