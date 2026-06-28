package com.jj.templateproject.design

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun TemplateTheme(
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    /**
     * When `true` (the default) the app uses Material You wallpaper-based colors on Android 12+
     * and falls back to the brand [LightColorScheme]/[DarkColorScheme] on older devices.
     *
     * Set this to `false` for a brand-locked app that must always render the brand palette — the
     * single switch needed to opt out of dynamic color everywhere.
     */
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isInDarkMode) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> brandColorScheme(darkTheme = isInDarkMode)
    }

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
        colorScheme = colorScheme,
        content = content,
        typography = Typography,
        shapes = Shapes,
    )
}
