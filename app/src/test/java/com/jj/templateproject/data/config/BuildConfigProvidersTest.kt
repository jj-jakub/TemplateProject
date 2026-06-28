package com.jj.templateproject.data.config

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.ad.GetInterstitialAdUnitId
import com.jj.templateproject.data.ad.GetMainAdUnitId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
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
    fun `banner and interstitial ad unit ids are distinct`() {
        assertNotEquals(GetMainAdUnitId().invoke(), GetInterstitialAdUnitId().invoke())
    }

    @Test
    fun `version text uses the exact revision, build number and version name format`() {
        val expected = "Revision: ${BuildConfig.currentRevisionHash}, " +
            "Build number: ${BuildConfig.ciBuildNumber}, Version: ${BuildConfig.VERSION_NAME}"

        assertEquals(expected, VersionTextProvider().getAboutVersionText())
    }
}
