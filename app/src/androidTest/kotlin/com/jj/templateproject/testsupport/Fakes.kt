package com.jj.templateproject.testsupport

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.google.exception.NetworkError

/** Deterministic, offline repository so the Settings flow renders fixed content under test. */
class FakeTemplateRepository : TemplateRepository {
    override suspend fun getGoogleData(): BaseResult<String, NetworkError> = BaseResult.Success("200")
    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> = BaseResult.Success(Unit)
}

/** No-op ads so instrumented tests never surface an interstitial that would cover the UI. */
class NoOpAdManager : AdManager {
    override fun initAds() = Unit
    override fun incrementActionsForAd() = Unit
    override fun showInterstitialAd() = Unit
}
