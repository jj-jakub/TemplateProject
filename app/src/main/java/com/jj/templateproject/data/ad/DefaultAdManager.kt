package com.jj.templateproject.data.ad


import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.domain.ad.AdManager

private const val ACTIONS_FOR_AD = 5

class DefaultAdManager(
    private val context: Context,
    private val activityProvider: ActivityProvider,
    private val getInterstitialAdUnitId: GetInterstitialAdUnitId,
) : AdManager {

    private var mainInterstitialAd: InterstitialAd? = null
    private var backupInterstitialAd: InterstitialAd? = null
    private var actionsBeforeAdCounter: Int = 0

    override fun initAds() {
        MobileAds.initialize(context)

        loadMainInterstitialAd()
    }

    private fun loadMainInterstitialAd() {
        val adLoadCallback = object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                super.onAdLoaded(interstitialAd)
                mainInterstitialAd = interstitialAd
                interstitialAd.setOnPaidEventListener {
                    // TODO
                }
            }
        }

        InterstitialAd.load(
            /* context = */ context,
            /* adUnitId = */ getInterstitialAdUnitId(),
            /* adRequest = */ AdRequest.Builder().build(),
            /* loadCallback = */ adLoadCallback,
        )
    }

    override fun incrementActionsForAd() {
        actionsBeforeAdCounter++
        if (actionsBeforeAdCounter == ACTIONS_FOR_AD) {
            showInterstitialAd()
        }
    }

    override fun showInterstitialAd() {
        actionsBeforeAdCounter = 0
        val interstitialAd = mainInterstitialAd
        val activity = activityProvider.activeActivity
        if (activity == null) {
            // TODO
            return
        }
        if (interstitialAd != null) {
            interstitialAd.show(activity)
            // TODO
        } else {
            val backupAd = backupInterstitialAd
            // TODO
            if (backupAd != null) {
                backupAd.show(activity)
                // TODO
            } else {
                // TODO
            }
        }
    }
}