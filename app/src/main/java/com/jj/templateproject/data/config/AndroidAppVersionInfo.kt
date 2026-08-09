package com.jj.templateproject.data.config

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.domain.app.AppVersionInfo

class AndroidAppVersionInfo : AppVersionInfo {
    override val revisionHash: String get() = BuildConfig.currentRevisionHash
    override val buildNumber: String get() = BuildConfig.ciBuildNumber.toString()
    override val versionName: String get() = BuildConfig.VERSION_NAME
}
