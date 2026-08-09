package com.jj.templateproject.data.analytics

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * The factory's fallback is the property the template depends on: a clone with no Firebase
 * configuration has to run, and a build that is not one a real user could be running has to report
 * nothing. Both conditions are false under test, so this pins the fallback rather than the
 * Firebase path (which needs a real initialized SDK to exercise at all).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AnalyticsFactoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()

    @Test
    fun `analytics falls back to the no-op logger when Firebase is not initialized`() {
        assertTrue(AnalyticsFactory.analyticsLogger(context) is NoOpAnalyticsLogger)
    }

    @Test
    fun `crash reporting falls back to the no-op reporter when Firebase is not initialized`() {
        assertTrue(AnalyticsFactory.crashReporter(context) is NoOpCrashReporter)
    }
}
