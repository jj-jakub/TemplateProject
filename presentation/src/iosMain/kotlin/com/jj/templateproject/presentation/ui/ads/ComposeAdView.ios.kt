package com.jj.templateproject.presentation.ui.ads

import androidx.compose.runtime.Composable

/** No-op: no iOS AdMob binding exists yet. See the file doc comment on the `expect` declaration. */
@Composable
actual fun ComposeAdView(
    adUnitId: String,
    onAdClicked: () -> Unit,
) = Unit
