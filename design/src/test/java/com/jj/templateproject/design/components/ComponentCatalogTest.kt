package com.jj.templateproject.design.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import com.jj.templateproject.design.ComponentUiTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class ComponentCatalogTest : ComponentUiTest() {

    @Test
    fun `catalog renders the component gallery`() {
        composeTestRule.setContent { ComponentCatalog() }

        composeTestRule.onNodeWithText("Primary").assertIsDisplayed()
        composeTestRule.onNodeWithText("Card title").assertIsDisplayed()
    }
}
