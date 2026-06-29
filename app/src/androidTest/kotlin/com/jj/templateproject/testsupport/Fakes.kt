package com.jj.templateproject.testsupport

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.google.exception.NetworkError

/**
 * Shared, test-controllable state for [FakeTemplateRepository]. The repository is a Koin singleton,
 * so tests flip this flag (before navigating) to drive the success vs. error paths, and reset it in
 * `@After`.
 */
object FakeNetwork {
    @Volatile
    var failStatusCall: Boolean = false

    fun reset() {
        failStatusCall = false
    }
}

/** Deterministic, offline repository whose status call can be made to fail via [FakeNetwork]. */
class FakeTemplateRepository : TemplateRepository {
    override suspend fun getGoogleData(): BaseResult<String, NetworkError> = BaseResult.Success("200")

    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        if (FakeNetwork.failStatusCall) {
            BaseResult.Error(NetworkError.Connectivity)
        } else {
            BaseResult.Success(Unit)
        }
}

/** No-op ads so instrumented tests never surface an interstitial that would cover the UI. */
class NoOpAdManager : AdManager {
    override fun initAds() = Unit
    override fun incrementActionsForAd() = Unit
    override fun showInterstitialAd() = Unit
}
