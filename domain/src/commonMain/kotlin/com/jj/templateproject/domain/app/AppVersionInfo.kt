package com.jj.templateproject.domain.app

/**
 * The running build's own version identity. The same `BuildConfig`-cannot-cross-modules reasoning
 * as [com.jj.templateproject.domain.ad.AdUnitIds] applies here: only the application module that
 * generated these values can supply a real implementation.
 */
interface AppVersionInfo {
    val revisionHash: String
    val buildNumber: String
    val versionName: String
}
