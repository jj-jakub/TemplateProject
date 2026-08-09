package com.jj.templateproject.data.analytics

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.jj.templateproject.data.config.BuildProfile
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter

/**
 * Picks the reporters this build should use, so the choice is made once instead of being a line in
 * the DI module that someone has to remember to edit.
 *
 * Reporting needs two things to be true, and either one missing falls back to the no-op pair rather
 * than failing: the build has to be one a real user could be running ([BuildProfile.isReportingBuild]),
 * and Firebase has to have initialized, which it only does once a real `google-services.json` is
 * present. That is what lets a fresh clone run with no Firebase configuration at all.
 */
object AnalyticsFactory {

    fun analyticsLogger(context: Context): AnalyticsLogger =
        if (reports(context)) {
            FirebaseAnalyticsLogger(FirebaseAnalytics.getInstance(context))
        } else {
            NoOpAnalyticsLogger()
        }

    fun crashReporter(context: Context): CrashReporter =
        if (reports(context)) {
            FirebaseCrashReporter(FirebaseCrashlytics.getInstance())
        } else {
            NoOpCrashReporter()
        }

    // Asking FirebaseAnalytics/FirebaseCrashlytics for an instance before the default FirebaseApp
    // exists throws, so this check is not optional.
    private fun reports(context: Context): Boolean =
        BuildProfile.isReportingBuild && FirebaseApp.getApps(context).isNotEmpty()
}
