package com.jj.templateproject.design

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that renders a composable in both light and dark themes at once. Apply
 * it instead of `@Preview` so every preview is checked in both themes from a single annotation.
 *
 * Android-only on purpose, even though the rest of the design system is shared. A preview is
 * tooling, not runtime behavior: nothing an app does at run time depends on this annotation, so
 * there is no multiplatform contract worth preserving. What it does depend on is `uiMode` and
 * `Configuration.UI_MODE_NIGHT_*`, which are an Android `@Preview` parameter and two Android
 * constants; Xcode's preview canvas has no equivalent notion of a night ui mode to map them onto.
 * An expect/actual pair would therefore buy an iOS actual that could only drop the one thing this
 * annotation exists to express.
 */
@Preview(name = "Light", group = "themes", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark", group = "themes", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemePreviews
