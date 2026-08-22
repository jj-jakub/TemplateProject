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
                // Plain for-loops, not Map.forEach: AGP 9.0.0's bundled lint misresolves the Kotlin
                // stdlib's inline Map<K, V>.forEach (destructured lambda) as the java.util.Map#forEach
                // default method added in API 24, a false NewApi error against this module's minSdk 23
                // (confirmed by inspecting the compiled bytecode, which never calls that method either
                // way). A for-loop sidesteps the misresolution; revisit once AGP is back on 9.3.1.
                for ((key, value) in params) putString(key, value)
                // putLong, not putString: a metric registered in the Firebase console can be summed
                // and averaged, but only if it arrives as a number.
                for ((key, value) in metrics) putLong(key, value)
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
