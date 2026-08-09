package com.jj.templateproject.domain.analytics

/**
 * Reports non-fatal errors and breadcrumb logs to a crash-reporting backend, behind a domain
 * abstraction so callers don't depend on a concrete SDK (Crashlytics, Sentry, …).
 */
interface CrashReporter {
    fun log(message: String)
    fun recordException(throwable: Throwable)
}
