package com.jj.templateproject.domain.sharing

/**
 * Hands text to whatever the user picks to send it with.
 *
 * A seam because sharing is the platform's, not ours: on Android it is a chooser over ACTION_SEND,
 * and a caller that built that intent itself would be a presentation concern reaching into the
 * platform for no reason.
 */
interface ContentSharer {

    /**
     * @param text what gets sent.
     * @param subject a title for targets that have one (mail, and little else). Ignored elsewhere.
     */
    fun shareText(text: String, subject: String? = null)
}

/** Does nothing, for tests and for any build with no share surface. */
object NoOpContentSharer : ContentSharer {
    override fun shareText(text: String, subject: String?) = Unit
}
