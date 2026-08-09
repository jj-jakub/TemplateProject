package com.jj.templateproject.domain.app

/** Hand-written stand-in for [AppInfoRepository], the same reasoning as
 *  [com.jj.templateproject.domain.google.FakeTemplateRepository]. */
class FakeAppInfoRepository(var installedFromValidSource: Boolean = true) : AppInfoRepository {
    override suspend fun installedFromValidSource(): Boolean = installedFromValidSource
}
