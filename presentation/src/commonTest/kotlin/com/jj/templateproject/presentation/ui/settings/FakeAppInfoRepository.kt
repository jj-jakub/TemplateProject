package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.domain.app.AppInfoRepository

class FakeAppInfoRepository(var installedFromValidSource: Boolean = true) : AppInfoRepository {
    override suspend fun installedFromValidSource(): Boolean = installedFromValidSource
}
