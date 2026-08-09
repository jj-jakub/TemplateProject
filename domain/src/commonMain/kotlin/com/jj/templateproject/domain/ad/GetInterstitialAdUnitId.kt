package com.jj.templateproject.domain.ad

class GetInterstitialAdUnitId(private val adUnitIds: AdUnitIds) {
    operator fun invoke(): String = adUnitIds.interstitialId
}
