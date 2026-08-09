package com.jj.templateproject.data.ad

import com.jj.templateproject.BuildConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class AndroidAdUnitIdsTest {

    private val adUnitIds = AndroidAdUnitIds()

    @Test
    fun `mainBannerId reads the configured banner ad unit id`() {
        assertEquals(BuildConfig.adMainBannerViewAdUnitId, adUnitIds.mainBannerId)
    }

    @Test
    fun `interstitialId reads the configured interstitial ad unit id`() {
        assertEquals(BuildConfig.adInterstitialAdUnitId, adUnitIds.interstitialId)
    }

    @Test
    fun `banner and interstitial ad unit ids are distinct`() {
        assertNotEquals(adUnitIds.mainBannerId, adUnitIds.interstitialId)
    }
}
