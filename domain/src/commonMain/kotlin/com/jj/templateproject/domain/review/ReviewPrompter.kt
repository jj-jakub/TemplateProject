package com.jj.templateproject.domain.review

/**
 * Asks the platform's app store to show its in-app review dialog. A request, never a guarantee —
 * the store itself decides whether the dialog actually appears (an undocumented, unqueryable quota
 * on Android; a similar yearly-request cap on iOS), which is why this is a fire-and-forget call
 * rather than something a caller could branch on.
 */
interface ReviewPrompter {
    fun requestReview()
}

/** No app-store review surface wired up for this platform yet (today: iOS). */
object NoOpReviewPrompter : ReviewPrompter {
    override fun requestReview() = Unit
}
