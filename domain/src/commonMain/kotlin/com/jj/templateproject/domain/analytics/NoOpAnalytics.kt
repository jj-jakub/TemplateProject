package com.jj.templateproject.domain.analytics

/**
 * Safe defaults that drop everything on the floor. Bound wherever a platform has no real analytics
 * SDK wired up yet: on Android, `:app`'s `AnalyticsFactory` falls back to these when the build
 * isn't reporting or `google-services.json` is absent; on iOS, `IosKoin`'s `iosAppModule` binds
 * them outright, since no iOS analytics implementation exists yet.
 */
class NoOpAnalyticsLogger : AnalyticsLogger {
    override fun logEvent(name: String, params: Map<String, String>) = Unit
}

class NoOpCrashReporter : CrashReporter {
    override fun log(message: String) = Unit
    override fun recordException(throwable: Throwable) = Unit
}
