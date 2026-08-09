package com.jj.templateproject.domain.ad

import kotlin.test.Test
import kotlin.test.assertEquals

private class FakeAdUnitIds(
    override val mainBannerId: String = "main-id",
    override val interstitialId: String = "interstitial-id",
) : AdUnitIds

class AdUnitIdUseCasesTest {

    @Test
    fun `GetMainAdUnitId returns the configured banner ad unit id`() {
        val adUnitIds = FakeAdUnitIds(mainBannerId = "banner-42")

        assertEquals("banner-42", GetMainAdUnitId(adUnitIds)())
    }

    @Test
    fun `GetInterstitialAdUnitId returns the configured interstitial ad unit id`() {
        val adUnitIds = FakeAdUnitIds(interstitialId = "interstitial-7")

        assertEquals("interstitial-7", GetInterstitialAdUnitId(adUnitIds)())
    }
}
