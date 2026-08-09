package com.jj.templateproject.core.data.review

import com.jj.templateproject.domain.review.ReviewPromptStore
import platform.Foundation.NSUserDefaults

/**
 * The satisfying-moment count and the "already asked" flag, in `NSUserDefaults` — same shape as
 * `UserDefaultsLaunchAttemptStore`. Namespaced keys rather than a separate suite: iOS has nothing
 * like `backup_rules.xml`, so there is no exclusion mechanism to lean on the way Android's
 * `SharedPreferencesReviewPromptStore` does; an iCloud-restored count costs at most one held-back
 * review ask, not a functional break.
 */
class UserDefaultsReviewPromptStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : ReviewPromptStore {

    override fun readSatisfyingMomentCount(): Int = defaults.integerForKey(KEY_MOMENT_COUNT).toInt()

    override fun writeSatisfyingMomentCount(count: Int) {
        defaults.setInteger(count.toLong(), KEY_MOMENT_COUNT)
    }

    override fun readHasPrompted(): Boolean = defaults.boolForKey(KEY_HAS_PROMPTED)

    override fun writeHasPrompted(hasPrompted: Boolean) {
        defaults.setBool(hasPrompted, KEY_HAS_PROMPTED)
    }

    private companion object {
        const val KEY_MOMENT_COUNT = "review_prompt.satisfying_moment_count"
        const val KEY_HAS_PROMPTED = "review_prompt.has_prompted"
    }
}
