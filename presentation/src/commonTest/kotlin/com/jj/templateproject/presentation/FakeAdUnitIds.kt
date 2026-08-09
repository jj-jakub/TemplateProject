package com.jj.templateproject.presentation

import com.jj.templateproject.domain.ad.AdUnitIds

class FakeAdUnitIds(
    override val mainBannerId: String = "main-ad-unit",
    override val interstitialId: String = "interstitial-ad-unit",
) : AdUnitIds
