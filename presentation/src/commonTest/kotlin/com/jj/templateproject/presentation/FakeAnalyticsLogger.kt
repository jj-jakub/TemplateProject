package com.jj.templateproject.presentation

import com.jj.templateproject.domain.analytics.AnalyticsLogger

class FakeAnalyticsLogger : AnalyticsLogger {

    val loggedEvents = mutableListOf<Pair<String, Map<String, String>>>()

    override fun logEvent(name: String, params: Map<String, String>) {
        loggedEvents += name to params
    }
}
