package com.jj.templateproject.core.data.achievement

import android.content.Context
import com.jj.templateproject.domain.achievement.AchievementStore

/**
 * Unlocked achievement ids, in their own SharedPreferences file — **not** listed in
 * `backup_rules.xml`/`data_extraction_rules.xml`, unlike every other store this codebase's platform
 * seams add: an achievement unlock is the user's progress, so it's exactly the state a backup or
 * device transfer is supposed to carry (see `AchievementStore`'s own doc comment).
 */
class SharedPreferencesAchievementStore(
    context: Context,
) : AchievementStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun readUnlockedIds(): Set<String> =
        preferences.getStringSet(KEY_UNLOCKED_IDS, null)?.toSet() ?: emptySet()

    override fun markUnlocked(id: String) {
        preferences.edit().putStringSet(KEY_UNLOCKED_IDS, readUnlockedIds() + id).apply()
    }

    private companion object {
        const val FILE_NAME = "achievements"
        const val KEY_UNLOCKED_IDS = "unlocked_ids"
    }
}
