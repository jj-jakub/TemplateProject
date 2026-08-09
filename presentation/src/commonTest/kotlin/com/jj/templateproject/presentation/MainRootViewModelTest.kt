package com.jj.templateproject.presentation

import com.jj.templateproject.domain.ad.GetMainAdUnitId
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.ThemeMode
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

@OptIn(ExperimentalCoroutinesApi::class)
class MainRootViewModelTest {

    // Unconfined, not Standard: MainRootViewModel's themeMode is stateIn(..., Eagerly, ...) inside
    // viewModelScope (backed by this Main dispatcher), and these tests read `.value` synchronously
    // right after construction, the same way the flow they observe used to be a cold flowOf(...).
    // A Standard dispatcher would leave that eager collection merely queued, not yet run.
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeTest
    fun setUp() = Dispatchers.setMain(testDispatcher)

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val analyticsLogger = FakeAnalyticsLogger()

    private fun createViewModel(
        adUnitId: String = "ca-app-pub/main",
        themeMode: ThemeMode = ThemeMode.SYSTEM,
    ): MainRootViewModel {
        val getMainAdUnitId = GetMainAdUnitId(FakeAdUnitIds(mainBannerId = adUnitId))
        val getThemeModeUseCase = GetThemeModeUseCase(FakeAppPreferencesRepository(themeMode = themeMode))
        return MainRootViewModel(getMainAdUnitId, getThemeModeUseCase, analyticsLogger)
    }

    @Test
    fun `initial state exposes the main ad unit id`() {
        assertEquals("ca-app-pub/main", createViewModel().viewState.value.adMainUnitId)
    }

    @Test
    fun `theme mode is exposed from the use case`() = runTest {
        val viewModel = createViewModel(themeMode = ThemeMode.DARK)

        assertEquals(ThemeMode.DARK, viewModel.themeMode.value)
    }

    @Test
    fun `onAdClicked logs an analytics event and does not mutate state`() {
        val viewModel = createViewModel()
        val before = viewModel.viewState.value

        viewModel.onAdClicked()

        assertEquals(before, viewModel.viewState.value)
        assertEquals(listOf("ad_clicked" to emptyMap()), analyticsLogger.loggedEvents)
    }
}
