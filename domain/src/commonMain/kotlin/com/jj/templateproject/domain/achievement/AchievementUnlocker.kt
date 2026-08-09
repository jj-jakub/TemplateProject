package com.jj.templateproject.domain.achievement

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Unlocks [Achievement]s exactly once and publishes each fresh unlock on [justUnlocked], so a
 * screen can show a one-off "achievement unlocked" moment without polling [unlockedAchievements]
 * for a diff. Bound as a Koin singleton (`coreModule`) rather than constructed per-screen, since the
 * unlock history — and the stream of fresh unlocks — has to be the same one everywhere in the app,
 * not a screen-local copy.
 */
class AchievementUnlocker(
    private val store: AchievementStore,
) {

    private val _justUnlocked = MutableSharedFlow<Achievement>(extraBufferCapacity = 1)

    /** Emits once per achievement, the moment it transitions from locked to unlocked. */
    val justUnlocked: SharedFlow<Achievement> = _justUnlocked.asSharedFlow()

    fun isUnlocked(achievement: Achievement): Boolean = achievement.id in store.readUnlockedIds()

    /** No-op if [achievement] is already unlocked — [justUnlocked] never re-fires for it. */
    fun unlock(achievement: Achievement) {
        if (isUnlocked(achievement)) return
        store.markUnlocked(achievement.id)
        _justUnlocked.tryEmit(achievement)
    }

    fun unlockedAchievements(): Set<Achievement> {
        val unlockedIds = store.readUnlockedIds()
        return Achievement.entries.filterTo(mutableSetOf()) { it.id in unlockedIds }
    }
}
