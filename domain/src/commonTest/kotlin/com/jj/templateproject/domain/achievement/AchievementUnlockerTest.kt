package com.jj.templateproject.domain.achievement

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

class AchievementUnlockerTest {

    private class FakeAchievementStore(
        private val unlocked: MutableSet<String> = mutableSetOf(),
    ) : AchievementStore {
        override fun readUnlockedIds(): Set<String> = unlocked
        override fun markUnlocked(id: String) {
            unlocked += id
        }
    }

    @Test
    fun `an achievement starts locked`() {
        val unlocker = AchievementUnlocker(FakeAchievementStore())

        assertFalse(unlocker.isUnlocked(Achievement.FIRST_SAVE))
    }

    @Test
    fun `unlocking marks it unlocked`() {
        val unlocker = AchievementUnlocker(FakeAchievementStore())

        unlocker.unlock(Achievement.FIRST_SAVE)

        assertTrue(unlocker.isUnlocked(Achievement.FIRST_SAVE))
    }

    @Test
    fun `unlocking twice keeps it unlocked and only unlocks once`() {
        val store = FakeAchievementStore()
        val unlocker = AchievementUnlocker(store)

        unlocker.unlock(Achievement.FIRST_SAVE)
        unlocker.unlock(Achievement.FIRST_SAVE)

        assertEquals(setOf(Achievement.FIRST_SAVE.id), store.readUnlockedIds())
    }

    @Test
    fun `unlocking one achievement does not unlock another`() {
        val unlocker = AchievementUnlocker(FakeAchievementStore())

        unlocker.unlock(Achievement.FIRST_SAVE)

        assertFalse(unlocker.isUnlocked(Achievement.FIVE_SAVES))
    }

    @Test
    fun `unlockedAchievements reflects what was persisted before construction`() {
        val store = FakeAchievementStore(unlocked = mutableSetOf(Achievement.FIRST_SAVE.id))

        assertEquals(setOf(Achievement.FIRST_SAVE), AchievementUnlocker(store).unlockedAchievements())
    }

    @Test
    fun `justUnlocked emits the achievement that was unlocked`() = runTest {
        val unlocker = AchievementUnlocker(FakeAchievementStore())
        val emitted = mutableListOf<Achievement>()
        // A collector has to be actively subscribed before the emission: justUnlocked is a one-off
        // event stream with no replay, the same shape MainScreenNavigation's own SharedFlow uses,
        // so a collector that starts after unlock() runs would never see it.
        val collector = launch { unlocker.justUnlocked.collect { emitted += it } }
        testScheduler.runCurrent()

        unlocker.unlock(Achievement.FIVE_SAVES)
        testScheduler.runCurrent()
        collector.cancel()

        assertEquals(listOf(Achievement.FIVE_SAVES), emitted)
    }
}
