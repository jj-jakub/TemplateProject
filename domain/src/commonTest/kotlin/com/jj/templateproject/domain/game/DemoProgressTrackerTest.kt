package com.jj.templateproject.domain.game

import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.domain.achievement.AchievementStore
import com.jj.templateproject.domain.achievement.AchievementUnlocker
import com.jj.templateproject.domain.review.NoOpReviewPrompter
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.review.ReviewPromptStore
import com.jj.templateproject.domain.time.FixedClock
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

class DemoProgressTrackerTest {

    private class FakeGameStateStorage : GameStateStorage {
        private val slots = mutableMapOf<String, SavedGameState>()
        override suspend fun save(slot: String, state: SavedGameState) {
            slots[slot] = state
        }

        override suspend fun load(slot: String): SavedGameState? = slots[slot]
        override suspend fun delete(slot: String) {
            slots.remove(slot)
        }
    }

    private class FakeReviewPromptStore : ReviewPromptStore {
        var count = 0
        override fun readSatisfyingMomentCount() = count
        override fun writeSatisfyingMomentCount(count: Int) {
            this.count = count
        }

        override fun readHasPrompted() = false
        override fun writeHasPrompted(hasPrompted: Boolean) = Unit
    }

    private class FakeAchievementStore : AchievementStore {
        val unlocked = mutableSetOf<String>()
        override fun readUnlockedIds(): Set<String> = unlocked
        override fun markUnlocked(id: String) {
            unlocked += id
        }
    }

    private val gameStateStorage = FakeGameStateStorage()
    private val reviewStore = FakeReviewPromptStore()
    private val achievementStore = FakeAchievementStore()
    private val clock = FixedClock(now = 1_000L)
    private val tracker = DemoProgressTracker(
        gameStateStorage = gameStateStorage,
        reviewController = ReviewController(reviewStore, NoOpReviewPrompter),
        achievementUnlocker = AchievementUnlocker(achievementStore),
        clock = clock,
    )

    @Test
    fun `the first save resolves to a score of 1`() = runTest {
        val state = tracker.recordProgress(slot = "demo", previousScore = null)

        assertEquals(1, state.score)
    }

    @Test
    fun `the next save increments from the previous score`() = runTest {
        val state = tracker.recordProgress(slot = "demo", previousScore = 4)

        assertEquals(5, state.score)
    }

    @Test
    fun `a save is persisted through GameStateStorage`() = runTest {
        tracker.recordProgress(slot = "demo", previousScore = null)

        assertEquals(1, gameStateStorage.load("demo")?.score)
    }

    @Test
    fun `a save banks a satisfying moment with the review controller`() = runTest {
        tracker.recordProgress(slot = "demo", previousScore = null)

        assertEquals(1, reviewStore.count)
    }

    @Test
    fun `the first save unlocks the FIRST_SAVE achievement`() = runTest {
        tracker.recordProgress(slot = "demo", previousScore = null)

        assertTrue(Achievement.FIRST_SAVE in tracker.unlockedAchievements())
    }

    @Test
    fun `the first save does not unlock FIVE_SAVES yet`() = runTest {
        tracker.recordProgress(slot = "demo", previousScore = null)

        assertFalse(Achievement.FIVE_SAVES in tracker.unlockedAchievements())
    }

    @Test
    fun `the fifth save unlocks FIVE_SAVES`() = runTest {
        tracker.recordProgress(slot = "demo", previousScore = 4)

        assertTrue(Achievement.FIVE_SAVES in tracker.unlockedAchievements())
    }
}
