package com.jj.templateproject.domain.ad

/**
 * The ad unit ids this build should request. A seam rather than a raw string constant because the
 * real values come from `BuildConfig`, which is generated per Android application module and so
 * cannot be read from `:domain`, `:networking`, `:core` or `:presentation` — only `:app` (and,
 * later, an iOS app target) can supply a real implementation, bound through Koin.
 */
interface AdUnitIds {
    val mainBannerId: String
    val interstitialId: String
}
