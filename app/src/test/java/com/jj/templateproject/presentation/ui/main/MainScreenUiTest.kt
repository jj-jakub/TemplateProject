package com.jj.templateproject.presentation.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.jj.templateproject.BaseInstrumentedKoinTest
import com.jj.templateproject.presentation.ui.main.model.MainScreenNavigation
import com.jj.templateproject.presentation.ui.main.model.MainScreenViewState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.module.Module
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class MainScreenUiTest : BaseInstrumentedKoinTest<ComponentActivity>() {

    override fun getInstrumentedTestModules(): List<Module> = listOf(module {})

    @get:Rule(order = 2)
    override val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun mainScreenViewModel() = mockk<MainScreenViewModel>(relaxed = true) {
        every { viewState } returns MutableStateFlow(MainScreenViewState())
        every { navigation } returns MutableSharedFlow<MainScreenNavigation>()
    }

    @Test
    fun `tapping navigate without optional args calls the view model`() {
        val viewModel = mainScreenViewModel()
        composeTestRule.setContent {
            MainScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Navigate without optional args").performClick()

        verify { viewModel.navigateWithoutOptionalArgs() }
    }

    @Test
    fun `tapping navigate with all optional args calls the view model`() {
        val viewModel = mainScreenViewModel()
        composeTestRule.setContent {
            MainScreen(navController = rememberNavController(), viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Navigate with all optional args").performClick()

        verify { viewModel.navigateWithAllOptionalArgs() }
    }
}
