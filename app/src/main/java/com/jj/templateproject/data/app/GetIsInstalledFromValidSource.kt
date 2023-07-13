package com.jj.templateproject.data.app

import com.jj.templateproject.domain.app.AppInfoRepository

class GetIsInstalledFromValidSource(
    private val appInfoRepository: AppInfoRepository,
) {
    suspend operator fun invoke() = appInfoRepository.installedFromValidSource()
}