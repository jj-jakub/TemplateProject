package com.jj.templateproject.domain.game

import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.domain.achievement.AchievementUnlocker
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.time.Clock
import kotlinx.coroutines.flow.SharedFlow

/**
 * Everything the Settings screen's "Save progress" demo touches, bundled behind one collaborator
 * rather than four raw dependencies on the ViewModel: [GameStateStorage] to persist the score,
 * [ReviewController] because a save is exactly the kind of moment it exists to count toward the
 * review ask, and [AchievementUnlocker] because this is also the demo's only source of "satisfying
 * moments" worth an achievement. All three only ever act together here, which is what makes this a
 * genuine orchestrator rather than a parameter-count workaround.
 */
class DemoProgressTracker(
    private val gameStateStorage: GameStateStorage,
    private val reviewController: ReviewController,
    private val achievementUnlocker: AchievementUnlocker,
    private val clock: Clock,
) {

    /** Fresh unlocks from any save this tracker records — see [AchievementUnlocker.justUnlocked]. */
    val justUnlocked: SharedFlow<Achievement> get() = achievementUnlocker.justUnlocked

    suspend fun loadSavedState(slot: String): SavedGameState? = gameStateStorage.load(slot)

    fun unlockedAchievements(): Set<Achievement> = achievementUnlocker.unlockedAchievements()

    /** Saves the next score for [slot], banks a satisfying moment, and unlocks any achievement it earns. */
    suspend fun recordProgress(slot: String, previousScore: Int?): SavedGameState {
        val nextScore = (previousScore ?: 0) + 1
        val state = SavedGameState(
            score = nextScore,
            progress = (nextScore % PROGRESS_CYCLE) / PROGRESS_CYCLE.toFloat(),
            savedAtEpochMillis = clock.nowMillis(),
        )
        gameStateStorage.save(slot, state)
        reviewController.recordSatisfyingMoment()
        if (nextScore >= FIRST_SAVE_SCORE) achievementUnlocker.unlock(Achievement.FIRST_SAVE)
        if (nextScore >= FIVE_SAVES_SCORE) achievementUnlocker.unlock(Achievement.FIVE_SAVES)
        return state
    }

    private companion object {
        /** Cycles the demo progress bar back to empty every 10 saves, purely for a visible example. */
        const val PROGRESS_CYCLE = 10
        const val FIRST_SAVE_SCORE = 1
        const val FIVE_SAVES_SCORE = 5
    }
}
