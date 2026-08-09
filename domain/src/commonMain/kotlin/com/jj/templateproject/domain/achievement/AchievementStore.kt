package com.jj.templateproject.domain.achievement

/**
 * Which [Achievement.id]s have been unlocked. Unlike `LaunchAttemptStore`/`ReviewPromptStore`/
 * `InstallIdStore`, this is deliberately **not** excluded from backup: an unlock is the user's
 * progress, not per-install bookkeeping, so it belongs to exactly the kind of state a device
 * transfer or cloud restore should carry (see `backup_rules.xml`'s own doc comment on that split).
 */
interface AchievementStore {
    fun readUnlockedIds(): Set<String>
    fun markUnlocked(id: String)
}
