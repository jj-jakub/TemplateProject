package com.jj.templateproject.design

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Material You on Android 12+, the brand palette everywhere else. The version check is the whole
 * reason this is not shared code: `dynamicDarkColorScheme`/`dynamicLightColorScheme` read the
 * user's wallpaper through the platform's tonal-palette APIs, which simply do not exist below S.
 */
@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme {
    val context = LocalContext.current
    return when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        else -> brandColorScheme(darkTheme = darkTheme)
    }
}

/**
 * Edge-to-edge replacement for the deprecated accompanist-systemuicontroller: the Activity draws
 * behind transparent system bars (see MainActivity.enableEdgeToEdge), and here we only adapt the
 * status bar icon contrast to the current theme.
 */
@Composable
actual fun AdjustSystemBarAppearance(isInDarkMode: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return

    SideEffect {
        // Safe cast: this composable may be hosted outside an Activity (tests, tooling).
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !isInDarkMode
            isAppearanceLightNavigationBars = !isInDarkMode
        }
    }
}
