package com.jj.templateproject.data.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter

/**
 * Firebase-backed implementations. Bound by [AnalyticsFactory] only for a build that both reports
 * (see `BuildProfile.isReportingBuild`) and has Firebase initialized; every other build gets the
 * no-op pair, so the app never crashes for lack of a `google-services.json`.
 *
 * Uses the plain (non-KTX) Java API surface deliberately: it has been stable for years, unlike the
 * Kotlin extension properties, which have moved packages across Firebase BoM versions.
 */
class FirebaseAnalyticsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, String>) =
        logEvent(name, params, emptyMap())

    override fun logEvent(name: String, params: Map<String, String>, metrics: Map<String, Long>) {
        firebaseAnalytics.logEvent(
            name,
            Bundle().apply {
                params.forEach { (key, value) -> putString(key, value) }
                // putLong, not putString: a metric registered in the Firebase console can be summed
                // and averaged, but only if it arrives as a number.
                metrics.forEach { (key, value) -> putLong(key, value) }
            },
        )
    }
}

class FirebaseCrashReporter(
    private val crashlytics: FirebaseCrashlytics,
) : CrashReporter {
    override fun log(message: String) = crashlytics.log(message)
    override fun recordException(throwable: Throwable) = crashlytics.recordException(throwable)
}
