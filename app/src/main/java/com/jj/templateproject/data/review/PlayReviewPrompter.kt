package com.jj.templateproject.data.review

import com.google.android.play.core.review.ReviewManager
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.domain.review.ReviewPrompter

/**
 * Play's in-app review flow. The [ReviewManager] is injected (built from
 * `ReviewManagerFactory.create(context)` at the DI-wiring call site in `mainModule`) rather than
 * constructed here, so this class can be unit-tested against a mock instead of the real Play Core
 * SDK — the same reason `TemplateService` takes an `HttpClient` rather than building one.
 *
 * `requestReviewFlow()` fetches a one-time-use `ReviewInfo` token; Play decides internally whether
 * the dialog actually shows (an undocumented quota, not queryable), so a successful
 * `launchReviewFlow` call is not a promise the user saw anything. Both steps fail silently rather
 * than falling back to anything (e.g. a Play Store deep link) — [ReviewController] already only
 * calls this once, ever, so a failed attempt here is not worth spending that budget on a fallback
 * the user did not ask for.
 */
class PlayReviewPrompter(
    private val reviewManager: ReviewManager,
    private val activityProvider: ActivityProvider,
) : ReviewPrompter {

    override fun requestReview() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (!task.isSuccessful) return@addOnCompleteListener
            val activity = activityProvider.activeActivity ?: return@addOnCompleteListener
            reviewManager.launchReviewFlow(activity, task.result)
        }
    }
}
