package com.jj.templateproject.design

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * The two places [TemplateTheme] genuinely has to differ per platform. Everything else about the
 * theme (the type scale, the shape scale, the brand palette, the composable's own shape) is shared,
 * so the boundary sits here rather than at the theme composable itself: a caller writes
 * `TemplateTheme { … }` in common code and gets whatever each platform can honestly provide.
 */

/**
 * Picks the [ColorScheme] the app renders with.
 *
 * [dynamicColor] asks for the platform's own wallpaper-derived palette *if it has one*; whether it
 * is honoured is deliberately the platform's answer to give, since Material You exists on Android
 * 12+ and nowhere else. Every platform falls back to [brandColorScheme], so the brand palette is
 * always the floor rather than a half-styled default.
 *
 * `@Composable` because Android's implementation needs the composition-local `Context` to read the
 * wallpaper palette, not because the result is expected to change on its own.
 */
@Composable
expect fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme

/**
 * Adapts the system bars' *icon* contrast to the theme, so the clock and battery stay readable once
 * the app draws behind them. Called once from [TemplateTheme] rather than by each screen, because
 * it is a property of the theme and not of any one screen.
 *
 * This is an appearance side effect, not state: platforms with no such concept implement it as a
 * documented no-op instead of forcing every caller to guard.
 */
@Composable
expect fun AdjustSystemBarAppearance(isInDarkMode: Boolean)
