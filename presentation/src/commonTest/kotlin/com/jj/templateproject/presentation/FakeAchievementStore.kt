package com.jj.templateproject.presentation

import com.jj.templateproject.domain.achievement.AchievementStore

class FakeAchievementStore : AchievementStore {

    private val unlocked = mutableSetOf<String>()

    override fun readUnlockedIds(): Set<String> = unlocked

    override fun markUnlocked(id: String) {
        unlocked += id
    }
}
