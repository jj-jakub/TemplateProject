package com.jj.templateproject.presentation.ui.secondary

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.jj.templateproject.BaseInstrumentedKoinTest
import com.jj.templateproject.presentation.ui.secondary.model.SecondaryScreenViewState
import io.mockk.every
import io.mockk.mockk
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
class SecondaryScreenUiTest : BaseInstrumentedKoinTest<ComponentActivity>() {

    override fun getInstrumentedTestModules(): List<Module> = listOf(module {})

    @get:Rule(order = 2)
    override val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `renders the three argument texts from state`() {
        val viewModel = mockk<SecondaryScreenViewModel> {
            every { viewState } returns MutableStateFlow(
                SecondaryScreenViewState(text = "Alpha", secondaryText = "Beta", tertiaryText = "Gamma")
            )
        }

        composeTestRule.setContent {
            SecondaryScreen(viewModel = viewModel)
        }

        composeTestRule.onNodeWithText("First: Alpha").assertIsDisplayed()
        composeTestRule.onNodeWithText("Secondary: Beta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tertiary: Gamma").assertIsDisplayed()
    }
}
