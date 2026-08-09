package com.jj.templateproject.core.data.review

import android.content.Context
import com.jj.templateproject.domain.review.ReviewPromptStore

/**
 * The satisfying-moment count and the "already asked" flag, in their own SharedPreferences file —
 * same rationale as `SharedPreferencesLaunchAttemptStore`'s own file: a restored backup should not
 * silently carry a review ask's history onto a device that never actually earned it (see
 * `backup_rules.xml`).
 */
class SharedPreferencesReviewPromptStore(
    context: Context,
) : ReviewPromptStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun readSatisfyingMomentCount(): Int = preferences.getInt(KEY_MOMENT_COUNT, 0)

    override fun writeSatisfyingMomentCount(count: Int) {
        preferences.edit().putInt(KEY_MOMENT_COUNT, count).apply()
    }

    override fun readHasPrompted(): Boolean = preferences.getBoolean(KEY_HAS_PROMPTED, false)

    override fun writeHasPrompted(hasPrompted: Boolean) {
        preferences.edit().putBoolean(KEY_HAS_PROMPTED, hasPrompted).apply()
    }

    companion object {
        /** Named in backup_rules.xml and data_extraction_rules.xml; keep the three in step. */
        const val FILE_NAME = "review_prompt"
        private const val KEY_MOMENT_COUNT = "satisfying_moment_count"
        private const val KEY_HAS_PROMPTED = "has_prompted"
    }
}
