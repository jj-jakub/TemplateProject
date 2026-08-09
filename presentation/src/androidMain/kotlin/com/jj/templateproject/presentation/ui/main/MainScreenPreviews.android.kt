package com.jj.templateproject.presentation.ui.main

import androidx.compose.runtime.Composable
import com.jj.templateproject.design.TemplateTheme
import com.jj.templateproject.design.ThemePreviews

@ThemePreviews
@Composable
private fun PreviewMainScreenViewContent() {
    TemplateTheme {
        MainScreenViewContent(
            navigateWithoutOptionalArgs = {},
            navigateWithFirstOptionalArg = {},
            navigateWithSecondOptionalArg = {},
            navigateWithAllOptionalArgs = {},
            crossPromoLabel = "Try our other app",
            onCrossPromoClicked = {},
        )
    }
}
