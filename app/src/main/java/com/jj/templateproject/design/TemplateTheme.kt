package com.jj.templateproject.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    primary = colorPrimaryDark,
    background = white,
    secondary = colorAccent,
)

val LightColorScheme = lightColorScheme(
    primary = colorPrimary,
    background = white,
    secondary = colorAccent,
)

@Composable
fun TemplateTheme(
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isInDarkMode) DarkColorScheme else LightColorScheme,
        content = content,
        typography = Typography,
        shapes = Shapes,
    )
}