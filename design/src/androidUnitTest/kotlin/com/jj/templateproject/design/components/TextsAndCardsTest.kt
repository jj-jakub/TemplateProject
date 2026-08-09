package com.jj.templateproject.design.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.jj.templateproject.design.ComponentUiTest
import com.jj.templateproject.design.TemplateTheme
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class TextsAndCardsTest : ComponentUiTest() {

    @Test
    fun `section header and body text render their content`() {
        composeTestRule.setContent {
            TemplateTheme {
                SectionHeader(text = "About")
                BodyText(text = "Version 1.0")
            }
        }

        composeTestRule.onNodeWithText("About").assertIsDisplayed()
        composeTestRule.onNodeWithText("Version 1.0").assertIsDisplayed()
    }

    @Test
    fun `app card renders its slotted content`() {
        composeTestRule.setContent {
            TemplateTheme {
                AppCard {
                    Text(text = "Card body")
                }
            }
        }

        composeTestRule.onNodeWithText("Card body").assertIsDisplayed()
    }
}
