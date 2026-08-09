# `:presentation`

The shared UI module: Compose screens + ViewModels (MVVM), type-safe navigation, and the Koin module
that assembles them. A genuine Kotlin Multiplatform + Compose Multiplatform module (Android + iOS) —
this is the entire reason the KMM conversion exists: everything in this module renders on both
platforms unmodified. It depends on `:domain`, `:networking`, `:core`, and `:design`.

`:app` (Android) and `iosApp` (iOS, Swift) are thin shells around it: each provides the platform
entry point, the app-only DI bindings only it can supply (a real `AdUnitIds`/`AppVersionInfo`,
`HttpClient`/`DispatcherProvider`, analytics), and starts Koin. Neither owns any screen or ViewModel.

## Layout

```
MainRoot.kt, MainRootViewModel.kt        top-level composable + its ViewModel
navigation/                              Route, MainNavGraph, PushRoutes
ui/main, ui/secondary, ui/settings/      the three screens and their ViewModels
ui/state/                                UiState<T> + UiStateContent
ui/ads/                                  ComposeAdView (expect/actual)
di/presentationModule.kt                 Koin module: this module's use cases + ViewModels
RequestNotificationPermissionOnLaunch.kt expect/actual: Android asks, iOS no-ops
androidMain/…Previews.android.kt         @ThemePreviews composables (tooling-only, Android-only)
iosMain/                                 MainViewController(), IosKoin.bootstrapKoin(), iOS-only bindings
```

## Screens & ViewModels (MVVM)

Each screen is a `@Composable` driven by a Koin-provided ViewModel (`koinViewModel()`, Koin's
multiplatform Compose ViewModel DSL — not `org.koin.androidx.compose`, which is Android-only),
following a consistent contract:

- **State out** — the ViewModel exposes an immutable `*ViewState` via a `StateFlow`
  (`MutableStateFlow` private, `asStateFlow()` public). The screen reads it with
  `collectAsState()`.
- **One-off navigation events** — emitted through a `MutableSharedFlow<…Navigation>`
  (`asSharedFlow()`), collected once in a `LaunchedEffect` and turned into `navController.navigate(...)`.
  This keeps navigation out of the rendered state so it isn't replayed on recomposition.

Screens:

- `MainScreen` / `MainScreenViewModel` — demonstrates the nav-event pattern (four
  `navigateWith…OptionalArgs()` actions emit `MainScreenNavigation.SecondaryScreen`), and pings
  `AdManager` (`NoOpAdManager` on iOS, since no ad SDK is wired up there yet).
- `SecondaryScreen` / `SecondaryScreenViewModel` — reads its typed args from `SavedStateHandle`.
- `SettingsScreen` / `SettingsScreenViewModel` — Google status/data, app version & install source,
  and a System/Light/Dark theme selector. Uses `UiStateContent` with a working **Retry**.

### `MainRoot` & `MainRootViewModel`

