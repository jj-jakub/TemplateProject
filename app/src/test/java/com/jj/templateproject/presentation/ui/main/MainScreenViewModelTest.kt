package com.jj.templateproject.presentation.ui.main

import android.Manifest
import app.cash.turbine.test
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
import com.jj.templateproject.util.MainDispatcherRule
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class MainScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val adManager = mockk<AdManager>(relaxed = true)

    private fun createViewModel() = MainScreenViewModel(adManager)

    @Test
    fun `init counts an action towards showing an ad`() {
        createViewModel()

        verify(exactly = 1) { adManager.incrementActionsForAd() }
    }

    @Test
    fun `initial state is loading and requires the notifications permission on API 33`() {
        val state = createViewModel().viewState.value

        assertTrue(state.loading)
        assertEquals(listOf(Manifest.permission.POST_NOTIFICATIONS), state.requiredPermissions)
    }

    @Test
    @Config(sdk = [30])
    fun `no runtime permission is required below API 33`() {
        val state = createViewModel().viewState.value

        assertTrue(state.requiredPermissions.isEmpty())
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
