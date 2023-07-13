package com.jj.templateproject.domain.app

interface AppInfoRepository {
    suspend fun installedFromValidSource(): Boolean
}