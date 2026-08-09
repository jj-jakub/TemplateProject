package com.jj.templateproject.presentation

import com.jj.templateproject.domain.app.AppVersionInfo

class FakeAppVersionInfo(
    override val revisionHash: String = "abcd1234",
    override val buildNumber: String = "17",
    override val versionName: String = "1.0",
) : AppVersionInfo
