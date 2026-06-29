package com.jj.templateproject.ui

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.framework.navigation.MainNavGraph
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

/**
 * Instrumented UI tests that drive the app's real navigation graph (Home / Secondary / Settings)
 * on a device/emulator. The hermetic Koin graph (see [com.jj.templateproject.HermeticTestApplication])
 * fakes the network so Settings content is deterministic and stubs ads so nothing covers the UI.
 *
 * `MainNavGraph` is rendered directly so the real AdMob banner is never created (its WebView would
 * block Compose idle synchronization).
 */
@RunWith(AndroidJUnit4::class)
class AppFlowsUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun str(resId: Int, vararg formatArgs: Any): String =
        composeTestRule.activity.getString(resId, *formatArgs)

    private fun launchApp() {
        composeTestRule.setContent {
            TemplateTheme {
                MainNavGraph(navController = rememberNavController())
            }
        }
    }

    @Test
    fun appStartsOnTheHomeScreen() {
        launchApp()

        composeTestRule.onNodeWithText(str(R.string.navigation_testing)).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.nav_home)).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.nav_settings)).assertIsDisplayed()
    }

    @Test
    fun bottomNavigationSwitchesBetweenTabs() {
        launchApp()

        composeTestRule.onNodeWithText(str(R.string.nav_settings)).performClick()
        composeTestRule.onNodeWithText(str(R.string.theme_section)).assertIsDisplayed()

        composeTestRule.onNodeWithText(str(R.string.nav_home)).performClick()
        composeTestRule.onNodeWithText(str(R.string.navigation_testing)).assertIsDisplayed()
    }

    @Test
    fun mainActionNavigatesToTheSecondaryScreenWithArguments() {
        launchApp()

        composeTestRule.onNodeWithText(str(R.string.navigate_with_all_optional_args)).performClick()

        composeTestRule.onNodeWithText(str(R.string.secondary_first, "First text1")).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.secondary_tertiary, "Tertiary text3"))
            .assertIsDisplayed()
    }

    @Test
    fun settingsShowsApiContentAndThemeControls() {
        launchApp()
        composeTestRule.onNodeWithText(str(R.string.nav_settings)).performClick()

        val status = str(R.string.api_call_status, "Ok")
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodesWithText(status).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(status).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.api_call_data, "200")).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.theme_system)).assertIsDisplayed()
    }

    @Test
    fun themeSelectionUpdatesTheSelectedOption() {
        launchApp()
        composeTestRule.onNodeWithText(str(R.string.nav_settings)).performClick()

        val dark = str(R.string.theme_dark)
        composeTestRule.onNodeWithText(dark).performClick()
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodes(hasText(dark) and isSelected()).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(dark).assertIsSelected()

        // Restore System so the persisted preference doesn't leak into other tests/runs.
        val system = str(R.string.theme_system)
        composeTestRule.onNodeWithText(system).performClick()
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodes(hasText(system) and isSelected()).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private companion object {
        const val TIMEOUT_MS = 5_000L
    }
}
