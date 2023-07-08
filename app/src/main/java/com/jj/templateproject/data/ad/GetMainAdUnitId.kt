package com.jj.templateproject.data.ad

import com.jj.templateproject.BuildConfig

class GetMainAdUnitId {
    operator fun invoke(): String = BuildConfig.adMainBannerViewAdUnitId
}