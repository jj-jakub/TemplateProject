package com.jj.templateproject.domain.push

/**
 * Where a push notification's tap, or one of its action buttons, sends the user.
 *
 * Each destination has a short token form ([PushDestinations.token]) because the same value has to
 * survive two hops it cannot travel as an object: the FCM payload (a `Map<String, String>` composed
 * by whoever sends the campaign) and an Android `Intent` extra (the notification is built in one
 * process state and read back in another).
 *
 * Add app-specific destinations here. Keep the set small and closed: a destination is a promise that
 * some screen will handle it, and an open-ended one (an arbitrary URL, a raw route string) turns a
 * remote string into navigation nobody reviewed.
 */
sealed interface PushDestination {

    /** The app's main screen. The default when a campaign names no destination. */
    data object Home : PushDestination

    /** The settings screen, for a campaign about something the user can turn on or off. */
    data object Settings : PushDestination

    /**
     * Another app's Play Store page. Leaves this app entirely, so it is opened straight from the
     * notification rather than routed through our UI.
     */
    data class PlayStoreApp(val packageName: String) : PushDestination
}

object PushDestinations {

    fun token(destination: PushDestination): String = when (destination) {
        PushDestination.Home -> HOME
        PushDestination.Settings -> SETTINGS
        is PushDestination.PlayStoreApp -> "$PLAY_PREFIX${destination.packageName}"
    }

    /**
     * Reads a token back. Deliberately total: an unknown or malformed token yields null rather than
     * throwing, because the input is a remote string we do not control and the caller is usually a
     * background service, where an exception costs the whole message.
     */
    fun parse(token: String?): PushDestination? = when {
        token == null -> null
        token.equals(HOME, ignoreCase = true) -> PushDestination.Home
        token.equals(SETTINGS, ignoreCase = true) -> PushDestination.Settings
        token.startsWith(PLAY_PREFIX, ignoreCase = true) ->
            token.substring(PLAY_PREFIX.length).takeIf(::isPackageName)?.let(PushDestination::PlayStoreApp)
        else -> null
    }

    /**
     * Whether a string is shaped like an Android package name. A campaign's target package is remote
     * input that ends up inside a URL, so a token that is not a package is rejected outright instead
     * of being handed to the store as-is.
     */
    private fun isPackageName(candidate: String): Boolean {
        val segments = candidate.split('.')
        return segments.size >= 2 && segments.all { segment ->
            segment.isNotEmpty() && !segment.first().isAsciiDigit() && segment.all { it.isPackageChar() }
        }
    }

    const val HOME = "home"
    const val SETTINGS = "settings"
    const val PLAY_PREFIX = "play:"
}

private fun Char.isAsciiDigit() = this in '0'..'9'

// ASCII only: a package name is never localised, and anything wider would widen what can reach the
// store URL.
private fun Char.isPackageChar() = this in 'a'..'z' || this in 'A'..'Z' || isAsciiDigit() || this == '_'
