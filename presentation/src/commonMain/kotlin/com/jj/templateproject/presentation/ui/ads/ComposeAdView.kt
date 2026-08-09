package com.jj.templateproject.presentation.ui.ads

import androidx.compose.runtime.Composable

/**
 * A banner ad. AdMob has no iOS Kotlin/Native binding wired up yet (it would need the Google
 * Mobile Ads SDK's iOS framework cinteropped, a separate piece of work), so this stays an
 * `expect`/`actual` seam rather than a shared implementation, the same way push notifications do
 * in `:core`. The iOS actual renders nothing rather than a placeholder, so a screen that includes
 * this composable degrades to "no banner" instead of a broken layout.
 */
@Composable
expect fun ComposeAdView(
    adUnitId: String,
    onAdClicked: () -> Unit,
)
