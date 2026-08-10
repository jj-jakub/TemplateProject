package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.achievement.Achievement
import com.jj.templateproject.domain.achievement.AchievementUnlocker
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.domain.game.DemoProgressTracker
import com.jj.templateproject.domain.game.SavedGameState
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.exception.NetworkError
import com.jj.templateproject.domain.review.NoOpReviewPrompter
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.streak.NoOpReminderScheduler
import com.jj.templateproject.domain.streak.StreakController
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.domain.time.FixedClock
import com.jj.templateproject.presentation.FakeAchievementStore
import com.jj.templateproject.presentation.FakeAppPreferencesRepository
import com.jj.templateproject.presentation.FakeAppVersionInfo
import com.jj.templateproject.presentation.FakeGameStateStorage
import com.jj.templateproject.presentation.FakeReviewPromptStore
import com.jj.templateproject.presentation.FakeStreakStore
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.state.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Built on real use cases over hand-written fakes at the repository boundary
 * ([FakeTemplateRepository], [FakeAppInfoRepository], [FakeAppPreferencesRepository]) rather than
 * mocked use cases: the use cases themselves are one-line delegations with nothing of their own
 * worth mocking, so faking one layer lower exercises the same behaviour more realistically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsScreenViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val templateRepository = FakeTemplateRepository()
    private val appInfoRepository = FakeAppInfoRepository()
    private val appPreferencesRepository = FakeAppPreferencesRepository()
    private val gameStateStorage = FakeGameStateStorage()
    private val reviewPromptStore = FakeReviewPromptStore()
    private val achievementStore = FakeAchievementStore()
    private val streakStore = FakeStreakStore()
    private val clock = FixedClock(now = 1_000L)
    private val demoProgressTracker = DemoProgressTracker(
        gameStateStorage = gameStateStorage,
        reviewController = ReviewController(reviewPromptStore, NoOpReviewPrompter),
        achievementUnlocker = AchievementUnlocker(achievementStore),
        clock = clock,
    )
    private val streakController = StreakController(streakStore, clock, NoOpReminderScheduler)

    private fun createViewModel(
        versionText: String = "v",
        themeMode: ThemeMode = ThemeMode.SYSTEM,
        tracker: DemoProgressTracker = demoProgressTracker,
        streaks: StreakController = streakController,
    ): SettingsScreenViewModel {
        // Reuse the shared fake for the common (SYSTEM) case; a non-default starting mode needs its
        // own instance, since FakeAppPreferencesRepository's theme mode is set at construction.
        val preferences = if (themeMode == ThemeMode.SYSTEM) {
            appPreferencesRepository
        } else {
            FakeAppPreferencesRepository(themeMode = themeMode)
        }
        return SettingsScreenViewModel(
            versionTextProvider = VersionTextProvider(FakeAppVersionInfo(versionName = versionText)),
            getGoogleStatusUseCase = GetGoogleStatusUseCase(templateRepository),
            getGoogleDataUseCase = GetGoogleDataUseCase(templateRepository),
            getIsInstalledFromValidSource = GetIsInstalledFromValidSource(appInfoRepository),
            getThemeModeUseCase = GetThemeModeUseCase(preferences),
            setThemeModeUseCase = SetThemeModeUseCase(preferences),
            demoProgressTracker = tracker,
            streakController = streaks,
        )
    }

    @Test
    fun `version text is taken from the provider`() {
        val viewModel = createViewModel(versionText = "abc")

        assertTrue(viewModel.viewState.value.versionText.contains("abc"))
    }

    @Test
    fun `successful api calls expose a Success state with Ok status and data`() {
        templateRepository.statusResult = BaseResult.Success(Unit)
        templateRepository.dataResult = BaseResult.Success("200")
        appInfoRepository.installedFromValidSource = true

        val state = createViewModel().viewState.value

        assertEquals(UiState.Success(ApiData(status = "Ok", data = "200")), state.apiState)
        assertEquals(true, state.installedFromValidSource)
    }

    @Test
    fun `a status error surfaces an error state with the message`() {
        templateRepository.statusResult = BaseResult.Error(NetworkError.Http(500, "server down"))
        appInfoRepository.installedFromValidSource = false

        val state = createViewModel().viewState.value

        assertEquals(UiState.Error("server down"), state.apiState)
        assertEquals(false, state.installedFromValidSource)
    }

    @Test
    fun `a data error surfaces an error state with the message`() {
        templateRepository.statusResult = BaseResult.Success(Unit)
        templateRepository.dataResult = BaseResult.Error(NetworkError.Connectivity)

        val state = createViewModel().viewState.value

        assertEquals(UiState.Error("No network connection"), state.apiState)
    }

    @Test
    fun `the persisted theme mode is observed into state`() {
        val state = createViewModel(themeMode = ThemeMode.DARK).viewState.value

        assertEquals(ThemeMode.DARK, state.themeMode)
    }

    @Test
    fun `retry re-runs the fetch and recovers from an error`() {
        templateRepository.statusResult = BaseResult.Error(NetworkError.Connectivity)
        templateRepository.dataResult = BaseResult.Success("200")

        val viewModel = createViewModel()
        assertEquals(UiState.Error("No network connection"), viewModel.viewState.value.apiState)

        templateRepository.statusResult = BaseResult.Success(Unit)
        viewModel.retry()

        assertEquals(
            UiState.Success(ApiData(status = "Ok", data = "200")),
            viewModel.viewState.value.apiState,
        )
        assertEquals(2, templateRepository.statusCallCount)
    }

    @Test
    fun `setThemeMode writes through to the preferences repository`() {
        val preferences = FakeAppPreferencesRepository(themeMode = ThemeMode.SYSTEM)
        val viewModel = SettingsScreenViewModel(
            versionTextProvider = VersionTextProvider(FakeAppVersionInfo()),
            getGoogleStatusUseCase = GetGoogleStatusUseCase(templateRepository),
            getGoogleDataUseCase = GetGoogleDataUseCase(templateRepository),
            getIsInstalledFromValidSource = GetIsInstalledFromValidSource(appInfoRepository),
            getThemeModeUseCase = GetThemeModeUseCase(preferences),
            setThemeModeUseCase = SetThemeModeUseCase(preferences),
            demoProgressTracker = demoProgressTracker,
            streakController = streakController,
        )

        viewModel.setThemeMode(ThemeMode.LIGHT)

        assertEquals(ThemeMode.LIGHT, viewModel.viewState.value.themeMode)
    }

    @Test
    fun `no saved progress at first is exposed as null rather than a placeholder score`() {
        val state = createViewModel().viewState.value

        assertNull(state.savedGameState)
    }

    @Test
    fun `a save is exposed in state with a score of 1 and the current time`() {
        val viewModel = createViewModel()

        viewModel.saveDemoProgress()

        val saved = viewModel.viewState.value.savedGameState
        assertEquals(SavedGameState(score = 1, progress = 0.1f, savedAtEpochMillis = 1_000L), saved)
    }

    @Test
    fun `each save increments the score from the previous save`() {
        val viewModel = createViewModel()

        viewModel.saveDemoProgress()
        viewModel.saveDemoProgress()
        viewModel.saveDemoProgress()

        assertEquals(3, viewModel.viewState.value.savedGameState?.score)
    }

    @Test
    fun `a save persists through GameStateStorage rather than only in view state`() = runTest {
        val viewModel = createViewModel()

        viewModel.saveDemoProgress()

        assertEquals(1, gameStateStorage.load("demo")?.score)
    }

    @Test
    fun `a previously saved slot is loaded into state on creation`() = runTest {
        gameStateStorage.save("demo", SavedGameState(score = 7, progress = 0.7f, savedAtEpochMillis = 5L))

        val state = createViewModel().viewState.value

        assertEquals(7, state.savedGameState?.score)
    }

    @Test
    fun `a save counts as a satisfying moment toward the review prompt`() {
        val store = FakeReviewPromptStore()
        val tracker = DemoProgressTracker(
            gameStateStorage = FakeGameStateStorage(),
            reviewController = ReviewController(store, NoOpReviewPrompter),
            achievementUnlocker = AchievementUnlocker(FakeAchievementStore()),
            clock = clock,
        )
        val viewModel = createViewModel(tracker = tracker)

        viewModel.saveDemoProgress()

        assertEquals(1, store.readSatisfyingMomentCount())
    }

    @Test
    fun `no achievements are unlocked before any save`() {
        val state = createViewModel().viewState.value

        assertEquals(emptySet(), state.unlockedAchievements)
    }

    @Test
    fun `the first save unlocks FIRST_SAVE in state`() {
        val viewModel = createViewModel()

        viewModel.saveDemoProgress()

        assertEquals(setOf(Achievement.FIRST_SAVE), viewModel.viewState.value.unlockedAchievements)
    }

    @Test
    fun `unlocked achievements from a previous session are loaded into state on creation`() {
        achievementStore.markUnlocked(Achievement.FIRST_SAVE.id)

        val state = createViewModel().viewState.value

        assertEquals(setOf(Achievement.FIRST_SAVE), state.unlockedAchievements)
    }

    @Test
    fun `the streak is 0 in state before any check-in`() {
        val state = createViewModel().viewState.value

        assertEquals(0, state.currentStreak)
    }

    @Test
    fun `checking in updates the streak in state`() {
        val viewModel = createViewModel()

        viewModel.checkInToday()

        assertEquals(1, viewModel.viewState.value.currentStreak)
    }

    @Test
    fun `an existing streak is loaded into state on creation`() {
        val store = FakeStreakStore(count = 4, lastCheckInEpochDay = clock.nowMillis() / MILLIS_PER_DAY)
        val streaks = StreakController(store, clock, NoOpReminderScheduler)

        val state = createViewModel(streaks = streaks).viewState.value

        assertEquals(4, state.currentStreak)
    }

    @Test
    fun `the reminder is disabled in state by default`() {
        val state = createViewModel().viewState.value

        assertFalse(state.reminderEnabled)
    }

    @Test
    fun `enabling the reminder is reflected in state`() {
        val viewModel = createViewModel()

        viewModel.setReminderEnabled(true)

        assertTrue(viewModel.viewState.value.reminderEnabled)
    }

    @Test
    fun `disabling the reminder is reflected in state`() {
        val store = FakeStreakStore(reminderEnabled = true)
        val streaks = StreakController(store, clock, NoOpReminderScheduler)
        val viewModel = createViewModel(streaks = streaks)

        viewModel.setReminderEnabled(false)

        assertFalse(viewModel.viewState.value.reminderEnabled)
    }

    private companion object {
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}
