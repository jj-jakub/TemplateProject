package com.jj.templateproject.domain.analytics

/**
 * Records analytics events. Kept as a domain abstraction so the rest of the app never depends on
 * a concrete analytics SDK and can be driven by a no-op (debug/tests) or a Firebase-backed impl.
 */
interface AnalyticsLogger {
    /** Logs a named event with optional string parameters. */
    fun logEvent(name: String, params: Map<String, String> = emptyMap())

    /**
     * Logs an event carrying numbers as well as strings.
     *
     * Worth a channel of its own because a number sent as text can only ever be counted, never
     * averaged or summed: a duration has to arrive as a number for the dashboard to be able to say
     * how long a typical one was.
     *
     * The default folds the metrics into strings, so an implementation without a numeric path stays
     * correct without knowing this overload exists.
     */
    fun logEvent(name: String, params: Map<String, String>, metrics: Map<String, Long>) =
        logEvent(name, params + metrics.mapValues { (_, value) -> value.toString() })
}
