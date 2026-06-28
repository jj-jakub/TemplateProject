package com.jj.templateproject.design

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that renders a composable in both light and dark themes at once. Apply
 * it instead of `@Preview` so every preview is checked in both themes from a single annotation.
 */
@Preview(name = "Light", group = "themes", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark", group = "themes", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreviews
