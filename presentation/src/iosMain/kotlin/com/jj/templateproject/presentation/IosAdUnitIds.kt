package com.jj.templateproject.presentation

import com.jj.templateproject.domain.ad.AdUnitIds

/**
 * Google's published sample ad unit ids (always resolve test ads, safe to ship in a build that is
 * never in front of a real user). There is no real value to read yet: `ComposeAdView`'s iOS actual
 * renders nothing, since the AdMob iOS SDK has no Kotlin/Native binding wired up here. This exists
 * purely so the Koin graph has something to bind — `GetMainAdUnitId`/`GetInterstitialAdUnitId`
 * (and therefore `MainRootViewModel`) would otherwise fail to resolve on iOS at all. Swap for real
 * ad unit ids alongside wiring up a real iOS `ComposeAdView` actual.
 */
class IosAdUnitIds : AdUnitIds {
    override val mainBannerId: String = "ca-app-pub-3940256099942544/2934735716"
    override val interstitialId: String = "ca-app-pub-3940256099942544/4411468910"
}
