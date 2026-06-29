package com.jj.templateproject.ui

import android.Manifest
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelected
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.jj.templateproject.R
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.TestTags
import com.jj.templateproject.framework.navigation.MainNavGraph
import com.jj.templateproject.testsupport.FakeNetwork
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

/**
 * Instrumented UI tests that drive the app's real navigation graph (Home / Secondary / Settings)
 * on a device/emulator. The hermetic Koin graph (see [com.jj.templateproject.HermeticTestApplication])
 * fakes the network so Settings content is deterministic and stubs ads so nothing covers the UI.
 *
 * `MainNavGraph` is rendered directly so the real AdMob banner is never created (its WebView would
 * block Compose idle synchronization). POST_NOTIFICATIONS is granted up front so the runtime
 * permission dialog (requested by Main/Settings on launch) never steals focus from the app window.
 */
@RunWith(AndroidJUnit4::class)
class AppFlowsUiTest {

    private val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @get:Rule
    val rules: RuleChain = RuleChain
        // Granted only on API 33+, where Main/Settings actually request it; on older API levels
        // (e.g. the API 29 CI emulator) POST_NOTIFICATIONS isn't a grantable permission.
        .outerRule(GrantPermissionRule.grant(*notificationPermissions()))
        .around(composeTestRule)

    @After
    fun tearDown() {
        FakeNetwork.reset()
    }

    private fun str(resId: Int, vararg formatArgs: Any): String =
        composeTestRule.activity.getString(resId, *formatArgs)

    private fun launchApp() {
        composeTestRule.setContent {
            TemplateTheme {
                MainNavGraph(navController = rememberNavController())
            }
        }
    }

    private fun pressBack() {
        composeTestRule.runOnUiThread {
            composeTestRule.activity.onBackPressedDispatcher.onBackPressed()
        }
        composeTestRule.waitForIdle()
    }

    private fun openSettings() {
        composeTestRule.onNodeWithText(str(R.string.nav_settings)).performClick()
    }

    private fun selectThemeAndAwait(label: String) {
        composeTestRule.onNodeWithText(label).performClick()
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodes(hasText(label) and isSelected()).fetchSemanticsNodes().isNotEmpty()
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
    fun bottomNavigationSwitchesBetweenAllTabs() {
        launchApp()

        openSettings()
        composeTestRule.onNodeWithText(str(R.string.theme_section)).assertIsDisplayed()

        // The Secondary bottom-nav tab opens the screen with default (empty) arguments.
        composeTestRule.onNodeWithText(str(R.string.nav_secondary)).performClick()
        composeTestRule.onNodeWithText("First:", substring = true).assertIsDisplayed()

        composeTestRule.onNodeWithText(str(R.string.nav_home)).performClick()
        composeTestRule.onNodeWithText(str(R.string.navigation_testing)).assertIsDisplayed()
    }

    @Test
    fun everyMainActionNavigatesToSecondaryWithItsArguments() {
        launchApp()

        // text is always "First text1"; the four actions differ in the optional args they pass.
        navigateAndAssertArgs(R.string.navigate_without_optional_args, secondary = false, tertiary = false)
        navigateAndAssertArgs(R.string.navigate_with_first_optional_arg, secondary = true, tertiary = false)
        navigateAndAssertArgs(R.string.navigate_with_second_optional_arg, secondary = false, tertiary = true)
        navigateAndAssertArgs(R.string.navigate_with_all_optional_args, secondary = true, tertiary = true)
    }

    private fun navigateAndAssertArgs(actionRes: Int, secondary: Boolean, tertiary: Boolean) {
        composeTestRule.onNodeWithText(str(actionRes)).performClick()

        composeTestRule.onNodeWithText(str(R.string.secondary_first, "First text1")).assertIsDisplayed()
        val secondaryNode = composeTestRule.onNodeWithText(str(R.string.secondary_secondary, "Secondary text2"))
        val tertiaryNode = composeTestRule.onNodeWithText(str(R.string.secondary_tertiary, "Tertiary text3"))
        if (secondary) secondaryNode.assertIsDisplayed() else secondaryNode.assertDoesNotExist()
        if (tertiary) tertiaryNode.assertIsDisplayed() else tertiaryNode.assertDoesNotExist()

        pressBack()
        composeTestRule.onNodeWithText(str(R.string.navigation_testing)).assertIsDisplayed()
    }

    @Test
    fun settingsShowsApiContentAndThemeControls() {
        launchApp()
        openSettings()

        val status = str(R.string.api_call_status, "Ok")
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodesWithText(status).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(status).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.api_call_data, "200")).assertIsDisplayed()
        composeTestRule.onNodeWithText(str(R.string.theme_system)).assertIsDisplayed()
    }

    @Test
    fun settingsErrorStateRecoversViaRetry() {
        FakeNetwork.failStatusCall = true // the Settings VM is created on navigation, so set this first
        launchApp()
        openSettings()

        // Error slot with a Retry action is shown.
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodesWithTag(TestTags.ERROR_RETRY).fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag(TestTags.ERROR_RETRY).assertIsDisplayed()

        // Recover the network, retry, and the success content appears.
        FakeNetwork.failStatusCall = false
        composeTestRule.onNodeWithTag(TestTags.ERROR_RETRY).performClick()
        composeTestRule.waitUntil(TIMEOUT_MS) {
            composeTestRule.onAllNodesWithText(str(R.string.api_call_status, "Ok"))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText(str(R.string.api_call_data, "200")).assertIsDisplayed()
    }

    @Test
    fun themeSelectionUpdatesAcrossAllOptions() {
        launchApp()
        openSettings()

        selectThemeAndAwait(str(R.string.theme_dark))
        composeTestRule.onNodeWithText(str(R.string.theme_dark)).assertIsSelected()

        selectThemeAndAwait(str(R.string.theme_light))
        composeTestRule.onNodeWithText(str(R.string.theme_light)).assertIsSelected()

        // Restore System so the persisted preference doesn't leak into other tests/runs.
        selectThemeAndAwait(str(R.string.theme_system))
        composeTestRule.onNodeWithText(str(R.string.theme_system)).assertIsSelected()
    }

    private companion object {
        const val TIMEOUT_MS = 5_000L

        private fun notificationPermissions(): Array<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                emptyArray()
            }
    }
}
