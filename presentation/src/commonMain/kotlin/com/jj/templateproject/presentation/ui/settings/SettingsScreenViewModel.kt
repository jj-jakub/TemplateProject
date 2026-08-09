package com.jj.templateproject.presentation.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.domain.game.GameStateStorage
import com.jj.templateproject.domain.game.SavedGameState
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.domain.time.Clock
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
    private val gameStateStorage: GameStateStorage,
    private val reviewController: ReviewController,
    private val clock: Clock,
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
        getThemeModeUseCase()
            .onEach { mode -> _viewState.update { it.copy(themeMode = mode) } }
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
     * A working example of [GameStateStorage] and [ReviewController] together: a save is exactly
     * the kind of moment [ReviewController] exists to count toward the review ask, so recording one
     * here is not a separate step a real screen would add later — it is the natural place for it.
     */
    fun saveDemoProgress() {
        viewModelScope.launch {
            val nextScore = (_viewState.value.savedGameState?.score ?: 0) + 1
            val state = SavedGameState(
                score = nextScore,
                progress = (nextScore % PROGRESS_CYCLE) / PROGRESS_CYCLE.toFloat(),
                savedAtEpochMillis = clock.nowMillis(),
            )
            gameStateStorage.save(DEMO_SLOT, state)
            _viewState.update { it.copy(savedGameState = state) }
            reviewController.recordSatisfyingMoment()
        }
    }

    private fun loadSavedGameState() {
        viewModelScope.launch {
            _viewState.update { it.copy(savedGameState = gameStateStorage.load(DEMO_SLOT)) }
        }
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

        /** Cycles the demo progress bar back to empty every 10 saves, purely for a visible example. */
        const val PROGRESS_CYCLE = 10
    }
}
