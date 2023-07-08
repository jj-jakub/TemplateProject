package com.jj.templateproject.data.ad

import com.jj.templateproject.BuildConfig

class GetInterstitialAdUnitId {
    operator fun invoke(): String = BuildConfig.adInterstitialAdUnitId
}