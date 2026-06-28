package com.jj.templateproject.data.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter

/**
 * Firebase-backed implementations, ready to enable once Firebase is configured (a real
 * `google-services.json` + the google-services Gradle plugin). They are intentionally NOT bound
 * by default — see the opt-in block in `mainModule` — so the template runs without Firebase.
 */
class FirebaseAnalyticsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, String>) {
        firebaseAnalytics.logEvent(
            name,
            Bundle().apply { params.forEach { (key, value) -> putString(key, value) } },
        )
    }
}

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {
    override fun log(message: String) = crashlytics.log(message)
    override fun recordException(throwable: Throwable) = crashlytics.recordException(throwable)
}
