package com.jj.templateproject.domain.ad

interface AdManager {
    fun initAds()
    fun incrementActionsForAd()
    fun showInterstitialAd()
}

/** Does nothing, for a build or a test with no ad SDK wired up (today: iOS). */
object NoOpAdManager : AdManager {
    override fun initAds() = Unit
    override fun incrementActionsForAd() = Unit
    override fun showInterstitialAd() = Unit
}
