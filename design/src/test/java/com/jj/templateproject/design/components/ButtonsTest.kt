package com.jj.templateproject.design.components

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.jj.templateproject.design.ComponentUiTest
import com.jj.templateproject.design.TemplateTheme
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ButtonsTest : ComponentUiTest() {

    @Test
    fun `primary button renders its label and invokes onClick`() {
        var clicks = 0
        composeTestRule.setContent {
            TemplateTheme { PrimaryButton(text = "Continue", onClick = { clicks++ }) }
        }

        composeTestRule.onNodeWithText("Continue").assertHasClickAction().performClick()

        assertEquals(1, clicks)
    }

    @Test
    fun `disabled primary button does not invoke onClick`() {
        var clicks = 0
        composeTestRule.setContent {
            TemplateTheme {
                PrimaryButton(text = "Disabled", onClick = { clicks++ }, enabled = false)
            }
        }

        composeTestRule.onNodeWithText("Disabled").assertIsNotEnabled()
        composeTestRule.onNodeWithText("Disabled").performClick()

        assertEquals(0, clicks)
    }

    @Test
    fun `secondary button renders its label and invokes onClick`() {
        var clicks = 0
        composeTestRule.setContent {
            TemplateTheme { SecondaryButton(text = "Cancel", onClick = { clicks++ }) }
        }

        composeTestRule.onNodeWithText("Cancel").assertHasClickAction().performClick()

        assertEquals(1, clicks)
    }
}
