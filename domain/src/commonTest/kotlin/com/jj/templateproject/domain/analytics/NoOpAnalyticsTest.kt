package com.jj.templateproject.domain.analytics

import kotlin.test.Test

class NoOpAnalyticsTest {

    @Test
    fun noOpAnalyticsLoggerSwallowsEvents() {
        val logger = NoOpAnalyticsLogger()

        logger.logEvent("event")
        logger.logEvent("event_with_params", mapOf("key" to "value"))
    }

    @Test
    fun noOpCrashReporterSwallowsLogsAndExceptions() {
        val reporter = NoOpCrashReporter()

        reporter.log("breadcrumb")
        reporter.recordException(IllegalStateException("boom"))
    }
}
