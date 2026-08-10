package com.jj.templateproject.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.domain.game.DemoProgressTracker
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.streak.StreakController
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import com.jj.templateproject.presentation.ui.state.UiState
import com.jj.templateproject.presentation.ui.state.map
import com.jj.templateproject.presentation.ui.state.toUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    versionTextProvider: VersionTextProvider,
    private val getGoogleStatusUseCase: GetGoogleStatusUseCase,
    private val getGoogleDataUseCase: GetGoogleDataUseCase,
    private val getIsInstalledFromValidSource: GetIsInstalledFromValidSource,
    getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val demoProgressTracker: DemoProgressTracker,
    private val streakController: StreakController,
) : ViewModel() {

    private val _viewState = MutableStateFlow(
        SettingsScreenViewState(
            versionText = versionTextProvider.getAboutVersionText(),
        )
    )
    val viewState: StateFlow<SettingsScreenViewState> = _viewState.asStateFlow()

    init {
        fetchApiData()
        fetchInstallationValidity()
        loadSavedGameState()
        loadStreakState()
        getThemeModeUseCase()
            .onEach { mode -> _viewState.update { it.copy(themeMode = mode) } }
            .launchIn(viewModelScope)
        demoProgressTracker.justUnlocked
            .onEach { refreshUnlockedAchievements() }
            .launchIn(viewModelScope)
    }

    /** Re-runs the API fetch; wired to the error state's Retry action. */
    fun retry() {
        fetchApiData()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { setThemeModeUseCase(mode) }
    }

    /**
     * A working example of [DemoProgressTracker]'s three effects together — persisting the score,
     * banking a review-prompt moment, and unlocking any achievement it earns — the natural place
     * for all three, since a save is what makes each of them fire.
     */
    fun saveDemoProgress() {
        viewModelScope.launch {
            val state = demoProgressTracker.recordProgress(
                slot = DEMO_SLOT,
                previousScore = _viewState.value.savedGameState?.score,
            )
            _viewState.update { it.copy(savedGameState = state) }
        }
    }

    /** Records today's check-in; safe to tap more than once a day — the count only changes once. */
    fun checkInToday() {
        val streak = streakController.recordCheckIn()
        _viewState.update { it.copy(currentStreak = streak) }
    }

    fun setReminderEnabled(enabled: Boolean) {
        streakController.setReminderEnabled(enabled)
        _viewState.update { it.copy(reminderEnabled = enabled) }
    }

    private fun loadSavedGameState() {
        viewModelScope.launch {
            _viewState.update { it.copy(savedGameState = demoProgressTracker.loadSavedState(DEMO_SLOT)) }
        }
        refreshUnlockedAchievements()
    }

    private fun loadStreakState() {
        _viewState.update {
            it.copy(
                currentStreak = streakController.currentStreak(),
                reminderEnabled = streakController.isReminderEnabled(),
            )
        }
    }

    private fun refreshUnlockedAchievements() {
        _viewState.update { it.copy(unlockedAchievements = demoProgressTracker.unlockedAchievements()) }
    }

    private fun fetchApiData() {
        _viewState.update { it.copy(apiState = UiState.Loading) }
        viewModelScope.launch {
            val apiState = loadApiData()
            _viewState.update { it.copy(apiState = apiState) }
        }
    }

    private suspend fun loadApiData(): UiState<ApiData> {
        val statusResult = getGoogleStatusUseCase()
        if (statusResult is BaseResult.Error) {
            return UiState.Error(statusResult.error.message)
        }
        return getGoogleDataUseCase().toUiState().map { data ->
            ApiData(status = "Ok", data = data)
        }
    }

    private fun fetchInstallationValidity() {
        viewModelScope.launch {
            _viewState.update { it.copy(installedFromValidSource = getIsInstalledFromValidSource()) }
        }
    }

    private companion object {
        /** A single fixed slot for this demo; a real game would offer more than one save file. */
        const val DEMO_SLOT = "demo"
    }
}
