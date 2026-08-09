package com.jj.templateproject.design

import androidx.compose.material3.ColorScheme

/**
 * The brand fallback color scheme, used by [TemplateTheme] whenever dynamic color is disabled or
 * unsupported (Android 11 and below). Kept as a pure, testable function so the fallback decision
 * is verified without a device, while the dynamic-color branch stays a thin Android-12+ call.
 */
internal fun brandColorScheme(darkTheme: Boolean): ColorScheme =
    if (darkTheme) DarkColorScheme else LightColorScheme
