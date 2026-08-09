package com.jj.templateproject.presentation.ui.main

import app.cash.turbine.test
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
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
import kotlin.test.assertTrue

private class FakeAdManager : AdManager {
    var incrementCount = 0
        private set

    override fun initAds() = Unit
    override fun incrementActionsForAd() {
        incrementCount++
    }

    override fun showInterstitialAd() = Unit
}

/**
 * The two cases this used to cover for `requiredPermissions` moved with that field: asking for the
 * notification permission is now [com.jj.templateproject.presentation.RequestNotificationPermissionOnLaunch],
 * a composable seam with no ViewModel-level state to assert on (see that file's doc comment for
 * why it was pulled out of both `MainScreenViewModel` and `SettingsScreenViewModel`).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainScreenViewModelTest {

    @BeforeTest
    fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun tearDown() = Dispatchers.resetMain()

    private val adManager = FakeAdManager()

    private fun createViewModel() = MainScreenViewModel(adManager)

    @Test
    fun `init counts an action towards showing an ad`() {
        createViewModel()

        assertEquals(1, adManager.incrementCount)
    }

    @Test
    fun `initial state is loading`() {
        val state = createViewModel().viewState.value

        assertTrue(state.loading)
    }

    @Test
    fun `navigateWithoutOptionalArgs emits secondary screen without optional args`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigation.test {
            viewModel.navigateWithoutOptionalArgs()
            assertEquals(
                MainScreenNavigation.SecondaryScreen("First text1", null, null),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateWithFirstOptionalArg emits secondary screen with secondary text`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigation.test {
            viewModel.navigateWithFirstOptionalArg()
            assertEquals(
                MainScreenNavigation.SecondaryScreen("First text1", "Secondary text2", null),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateWithSecondOptionalArg emits secondary screen with tertiary text`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigation.test {
            viewModel.navigateWithSecondOptionalArg()
            assertEquals(
                MainScreenNavigation.SecondaryScreen("First text1", null, "Tertiary text3"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `navigateWithAllOptionalArgs emits secondary screen with all texts`() = runTest {
        val viewModel = createViewModel()

        viewModel.navigation.test {
            viewModel.navigateWithAllOptionalArgs()
            assertEquals(
                MainScreenNavigation.SecondaryScreen("First text1", "Secondary text2", "Tertiary text3"),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }
    }
}
