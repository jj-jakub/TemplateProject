package com.jj.templateproject.presentation

import com.jj.templateproject.domain.app.AppVersionInfo
import platform.Foundation.NSBundle

/**
 * Reads the app's own bundle info, the iOS analogue of `:app`'s `AndroidAppVersionInfo` reading
 * `BuildConfig`. There is no iOS equivalent of `currentRevisionHash`/`ciBuildNumber` (those come
 * from this project's Android CI, see `.github/actions/setup-android-build`), so `revisionHash`
 * reads `CFBundleVersion` instead — the build-number slot iOS actually has.
 */
class IosAppVersionInfo : AppVersionInfo {
    override val revisionHash: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String ?: "unknown"

    override val buildNumber: String
        get() = revisionHash

    override val versionName: String
        get() = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "unknown"
}
