package com.jj.templateproject.design

import androidx.compose.ui.graphics.Color

/**
 * The complete Material 3 role palette, in light and dark, derived from the green brand seed in
 * [BaseColors]. These are the single source of truth for color; [ColorSchemes] assembles them
 * into `ColorScheme`s and screens read them through `MaterialTheme.colorScheme`.
 *
 * Naming mirrors the Material Theme Builder export (`md_light_*` / `md_dark_*`) so a regenerated
 * palette can be pasted in wholesale. Tokens are `internal` so the rest of the app cannot bind
 * to a raw role color and bypass the theme.
 */

// region Light
internal val md_light_primary = Color(0xFF2E6A36)
internal val md_light_onPrimary = Color(0xFFFFFFFF)
internal val md_light_primaryContainer = Color(0xFFB1F1AE)
internal val md_light_onPrimaryContainer = Color(0xFF00210A)
internal val md_light_secondary = Color(0xFF52634F)
internal val md_light_onSecondary = Color(0xFFFFFFFF)
internal val md_light_secondaryContainer = Color(0xFFD5E8CF)
internal val md_light_onSecondaryContainer = Color(0xFF101F10)
internal val md_light_tertiary = Color(0xFF38656A)
internal val md_light_onTertiary = Color(0xFFFFFFFF)
internal val md_light_tertiaryContainer = Color(0xFFBCEBF0)
internal val md_light_onTertiaryContainer = Color(0xFF002023)
internal val md_light_error = Color(0xFFBA1A1A)
internal val md_light_onError = Color(0xFFFFFFFF)
internal val md_light_errorContainer = Color(0xFFFFDAD6)
internal val md_light_onErrorContainer = Color(0xFF410002)
internal val md_light_background = Color(0xFFF7FBF1)
internal val md_light_onBackground = Color(0xFF191D17)
internal val md_light_surface = Color(0xFFF7FBF1)
internal val md_light_onSurface = Color(0xFF191D17)
internal val md_light_surfaceVariant = Color(0xFFDDE5D8)
internal val md_light_onSurfaceVariant = Color(0xFF424940)
internal val md_light_outline = Color(0xFF72796F)
internal val md_light_outlineVariant = Color(0xFFC1C9BC)
internal val md_light_scrim = Color(0xFF000000)
internal val md_light_inverseSurface = Color(0xFF2E322C)
internal val md_light_inverseOnSurface = Color(0xFFEFF2E9)
internal val md_light_inversePrimary = Color(0xFF96D894)
// endregion

// region Dark
internal val md_dark_primary = Color(0xFF96D894)
internal val md_dark_onPrimary = Color(0xFF003912)
internal val md_dark_primaryContainer = Color(0xFF14521E)
internal val md_dark_onPrimaryContainer = Color(0xFFB1F1AE)
internal val md_dark_secondary = Color(0xFFB9CCB4)
internal val md_dark_onSecondary = Color(0xFF243424)
internal val md_dark_secondaryContainer = Color(0xFF3A4B39)
internal val md_dark_onSecondaryContainer = Color(0xFFD5E8CF)
internal val md_dark_tertiary = Color(0xFFA0CFD4)
internal val md_dark_onTertiary = Color(0xFF00363B)
internal val md_dark_tertiaryContainer = Color(0xFF1E4D52)
internal val md_dark_onTertiaryContainer = Color(0xFFBCEBF0)
internal val md_dark_error = Color(0xFFFFB4AB)
internal val md_dark_onError = Color(0xFF690005)
internal val md_dark_errorContainer = Color(0xFF93000A)
internal val md_dark_onErrorContainer = Color(0xFFFFDAD6)
internal val md_dark_background = Color(0xFF11140F)
internal val md_dark_onBackground = Color(0xFFE1E4DB)
internal val md_dark_surface = Color(0xFF11140F)
internal val md_dark_onSurface = Color(0xFFE1E4DB)
internal val md_dark_surfaceVariant = Color(0xFF424940)
internal val md_dark_onSurfaceVariant = Color(0xFFC1C9BC)
internal val md_dark_outline = Color(0xFF8C9388)
internal val md_dark_outlineVariant = Color(0xFF424940)
internal val md_dark_scrim = Color(0xFF000000)
internal val md_dark_inverseSurface = Color(0xFFE1E4DB)
internal val md_dark_inverseOnSurface = Color(0xFF2E322C)
internal val md_dark_inversePrimary = Color(0xFF2E6A36)
// endregion
