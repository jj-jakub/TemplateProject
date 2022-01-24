package com.jj.templateproject.data.text

import com.jj.templateproject.BuildConfig

class VersionTextProvider {

    fun getAboutVersionText(): String = "Revision: ${BuildConfig.currentRevisionHash}, " +
            "Build number: ${BuildConfig.ciBuildNumber}, Version: ${BuildConfig.VERSION_NAME}"
}