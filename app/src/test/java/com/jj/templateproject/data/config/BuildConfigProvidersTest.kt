package com.jj.templateproject.data.config

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.ad.GetInterstitialAdUnitId
import com.jj.templateproject.data.ad.GetMainAdUnitId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BuildConfigProvidersTest {

    @Test
    fun `GetMainAdUnitId returns the configured banner ad unit id`() {
        assertEquals(BuildConfig.adMainBannerViewAdUnitId, GetMainAdUnitId().invoke())
    }

    @Test
    fun `GetInterstitialAdUnitId returns the configured interstitial ad unit id`() {
        assertEquals(BuildConfig.adInterstitialAdUnitId, GetInterstitialAdUnitId().invoke())
    }

    @Test
    fun `version text combines revision, build number and version name`() {
        val text = VersionTextProvider().getAboutVersionText()

        assertTrue(text.contains(BuildConfig.currentRevisionHash))
        assertTrue(text.contains(BuildConfig.ciBuildNumber.toString()))
        assertTrue(text.contains(BuildConfig.VERSION_NAME))
    }
}
