package com.jj.templateproject.domain.analytics

import kotlin.test.assertEquals
import kotlin.test.Test

/**
 * The numeric overload carries a default implementation so that an [AnalyticsLogger] written before
 * it existed stays correct. These pin that fallback, since it is the behavior every implementation
 * inherits unless it deliberately overrides it.
 */
class AnalyticsLoggerMetricsTest {

    private class RecordingLogger : AnalyticsLogger {
        var lastName: String? = null
        var lastParams: Map<String, String> = emptyMap()

        override fun logEvent(name: String, params: Map<String, String>) {
            lastName = name
            lastParams = params
        }
    }

    @Test
    fun `metrics fall back to string parameters for a logger with no numeric path`() {
        val logger = RecordingLogger()

        logger.logEvent("session_end", mapOf("screen" to "main"), mapOf("duration_ms" to 4200L))

        assertEquals("session_end", logger.lastName)
        assertEquals(mapOf("screen" to "main", "duration_ms" to "4200"), logger.lastParams)
    }

    @Test
    fun `an event with only metrics still reaches the string channel`() {
        val logger = RecordingLogger()

        logger.logEvent("startup", emptyMap(), mapOf("cold_start_ms" to 812L))

        assertEquals(mapOf("cold_start_ms" to "812"), logger.lastParams)
    }

    @Test
    fun `a metric overrides a string parameter of the same name rather than being dropped`() {
        val logger = RecordingLogger()

        logger.logEvent("count", mapOf("value" to "stale"), mapOf("value" to 7L))

        assertEquals(mapOf("value" to "7"), logger.lastParams)
    }
}
