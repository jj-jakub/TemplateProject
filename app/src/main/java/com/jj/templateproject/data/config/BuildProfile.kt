package com.jj.templateproject.data.config

import com.jj.templateproject.BuildConfig

/**
 * What kind of build this is, in the one place that answers the question.
 *
 * The distinction that matters is [isReportingBuild]: whether a real user could be running this
 * binary. Development sessions and internal side-loads share a backend project with the builds that
 * ship, so without a gate their events land in the same dashboards and their crashes in the same
 * reports, which quietly makes both useless. Analytics and crash reporting key off this rather than
 * off `BuildConfig.DEBUG` alone, so that a future non-distributed variant (a QA edition, an internal
 * flavor) only has to be excluded here.
 *
 * Held on two layers: this one picks the no-op reporters (see `AnalyticsFactory`), and
 * `src/debug/AndroidManifest.xml` turns the SDKs' own collection off, so a reporter constructed by
 * some other path still sends nothing.
 */
object BuildProfile {

    /** True for a debug binary. Gates developer-only affordances. */
    val isDebugBuild: Boolean get() = BuildConfig.DEBUG

    /** True only for a build a real user could be running, which today means any release build. */
    val isReportingBuild: Boolean get() = !BuildConfig.DEBUG
}
