package com.jj.templateproject.presentation.ui.state

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.TestTags
import com.jj.templateproject.util.ComposeComponentTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class UiStateContentTest : ComposeComponentTest() {

    @Test
    fun `loading state renders the loading slot`() {
        composeTestRule.setContent {
            TemplateTheme { UiStateContent(state = UiState.Loading) { Text("unused") } }
        }

        composeTestRule.onNodeWithTag(TestTags.LOADING_STATE).assertIsDisplayed()
    }

    @Test
    fun `success state renders the success slot`() {
        composeTestRule.setContent {
            TemplateTheme {
                UiStateContent(state = UiState.Success("Loaded value")) { Text(it) }
            }
        }

        composeTestRule.onNodeWithText("Loaded value").assertIsDisplayed()
    }

    @Test
    fun `error state renders the message and wires retry`() {
        var retries = 0
        composeTestRule.setContent {
            TemplateTheme {
                UiStateContent(
                    state = UiState.Error("Failed to load"),
                    onRetry = { retries++ },
                ) { Text("unused") }
            }
        }

        composeTestRule.onNodeWithText("Failed to load").assertIsDisplayed()
        composeTestRule.onNodeWithTag(TestTags.ERROR_RETRY).performClick()
        assertEquals(1, retries)
    }

    @Test
    fun `empty state renders the empty slot with the given title`() {
        composeTestRule.setContent {
            TemplateTheme {
                UiStateContent(state = UiState.Empty, emptyTitle = "All clear") { Text("unused") }
            }
        }

        composeTestRule.onNodeWithTag(TestTags.EMPTY_STATE).assertIsDisplayed()
        composeTestRule.onNodeWithText("All clear").assertIsDisplayed()
    }
}