`MainRoot` is the top-level composable, shared by both platforms (Android's `MainActivity` and
iOS's `MainViewController()` both call it directly). `MainRootViewModel` exposes a
`themeMode: StateFlow<ThemeMode>` (via `GetThemeModeUseCase().stateIn(...)`) and the banner ad unit
id; `MainRoot` maps `ThemeMode` (`SYSTEM`→`isSystemInDarkTheme()`, `LIGHT`/`DARK`) to a boolean and
passes it to `TemplateTheme`, then renders `ComposeAdView` + `MainNavGraph`.

`MainRoot` also takes an optional `pushDestination: PushDestination?` + `onPushDestinationHandled`
— where a notification tap or a `templateproject://` deep link asked to go. Android's `MainActivity`
resolves and passes this (push has no iOS counterpart yet, see `PUSH.md`); iOS's `MainViewController()`
leaves both at their `null`/no-op defaults.

### `UiState`

`ui.state.UiState<T>` is a sealed `Loading/Success/Error/Empty` with helpers (`isLoading`,
`dataOrNull`, `map`) and a `BaseResult.toUiState()` bridge. `UiStateContent(state, onRetry, success { })`
renders the matching `:design` state slot (`LoadingState`, `ErrorState`, `EmptyState`).

## Navigation (type-safe Compose)

- `navigation/model/Route.kt` — a `sealed interface Route` of `@Serializable` destinations
  (`MainScreen`, `SecondaryScreen(text, textSecondary?, textTertiary?)`, `SettingsScreen`), plus
  `matchesCurrentEntry` / `findSelectedIndex` helpers for bottom-bar selection.
- `navigation/MainNavGraph.kt` — a `Scaffold` with a Material 3 `NavigationBar` (Home / Secondary /
  Settings, text-only labels — `material-icons-core`'s Compose Multiplatform release line isn't
  version-paired with this project's CMP version yet, so icons were dropped rather than risk an
  unverified pairing) over a `NavHost` whose `composable<Route.*>` entries resolve each ViewModel
  via `koinViewModel()`. Bottom-bar taps `navigate` with `popUpTo(startDestination)`,
  `launchSingleTop`, and `restoreState`.
- `navigation/PushRoutes.kt` — maps a `PushDestination` (from `:domain`) to a `Route`, the seam
  `MainRoot`'s push-handling `LaunchedEffect` uses.
- Screen-local navigation intents live in `…/model/*Navigation.kt` (e.g. `MainScreenNavigation`),
  which wrap a concrete `Route`.

## Platform seams this module owns

Both `expect`/`actual` pairs, so the *screens* that use them stay fully shared:

- **`RequestNotificationPermissionOnLaunch()`** — a `@Composable` that asks for the notification
  runtime permission once, on first composition. Android's actual does the real
  `accompanist-permissions` ask (API 33+); iOS's is a no-op (no equivalent permission model there).
- **`ComposeAdView(adUnitId, onAdClicked)`** — a banner ad. Android's actual renders the real AdMob
  banner; iOS's renders nothing (no Kotlin/Native cinterop for the Google Mobile Ads SDK yet), so a
  screen that includes it degrades to "no banner" rather than a broken layout.

## DI wiring (`presentationModule`)

```kotlin
val presentationModule = module {
    single { GetMainAdUnitId(adUnitIds = get()) }
    single { GetInterstitialAdUnitId(adUnitIds = get()) }
    single { VersionTextProvider(appVersionInfo = get()) }
    single { GetIsInstalledFromValidSource(appInfoRepository = get()) }

    viewModel { MainScreenViewModel(adManager = get()) }
    viewModel { SettingsScreenViewModel(...) }
    viewModel { MainRootViewModel(...) }
    viewModel { SecondaryScreenViewModel(savedStateHandle = get()) }
}
```

`presentationModule` never provides `AdUnitIds`, `AppVersionInfo`, `HttpClient`, or
`DispatcherProvider` themselves — those come from whichever app-layer module actually has a real
value to give (`:app`'s `mainModule` on Android, `IosKoin`'s `iosAppModule` on iOS). This module
assembles together with `networkingModule` (`:networking`), `coreModule` + `platformCoreModule()`
(`:core`), and the app-owned module:

```kotlin
// Android — :app's KoinLauncher
modules(mainModule, networkingModule, coreModule, platformCoreModule(), presentationModule)

// iOS — IosKoin.bootstrapKoin()
modules(networkingModule, coreModule, platformCoreModule(), presentationModule, iosAppModule)
```

## The iOS entry point (`iosMain`)

- **`MainViewController()`** — `fun MainViewController(): UIViewController = ComposeUIViewController { MainRoot(...) }`.
  The one symbol Swift calls (`MainViewControllerKt.MainViewController()` from the generated
  Objective-C header), wrapped as a `UIViewControllerRepresentable` in `iosApp/iosApp/ContentView.swift`.
- **`IosKoin.kt`** — `bootstrapKoin()` (not `initKoin`: Kotlin/Native's Objective-C export renames
  any top-level function starting with `init`, since it collides with Cocoa's initializer
  convention) assembles the graph and binds `iosAppModule` — the iOS counterpart of `:app`'s
  `mainModule`: `AdUnitIds`, `AppVersionInfo`, `AppInfoRepository`, `DispatcherProvider`,
  `HttpClient`, `AdManager`, `AnalyticsLogger`, `CrashReporter`, all either real (where a platform
  API exists) or the shared NoOp/always-true default from `:domain`.
- **`IosKoinGraphTest`** (`iosTest`) — the iOS counterpart of `:app`'s `KoinGraphTest`: builds the
  real graph via a standalone `koinApplication` and asserts every singleton/ViewModel resolves. A
  `xcodebuild`/link success only proves the Kotlin/Swift boundary compiles, not that the Koin graph
  behind it actually resolves — this test is what closes that gap (it once caught a genuinely
  missing `AppPreferencesRepository`/`HttpClient`/`DispatcherProvider` binding before either shipped).

See `iosApp/project.yml` and `ARCHITECTURE.md`'s iOS section for how the framework this module
exports gets embedded into the Xcode project.

## Where tests live

- `src/commonTest/` — `kotlin.test` unit tests for ViewModels and pure helpers, run on both the JVM
  and `iosSimulatorArm64` (`./gradlew :presentation:testDebugUnitTest :presentation:iosSimulatorArm64Test`).
  Hand-written fakes (`FakeAppPreferencesRepository`, `FakeTemplateRepository`, `FakeAppInfoRepository`)
  rather than MockK, which is JVM-only.
- `src/androidUnitTest/` — Android-only tests that need to mock a platform framework type with no
  clean fake (`NavBackStackEntry`, `SavedStateHandle`) via MockK, plus Robolectric Compose UI tests.
- `src/iosTest/` — `IosKoinGraphTest` (see above).
