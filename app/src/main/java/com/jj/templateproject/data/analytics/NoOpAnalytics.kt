package com.jj.templateproject.data.analytics

import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter

/**
 * Safe defaults that drop everything on the floor. These are bound by default so the template
 * builds and runs without a `google-services.json`; swap in the Firebase implementations once
 * Firebase is configured (see `mainModule`).
 */
class NoOpAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, String>) = Unit
}

class NoOpCrashReporter : CrashReporter {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
}
