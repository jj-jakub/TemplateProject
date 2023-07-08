package com.jj.templateproject.presentation.ui.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun ComposeAdView(
    adUnitId: String,
    onAdClicked: () -> Unit,
) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.LARGE_BANNER)
                setAdUnitId(adUnitId)
                loadAd(AdRequest.Builder().build())
                adListener = object : AdListener() {
                    override fun onAdClicked() {
                        super.onAdClicked()
                        onAdClicked()
                    }
                }
            }
        }
    )
}