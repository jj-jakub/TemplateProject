package com.jj.templateproject.design.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jj.templateproject.design.ComponentUiTest
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.TestTags
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class StateViewsTest : ComponentUiTest() {

    @Test
    fun `loading state is present`() {
        composeTestRule.setContent { TemplateTheme { LoadingState() } }

        composeTestRule.onNodeWithTag(TestTags.LOADING_STATE).assertIsDisplayed()
    }

    @Test
    fun `error state shows the message and retry invokes the callback`() {
        var retries = 0
        composeTestRule.setContent {
            TemplateTheme { ErrorState(message = "Something broke", onRetry = { retries++ }) }
        }

        composeTestRule.onNodeWithText("Something broke").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ERROR_RETRY).performClick()

        assertEquals(1, retries)
    }

    @Test
    fun `error state hides retry when no callback is given`() {
        composeTestRule.setContent {
            TemplateTheme { ErrorState(message = "Read only error") }
        }

        composeTestRule.onNodeWithTag(TestTags.ERROR_RETRY).assertDoesNotExist()
    }

    @Test
    fun `empty state shows the title and action invokes the callback`() {
        var actions = 0
        composeTestRule.setContent {
            TemplateTheme {
                EmptyState(title = "Nothing here", actionLabel = "Add", onAction = { actions++ })
            }
        }

        composeTestRule.onNodeWithText("Nothing here").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.EMPTY_ACTION).performClick()

        assertEquals(1, actions)
    }
}
