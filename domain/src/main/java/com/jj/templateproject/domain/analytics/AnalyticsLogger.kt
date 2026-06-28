package com.jj.templateproject.domain.analytics

/**
 * Records analytics events. Kept as a domain abstraction so the rest of the app never depends on
 * a concrete analytics SDK and can be driven by a no-op (debug/tests) or a Firebase-backed impl.
 */
interface AnalyticsLogger {
    fun logEvent(name: String, params: Map<String, String> = emptyMap())
}
