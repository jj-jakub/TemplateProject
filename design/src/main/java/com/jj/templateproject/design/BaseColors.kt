package com.jj.templateproject.design

import androidx.compose.ui.graphics.Color

/**
 * Brand **seed** colors for the template.
 *
 * These are the source hues a branching app changes to re-brand the design system. The full
 * Material 3 role palette (primary/secondary/tertiary/surface/error/… in light *and* dark) is
 * derived from these and lives in [ColorTokens]; the assembled schemes are in [ColorSchemes].
 *
 * To re-theme the app, regenerate the tonal palette from a new seed (e.g. with the Material
 * Theme Builder) and update [ColorTokens] — screens consume `MaterialTheme.colorScheme`, never
 * these raw values.
 */
val colorPrimary = Color(0xFF4CAF50)
val colorPrimaryDark = Color(0xFF388E3C)
val colorAccent = Color(0xFFFFEB3B)

/**
 * Light surface/background tint kept so screens that have not yet migrated to
 * `MaterialTheme.colorScheme.background` keep compiling. Aliased to the light background token
 * so there is a single source of truth.
 */
val colorBackground = md_light_background
