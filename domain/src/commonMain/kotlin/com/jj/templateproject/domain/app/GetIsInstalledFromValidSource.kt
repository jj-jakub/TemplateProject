package com.jj.templateproject.domain.app

class GetIsInstalledFromValidSource(
    private val appInfoRepository: AppInfoRepository,
) {
    suspend operator fun invoke() = appInfoRepository.installedFromValidSource()
}
