package com.jj.templateproject.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun TemplateTheme(
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    /**
     * When `true` (the default) the app uses Material You wallpaper-based colors on Android 12+
     * and falls back to the brand [LightColorScheme]/[DarkColorScheme] on older devices.
     *
     * Set this to `false` for a brand-locked app that must always render the brand palette — the
     * single switch needed to opt out of dynamic color everywhere. It has no effect on iOS, which
     * has no wallpaper-derived palette to read (see the iOS [platformColorScheme]).
     */
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    AdjustSystemBarAppearance(isInDarkMode = isInDarkMode)

    MaterialTheme(
        colorScheme = platformColorScheme(darkTheme = isInDarkMode, dynamicColor = dynamicColor),
        content = content,
        typography = Typography,
        shapes = Shapes,
    )
}
