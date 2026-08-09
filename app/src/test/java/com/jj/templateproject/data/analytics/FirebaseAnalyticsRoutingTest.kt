package com.jj.templateproject.data.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
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
    fun `analytics logger sends metrics as numbers, not as strings`() {
        // A metric that arrives as text can be counted but never summed or averaged, which is the
        // whole reason the numeric overload exists.
        val firebaseAnalytics = mockk<FirebaseAnalytics>(relaxed = true)
        val bundle = slot<Bundle>()

        FirebaseAnalyticsLogger(firebaseAnalytics)
            .logEvent("session_end", mapOf("screen" to "main"), mapOf("duration_ms" to 4200L))

        verify { firebaseAnalytics.logEvent(eq("session_end"), capture(bundle)) }
        assertEquals("main", bundle.captured.getString("screen"))
        assertEquals(4200L, bundle.captured.getLong("duration_ms"))
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
