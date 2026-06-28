package com.jj.templateproject.data.ad

import android.content.Context
import com.jj.templateproject.di.ActivityProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefaultAdManagerTest {

    private val context = mockk<Context>(relaxed = true)
    private val activityProvider = mockk<ActivityProvider>()
    private val getInterstitialAdUnitId = mockk<GetInterstitialAdUnitId>(relaxed = true)

    private val adManager = DefaultAdManager(
        context = context,
        activityProvider = activityProvider,
        getInterstitialAdUnitId = getInterstitialAdUnitId,
    )

    @BeforeEach
    fun setUp() {
        // No active activity -> showInterstitialAd takes the early-return branch (no GMS calls).
        every { activityProvider.activeActivity } returns null
    }

    @Test
    fun `fewer than five actions do not attempt to show an ad`() {
        repeat(4) { adManager.incrementActionsForAd() }

        verify(exactly = 0) { activityProvider.activeActivity }
    }

    @Test
    fun `the fifth action attempts to show an interstitial ad`() {
        repeat(5) { adManager.incrementActionsForAd() }

        verify(exactly = 1) { activityProvider.activeActivity }
    }

    @Test
    fun `the action counter resets so every fifth action shows an ad`() {
        repeat(10) { adManager.incrementActionsForAd() }

        verify(exactly = 2) { activityProvider.activeActivity }
    }

    @Test
    fun `showInterstitialAd is a safe no-op when there is no active activity`() {
        adManager.showInterstitialAd()

        verify(exactly = 1) { activityProvider.activeActivity }
    }
}
