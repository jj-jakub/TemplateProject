package com.jj.templateproject.design

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun TemplateTheme(
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Edge-to-edge replacement for the deprecated accompanist-systemuicontroller:
    // the Activity draws behind transparent system bars (see MainActivity.enableEdgeToEdge),
    // and here we only adapt the status bar icon contrast to the current theme.
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            // Safe cast: this composable may be hosted outside an Activity (tests, tooling).
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !isInDarkMode
                isAppearanceLightNavigationBars = !isInDarkMode
            }
        }
    }

    MaterialTheme(
        colorScheme = if (isInDarkMode) DarkColorScheme else LightColorScheme,
        content = content,
        typography = Typography,
        shapes = Shapes,
    )
}
