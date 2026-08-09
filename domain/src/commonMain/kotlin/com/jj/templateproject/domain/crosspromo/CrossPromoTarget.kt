package com.jj.templateproject.domain.crosspromo

/**
 * Resolves a raw, remotely-published target string into the URL that is actually safe to open.
 *
 * Three shapes are accepted: a full `https://play.google.com/…` URL, a full
 * `https://apps.apple.com/…` URL, or a bare Android package name (`com.example.other`), turned
 * into that app's Play Store listing — iOS has no package-name equivalent (App Store listings are
 * keyed by a numeric id, not a reverse-DNS name), so promoting an iOS app means publishing its full
 * `apps.apple.com` URL directly rather than a bare identifier. Anything else — a typo in the
 * console, or a compromised remote config value — resolves to `null` rather than being opened
 * as-is, which is what keeps a misconfigured flag from becoming an arbitrary open-URL vector.
 */
object CrossPromoTarget {

    private const val PLAY_STORE_URL_PREFIX = "https://play.google.com/"
    private const val APP_STORE_URL_PREFIX = "https://apps.apple.com/"
    private val PACKAGE_NAME_REGEX = Regex("^[a-zA-Z][a-zA-Z0-9_]*(\\.[a-zA-Z][a-zA-Z0-9_]*)+$")

    fun resolveUrl(target: String): String? = when {
        target.startsWith(PLAY_STORE_URL_PREFIX) -> target
        target.startsWith(APP_STORE_URL_PREFIX) -> target
        PACKAGE_NAME_REGEX.matches(target) -> "${PLAY_STORE_URL_PREFIX}store/apps/details?id=$target"
        else -> null
    }
}
