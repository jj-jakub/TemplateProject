package com.jj.templateproject.data.ad

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.domain.ad.AdUnitIds

class AndroidAdUnitIds : AdUnitIds {
    override val mainBannerId: String get() = BuildConfig.adMainBannerViewAdUnitId
    override val interstitialId: String get() = BuildConfig.adInterstitialAdUnitId
}
