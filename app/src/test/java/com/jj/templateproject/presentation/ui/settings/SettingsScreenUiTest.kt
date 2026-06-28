package com.jj.templateproject.presentation.ui.settings

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jj.templateproject.BaseInstrumentedKoinTest
import com.jj.templateproject.domain.theme.ThemeMode
import com.jj.templateproject.presentation.ui.settings.model.ApiData
import com.jj.templateproject.presentation.ui.settings.model.SettingsScreenViewState
import com.jj.templateproject.presentation.ui.state.UiState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
class SettingsScreenUiTest : BaseInstrumentedKoinTest<ComponentActivity>() {

    override fun getInstrumentedTestModules(): List<Module> = listOf(module {})

    @get:Rule(order = 2)
    override val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `renders version, status and data from state`() {
        val viewModel = mockk<SettingsScreenViewModel>(relaxed = true) {
            every { viewState } returns MutableStateFlow(
                SettingsScreenViewState(
                    versionText = "1.0",
                    apiState = UiState.Success(ApiData(status = "Ok", data = "200")),
                    installedFromValidSource = true,
                )
            )
        }

        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Version: 1.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("API call status: Ok").assertIsDisplayed()
        composeTestRule.onNodeWithText("API call data: 200").assertIsDisplayed()
    }

    @Test
    fun `selecting a theme option calls the view model`() {
        val viewModel = mockk<SettingsScreenViewModel>(relaxed = true) {
            every { viewState } returns MutableStateFlow(
                SettingsScreenViewState(
                    versionText = "1.0",
                    apiState = UiState.Success(ApiData(status = "Ok", data = "200")),
                    themeMode = ThemeMode.SYSTEM,
                )
            )
        }

        composeTestRule.setContent {
            SettingsScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("Dark").performClick()

        verify { viewModel.setThemeMode(ThemeMode.DARK) }
    }
}
