package com.jj.templateproject.presentation.ui.secondary

import androidx.compose.runtime.Composable
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews

@ThemePreviews
@Composable
private fun PreviewSecondaryScreen() {
    TemplateTheme {
        SecondaryScreenContent(
            text = "state.text",
            secondaryText = "state.secondaryText",
            tertiaryText = "state.tertiaryText",
        )
    }
}
