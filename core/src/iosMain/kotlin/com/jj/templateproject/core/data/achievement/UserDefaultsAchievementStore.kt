package com.jj.templateproject.core.data.achievement

import com.jj.templateproject.domain.achievement.AchievementStore
import platform.Foundation.NSUserDefaults

/**
 * Unlocked achievement ids, in `NSUserDefaults` — **not** excluded from anything, unlike this
 * codebase's other `NSUserDefaults`-backed stores: see `AchievementStore`'s doc comment for why an
 * unlock is the one kind of per-device state that's supposed to travel.
 */
class UserDefaultsAchievementStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : AchievementStore {

    @Suppress("UNCHECKED_CAST")
    override fun readUnlockedIds(): Set<String> =
        (defaults.stringArrayForKey(KEY_UNLOCKED_IDS) as? List<String>)?.toSet() ?: emptySet()

    override fun markUnlocked(id: String) {
        defaults.setObject((readUnlockedIds() + id).toList(), KEY_UNLOCKED_IDS)
    }

    private companion object {
        const val KEY_UNLOCKED_IDS = "achievements.unlocked_ids"
    }
}
