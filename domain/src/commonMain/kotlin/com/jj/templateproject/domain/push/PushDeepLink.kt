package com.jj.templateproject.domain.push

/**
 * The `templateproject://` link form of a [PushDestination].
 *
 * Push carries its destination as a payload field, but an in-app message composed in a console does
 * not have that channel: each button there carries its own action URL, and the message's custom data
 * is a single set for the whole message, so it could name one destination at most. A link is the
 * only per-button channel such a campaign has.
 *
 * The scheme is browsable, so anything can send one — a web page, another app, a `adb shell am`
 * command. Every link is therefore parsed as untrusted input, exactly like a payload field, and
 * anything unrecognised routes nowhere instead of failing.
 *
 * | Destination            | Link                                    |
 * | ---------------------- | --------------------------------------- |
 * | Home                   | `templateproject://home`                 |
 * | Settings               | `templateproject://settings`             |
 * | Another app's listing  | `templateproject://play/com.example.app` |
 */
object PushDeepLink {

    const val SCHEME = "templateproject"

    fun format(destination: PushDestination): String = when (destination) {
        PushDestination.Home -> "$SCHEME://${PushDestinations.HOME}"
        PushDestination.Settings -> "$SCHEME://${PushDestinations.SETTINGS}"
        is PushDestination.PlayStoreApp -> "$SCHEME://$PLAY_HOST/${destination.packageName}"
    }

    /**
     * Reads a link back into a destination, or null for anything this app does not recognise.
     *
     * Parsed by hand rather than through a URI type so the whole thing stays pure and testable, and
     * so the accepted shapes are visible here rather than spread across a platform parser's
     * behaviour.
     */
    fun parse(link: String?): PushDestination? {
        val withoutScheme = link
            ?.trim()
            ?.takeIf { it.startsWith("$SCHEME://", ignoreCase = true) }
            ?.substringAfter("://")
            ?: return null
        // A trailing slash is easy to add by hand and means nothing here.
        val path = withoutScheme.trimEnd('/')
        val host = path.substringBefore('/')
        return when {
            host.equals(PLAY_HOST, ignoreCase = true) ->
                PushDestinations.parse(PushDestinations.PLAY_PREFIX + path.substringAfter('/', ""))
            else -> PushDestinations.parse(host)
        }
    }

    private const val PLAY_HOST = "play"
}
