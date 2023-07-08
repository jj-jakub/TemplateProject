package com.jj.templateproject.domain.ad

interface AdManager {
    fun initAds()
    fun incrementActionsForAd()
    fun showInterstitialAd()
}