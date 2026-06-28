package com.jj.templateproject.data.analytics

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test

class NoOpAnalyticsTest {

    @Test
    fun `no-op analytics logger swallows events`() {
        val logger = NoOpAnalyticsLogger()

        assertDoesNotThrow {
            logger.logEvent("event")
            logger.logEvent("event_with_params", mapOf("key" to "value"))
        }
    }

    @Test
    fun `no-op crash reporter swallows logs and exceptions`() {
        val reporter = NoOpCrashReporter()

        assertDoesNotThrow {
            reporter.log("breadcrumb")
            reporter.recordException(IllegalStateException("boom"))
        }
    }
}
