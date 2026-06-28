# Architecture

`TemplateProject` is a multi-module Android starter template built with Jetpack Compose +
Material 3, Koin for DI, and Retrofit 3 with kotlinx-serialization. This document describes the
module graph, the responsibilities of each module, the core patterns, and the architecture rules
that are enforced as tests.

## Module graph & dependency direction

The dependency direction is one-way and enforced by Konsist. `:data` is a *layer*
(package `com.jj.templateproject.data..`), not a module — its implementations live in `:app`
and `:networking`.

```
            ┌─────────────────────────────────────────┐
            │                  :app                    │
            │  presentation · navigation · DI wiring   │
            │  + data impls (Retrofit, DataStore, …)   │
            └───────┬───────────┬───────────┬──────────┘
                    │           │           │
        ┌───────────┘     ┌─────┘      ┌────┴───────┐
        ▼                 ▼            ▼            ▼
   :networking         :design      :core      (data layer)
   (data→domain)                  (→domain)    (→domain)
        │                              │
        └──────────────┬───────────────┘
                       ▼
                    :domain
                  (depends on NOTHING)
```

Rules in plain terms:

- **domain → nothing** (only external dep: kotlinx-coroutines for `Flow`/dispatchers).
- **data → domain** (implementations depend on domain interfaces, never the reverse).
- **presentation → domain + data**.

## Module responsibilities

- **`:domain`** — pure Kotlin core. Use cases (`GetGoogleDataUseCase`, `GetGoogleStatusUseCase`,
  `GetThemeModeUseCase`, `SetThemeModeUseCase`); repository interfaces (`TemplateRepository`,
  `AppInfoRepository`, `AppPreferencesRepository`); `BaseResult<Data, Err : BaseError>` +
  `BaseResultExt` operators; `DispatcherProvider` / `DefaultDispatcherProvider`; `ThemeMode`
  enum; `NetworkError` sealed type; `AnalyticsLogger`, `CrashReporter`, `NotificationManager`
  interfaces.
- **`:networking`** — Retrofit `TemplateService`, repository impls (`DefaultTemplateRepository`),
  `TemplateNetwork`, `NetworkUtils` (`toResult` + `safeApiCall`), and `networkingModule` (Koin).
- **`:core`** — platform glue: `AndroidNotificationManager`, Back4App init
  (`InitializeBack4App`), and `coreModule` registering the use cases and platform managers.
- **`:design`** — the design system: `ColorTokens` / `ColorSchemes` / `BaseColors`, full M3
  `Typography` (+ `AppFontFamily` seam), `Shapes`, `TemplateTheme(isInDarkMode, dynamicColor,
  content)`, the `components` package (`PrimaryButton`, `AppCard`, `LoadingState`,
  `ErrorState(onRetry)`, `EmptyState`, `ComponentCatalog`, …), `TestTags`, and the
  `@ThemePreviews` multipreview.
- **`:app`** — Compose screens + ViewModels, type-safe navigation (`Route` sealed types,
  `MainNavGraph`), DI wiring (`di/koin/mainModule`, `KoinLauncher`), framework
  (`TemplateProjectApplication`, edge-to-edge `MainActivity`), and the Android data impls
  (`RetrofitFactory`, `DataStoreAppPreferencesRepository`, NoOp/Firebase `AnalyticsLogger` &
  `CrashReporter`, `DefaultAdManager`, app-info/config providers).

## Key patterns

### `BaseResult` + extensions

Domain operations return `BaseResult<Data, Err : BaseError>`, a sealed `Success`/`Error` type.
`BaseResultExt` provides functional operators: `fold`, `map`, `mapError`, `flatMap`, `onSuccess`,
`onError`, `getOrNull`, `errorOrNull`, `getOrElse`, `getOrDefault`, `recover`,
`isSuccess`/`isError`.

### `NetworkError` + `safeApiCall`

`:networking` maps Retrofit `Response` and thrown exceptions into `BaseResult` via
`toResult { }` and `safeApiCall`. Failures become a `NetworkError`:
`Http`, `Connectivity`, `Timeout`, `Serialization`, or `Unknown` — each carries a non-null
message.

### `DispatcherProvider`

Threading is injected, never hardcoded. Repositories switch context with
`withContext(dispatcherProvider.io)`. Tests substitute a `TestDispatcherProvider` /
`MainDispatcherExtension`.

### `UiState` + `UiStateContent`

`UiState<T>` (in `com.jj.templateproject.presentation.ui.state`) is a sealed
`Loading`/`Success`/`Error`/`Empty` with helpers (`isLoading`, `dataOrNull`, `map`) and a
`BaseResult.toUiState()` bridge. `UiStateContent(state, onRetry, success { })` renders the
matching design-system slot (`LoadingState`, `ErrorState(onRetry)`, `EmptyState`, or your
success content).

### Theming / `ThemeMode`

`ThemeMode` (`SYSTEM`/`LIGHT`/`DARK`) is persisted via `AppPreferencesRepository` (DataStore).
`MainRootViewModel` exposes a `themeMode` `StateFlow`; `MainRoot` maps it to dark/light and
passes it to `TemplateTheme`. `TemplateTheme` uses dynamic color on API 31+ and the brand
palette otherwise. Settings offers a System/Light/Dark selector.

### Preferences / DataStore

`DataStoreAppPreferencesRepository` (in `:app`) implements `AppPreferencesRepository`, backing
theme (and other prefs) with DataStore.

### Analytics / crash seams

`AnalyticsLogger` and `CrashReporter` are bound to NoOp implementations by default, so the
template runs with no `google-services.json`. Firebase implementations are a documented one-line
Koin swap.

### Design system

Screens read `MaterialTheme.colorScheme` and compose from the `:design` `components` package;
they never reference raw color vals directly (enforced — see below).

## Request lifecycle: the Settings screen

A walkthrough of how a result flows end-to-end (Settings uses this with a working Retry):

1. **ViewModel → UseCase** — the ViewModel invokes a use case (e.g. `GetGoogleStatusUseCase`).
2. **UseCase → Repository** — the use case calls a domain repository interface
   (`TemplateRepository`).
3. **Repository → `safeApiCall`** — `DefaultTemplateRepository` runs the Retrofit call inside
   `withContext(dispatcherProvider.io)`, wrapping it with `safeApiCall` / `toResult`.
4. **→ `BaseResult`** — success yields `Success`; failures become `Error(NetworkError…)`.
5. **→ `UiState`** — the presentation layer bridges with `BaseResult.toUiState()`.
6. **→ `UiStateContent`** — the composable renders `LoadingState`, the success slot, or
   `ErrorState(onRetry)`; tapping Retry re-runs step 1.

## Konsist-enforced rules

Architecture invariants live as JUnit5 tests in `app/src/test/java/konsist`:

- The dependency direction above: **domain → nothing**, **data → domain**,
  **presentation → domain + data**.
- Use cases are named `*UseCase` and reside in the `domain` package.
- ViewModels have a single constructor of private dependencies.
- Presentation must not import raw design color vals (use `MaterialTheme.colorScheme`).

Keep these green when adding or restructuring code — they run as part of
`./gradlew testFlavor1DebugUnitTest`.
