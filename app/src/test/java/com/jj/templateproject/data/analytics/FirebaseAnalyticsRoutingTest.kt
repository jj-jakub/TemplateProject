package com.jj.templateproject.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Verifies the Firebase adapters actually route to the SDK (the NoOp impls have no behavior to
 * assert). Runs under Robolectric because the analytics adapter builds an `android.os.Bundle`.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class FirebaseAnalyticsRoutingTest {

    @Test
    fun `analytics logger forwards the event name to FirebaseAnalytics`() {
        val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)

        FirebaseAnalyticsLogger(firebaseAnalytics).logEvent("sign_up", mapOf("method" to "email"))

        verify { firebaseAnalytics.logEvent(eq("sign_up"), any()) }
    }

    @Test
    fun `crash reporter forwards logs and exceptions to Crashlytics`() {
        val crashlytics = mockk<FirebaseCrashlytics>(relaxed = true)
        val reporter = FirebaseCrashReporter(crashlytics)
        val error = IllegalStateException("boom")

        reporter.log("breadcrumb")
        reporter.recordException(error)

        verify { crashlytics.log("breadcrumb") }
        verify { crashlytics.recordException(error) }
    }
}
