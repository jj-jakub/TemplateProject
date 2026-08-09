package com.jj.templateproject.domain.push

/**
 * Distinct request codes for the pending intents one notification holds.
 *
 * Android matches pending intents on everything *except* their extras, so a tap and three action
 * buttons that share a request code are one pending intent wearing four labels: every button opens
 * whichever destination was registered first. The slot arithmetic keeps two notifications from
 * colliding as well.
 */
object PushRequestCodes {

    /** The notification body's own tap; action buttons take the slots after it. */
    const val TAP = 0

    /** One tap plus [PushPayload.MAX_ACTIONS] buttons. */
    const val SLOTS_PER_NOTIFICATION = PushPayload.MAX_ACTIONS + 1

    fun of(notificationId: Int, slot: Int): Int = notificationId * SLOTS_PER_NOTIFICATION + slot

    /** The slot an action button occupies, given its zero-based position in [PushMessage.actions]. */
    fun actionSlot(actionIndex: Int): Int = actionIndex + 1
}
