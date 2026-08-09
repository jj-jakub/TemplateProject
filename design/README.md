# Design System (`:design`)

The shared design system for the template: a Material 3 color/type/shape foundation, a small component library, and the `TemplateTheme` wrapper that ties them together. Screens consume everything through `MaterialTheme.*` tokens — a Konsist rule forbids importing raw color vals into presentation — so re-branding happens here, in one module.

A Compose Multiplatform module (Android + iOS): `TemplateTheme` and every component live in
`commonMain` and render identically on both platforms. The one platform-divergent piece is
`PlatformTheme.kt` — an `expect`/`actual` pair for `platformColorScheme` (Android's actual adds
Material You dynamic color on API 31+; iOS's actual always returns the brand palette, since dynamic
color has no iOS equivalent) and `AdjustSystemBarAppearance` (Android's actual sets status/nav-bar
icon contrast; iOS's actual is a no-op, since UIKit has no equivalent system-bar-tinting API).
`@ThemePreviews`/`ComponentCatalog` stay Android-only (`androidMain`), since `@Preview` is a
tooling-only annotation with no Compose Multiplatform equivalent.

## Color system

Color flows through three layers, each derived from the one above:

1. **`BaseColors`** — the brand **seed** hues a branching app changes to re-theme:
   ```kotlin
   val colorPrimary = Color(0xFF4CAF50)
   val colorPrimaryDark = Color(0xFF388E3C)
   val colorAccent = Color(0xFFFFEB3B)
   ```
2. **`ColorTokens`** — the complete M3 role palette (primary/secondary/tertiary/surface/error/outline/inverse…) in **light and dark**, generated from the seed. Tokens follow the Material Theme Builder export naming (`md_light_*` / `md_dark_*`) and are `internal`, so nothing outside `:design` can bind a raw role color and bypass the theme.
3. **`ColorSchemes`** — `LightColorScheme` and `DarkColorScheme`, assembled from the tokens with every M3 role set explicitly (so cards, snackbars, chips, nav bars, etc. render correctly in both themes instead of half-styled defaults).

```
BaseColors (seeds) → ColorTokens (md_light_* / md_dark_*) → ColorSchemes (Light/DarkColorScheme)
```

## Typography + `AppFontFamily`

`Typography` is the full M3 type scale — display/headline/title/body/label, three sizes each — so text has a consistent, accessible hierarchy out of the box.

`AppFontFamily` is the **single branding seam** for type. Every role references it, so swapping the app font is a one-line change:

```kotlin
// default
val AppFontFamily: FontFamily = FontFamily.Default

// brand font: add files to res/font, then
val AppFontFamily = FontFamily(
    Font(R.font.brand_regular),
    Font(R.font.brand_medium, FontWeight.Medium),
)
```

Screens should reference type roles (`MaterialTheme.typography.bodyMedium`) rather than hard-coding `fontSize`.

## Shapes from the 8dp grid

`Shapes` is the M3 shape scale, with corner radii derived from `gridMultiple()` (the shared 8dp grid) so rounding stays in step with spacing and can be retuned in one place:

| Role | Radius |
|------|--------|
| `extraSmall` | 4dp |
| `small` | 8dp |
| `medium` | 12dp |
| `large` | 16dp |
| `extraLarge` | 28dp |

`gridMultiple(i)` returns `(8 * i).dp` and only accepts increments of `0.5` and `1`. Components reference shapes via `MaterialTheme.shapes` (buttons use `small`, cards use `medium`).

## `TemplateTheme`

The single entry point. It selects a `ColorScheme`, applies `Typography` and `Shapes`, and adapts the status/navigation bar icon contrast to the theme (the edge-to-edge replacement for `accompanist-systemuicontroller`).

```kotlin
@Composable
fun TemplateTheme(
    isInDarkMode: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
)
```

- **`dynamicColor = true`** (default): Material You wallpaper colors on **Android 12+**, falling back to the brand `Light`/`DarkColorScheme` on older devices (via `brandColorScheme(darkTheme)`).
- **`dynamicColor = false`**: always render the brand palette — the single switch for a brand-locked app.

In the app, `MainRootViewModel` exposes the persisted `ThemeMode` as a `StateFlow`; `MainRoot` maps it to dark/light and passes `isInDarkMode` here.

## Component library

All components read `MaterialTheme` tokens (color, type, shapes) so they follow the theme — including dynamic color — automatically.

- **Buttons** (`Buttons.kt`)
  - `PrimaryButton(text, onClick, …, leadingIcon?)` — filled CTA, `shapes.small`, guaranteed 48dp min touch target, optional leading icon.
  - `SecondaryButton(text, onClick, …, leadingIcon?)` — lower-emphasis outlined variant sharing the same shape/touch-target rules.
- **Card** (`Cards.kt`)
  - `AppCard(content: @Composable ColumnScope.() -> Unit)` — themed surface, `shapes.medium`, padded on the 8dp grid; lays content out in a `Column`.
- **Text** (`Texts.kt`)
  - `SectionHeader(text)` — `titleMedium` / `onSurface`.
  - `BodyText(text)` — `bodyMedium` / `onSurfaceVariant`.
- **State views** (`StateViews.kt`) — standardised async slots, each tagged for tests:
  - `LoadingState(description = "Loading")` — centered `CircularProgressIndicator` with a screen-reader description.
  - `ErrorState(message, retryLabel = "Retry", onRetry?)` — error text; Retry shown only when `onRetry` is provided.
  - `EmptyState(title, actionLabel?, onAction?)` — placeholder with an optional CTA.

These pair with presentation's `UiState<T>` / `UiStateContent` so screens map a `BaseResult` to the matching state slot (e.g. `SettingsScreen`'s working Retry).

## TestTags

`TestTags` holds stable `testTag` constants so UI tests target nodes by a fixed value instead of display text (which changes with copy and locale):

```kotlin
object TestTags {
    const val LOADING_STATE = "loading_state"
    const val ERROR_STATE   = "error_state"
    const val ERROR_RETRY   = "error_retry"
    const val EMPTY_STATE   = "empty_state"
    const val EMPTY_ACTION  = "empty_action"
}
```

The state views apply these tags; the design tests assert against them.

## `@ThemePreviews` and `ComponentCatalog`

- **`@ThemePreviews`** — a multipreview annotation that renders a composable in **light and dark** at once. Use it instead of `@Preview` so every preview is checked in both themes from a single annotation.
- **`ComponentCatalog()`** — a `@ThemePreviews` gallery of every component under `TemplateTheme`. It doubles as living documentation (open it in the IDE) and a manual visual-QA surface when re-branding.

## How to re-brand

1. Regenerate a tonal palette from a new seed (e.g. the Material Theme Builder) and paste the `md_light_*` / `md_dark_*` values into **`ColorTokens`**; update the seeds in **`BaseColors`**. `ColorSchemes` picks them up automatically.
2. (Optional) Drop a brand font in `res/font` and point **`AppFontFamily`** at it.
3. (Optional) Retune corner rounding in **`Shapes`** via `gridMultiple`.
4. (Optional) Set `dynamicColor = false` in `TemplateTheme` for a brand-locked palette.

Because screens read only `MaterialTheme.colorScheme` / `typography` / `shapes`, these changes propagate everywhere with no edits to presentation code. Verify visually via `ComponentCatalog` in both themes.
