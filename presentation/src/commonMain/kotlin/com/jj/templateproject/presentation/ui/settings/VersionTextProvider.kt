package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.domain.app.AppVersionInfo

class VersionTextProvider(private val appVersionInfo: AppVersionInfo) {

    fun getAboutVersionText(): String = "Revision: ${appVersionInfo.revisionHash}, " +
            "Build number: ${appVersionInfo.buildNumber}, Version: ${appVersionInfo.versionName}"
}
