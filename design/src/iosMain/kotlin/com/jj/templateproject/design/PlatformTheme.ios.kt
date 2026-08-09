package com.jj.templateproject.design

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

/**
 * Always the brand palette.
 *
 * [dynamicColor] is inert here, and that is the honest answer rather than an oversight: Material
 * You derives its palette from the Android wallpaper through a platform API iOS has no counterpart
 * for. Rather than invent one (a hand-rolled palette extractor would be a different feature wearing
 * the same parameter's name), iOS renders the brand colors the design system already defines. The
 * parameter stays in the shared signature so a screen written once still compiles and reads the
 * same on both platforms.
 */
@Composable
actual fun platformColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme =
    brandColorScheme(darkTheme = darkTheme)

/**
 * A deliberate no-op.
 *
 * On iOS the status bar's appearance is decided by the hosting `UIViewController`
 * (`preferredStatusBarStyle`, plus `UIViewControllerBasedStatusBarAppearance` in Info.plist), which
 * in a Compose Multiplatform app is the controller the iOS app wires up around
 * `ComposeUIViewController`. Reaching into UIKit from inside a theme composable to override that
 * would fight the host for ownership of a value the host is meant to declare, and it would do so
 * from shared code that cannot see how the app is embedded. So the iOS side of the app sets its own
 * status bar style, and this stays empty.
 */
@Composable
actual fun AdjustSystemBarAppearance(isInDarkMode: Boolean) {
    // Intentionally empty: see the doc comment above.
}
