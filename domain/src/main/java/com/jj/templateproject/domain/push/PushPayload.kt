package com.jj.templateproject.domain.push

/** One notification action button: what it says, and where it goes. */
data class PushAction(val label: String, val destination: PushDestination)

/** A campaign, as the app will display it. */
data class PushMessage(
    val title: String,
    val body: String,
    /** Where tapping the notification body goes. */
    val tap: PushDestination,
    /** Up to [PushPayload.MAX_ACTIONS] buttons, in payload order. */
    val actions: List<PushAction>,
    /** Optional campaign name, carried so an event logged for this notification can name it. */
    val campaign: String?,
    /**
     * Which notification this message occupies. Two campaigns sent with different slots sit side by
     * side in the shade; re-sending the same slot replaces the earlier one, which is what a
     * correction or a reminder of the same announcement wants.
     */
    val notificationSlot: Int,
)

/**
 * Turns an FCM **data** message into a [PushMessage].
 *
 * Data-only, deliberately: a message carrying a `notification` block is drawn by the Firebase SDK
 * itself whenever the app is backgrounded, which never calls our code and so can never carry action
 * buttons or our own routing. Sends therefore have to go through the HTTP v1 API rather than the
 * console's Notifications composer, which always attaches one. See PUSH.md.
 *
 * Every field arrives as a remote string, so parsing is total: anything missing, unknown or
 * malformed is dropped rather than throwing inside a background service. A message without both a
 * title and a body is not displayable at all and yields null.
 */
object PushPayload {

    /** Android renders at most three action buttons; further ones in a payload are ignored. */
    const val MAX_ACTIONS = 3

    /** Highest [PushMessage.notificationSlot] a campaign may claim. */
    const val MAX_SLOT = 99

    fun parse(data: Map<String, String>): PushMessage? {
        val title = data[KEY_TITLE]?.trim().orEmpty()
        val body = data[KEY_BODY]?.trim().orEmpty()
        if (title.isEmpty() || body.isEmpty()) return null
        return PushMessage(
            title = title,
            body = body,
            tap = PushDestinations.parse(data[KEY_TAP]) ?: PushDestination.Home,
            actions = (1..MAX_ACTIONS).mapNotNull { index -> action(data, index) },
            campaign = data[KEY_CAMPAIGN]?.trim()?.takeIf(String::isNotEmpty),
            // Bounded, not just parsed: the slot is multiplied out into pending-intent request codes,
            // so a remote value is never allowed to be arbitrarily large or negative.
            notificationSlot = data[KEY_SLOT]?.trim()?.toIntOrNull()?.coerceIn(0, MAX_SLOT) ?: 0,
        )
    }

    // An action needs both halves to mean anything: a button with no destination does nothing, and a
    // destination with no label has nothing to press.
    private fun action(data: Map<String, String>, index: Int): PushAction? {
        val label = data["$ACTION_PREFIX$index$LABEL_SUFFIX"]?.trim()?.takeIf(String::isNotEmpty) ?: return null
        val destination = PushDestinations.parse(data["$ACTION_PREFIX$index$TARGET_SUFFIX"]) ?: return null
        return PushAction(label, destination)
    }

    const val KEY_TITLE = "title"
    const val KEY_BODY = "body"
    const val KEY_TAP = "tap"
    const val KEY_CAMPAIGN = "campaign"
    const val KEY_SLOT = "slot"
    private const val ACTION_PREFIX = "action"
    private const val LABEL_SUFFIX = "_label"
    private const val TARGET_SUFFIX = "_target"
}
