# Architecture

`TemplateProject` is a Kotlin Multiplatform starter template (Android + iOS) built with Compose
Multiplatform + Material 3, Koin for DI, and Ktor + kotlinx-serialization for networking. This
document describes the module graph, the responsibilities of each module, the core patterns, the
iOS app, and the architecture rules that are enforced as tests.

## Module graph & dependency direction

Six Gradle modules, plus `iosApp` — a Swift/Xcode project outside Gradle entirely, generated from
`iosApp/project.yml` via `xcodegen`. The dependency direction between the Gradle modules is one-way
and enforced by Konsist. `:data` is a *layer* (package `com.jj.templateproject.data..`), not a
module — its Android-specific implementations live in `:app`; its shared implementations live in
`:networking` and (for a couple of iOS actuals) `:core`.

```
                    ┌──────────────────────────────┐
                    │              iosApp           │   Swift, outside Gradle
                    │  iOSApp.swift · ContentView    │   (xcodegen-generated project)
                    └───────────────┬────────────────┘
                                    │ embeds the Presentation.framework
                    ┌───────────────▼────────────────┐        ┌────────┐
                    │              :app                │        │        │
                    │  Android framework entry points  │        │        │
                    │  + Android-only :data impls       │        │        │
                    └───────┬───────────────────────────┘        │        │
                            │           ┌────────────────────────┘        │
                    ┌───────▼──────────▼──────────────────────────────────▼┐
                    │                       :presentation                    │
                    │  Compose screens · ViewModels · navigation · DI (own)  │
                    │  Kotlin Multiplatform + Compose Multiplatform          │
                    └───────┬───────────┬───────────┬────────────┬──────────┘
                            │           │           │            │
                    ┌───────▼─────┐ ┌───▼───┐ ┌─────▼──────┐     │
                    │ :networking │ │ :core │ │  :design   │     │
                    └───────┬─────┘ └───┬───┘ └────────────┘     │
                            │           │                        │
                            └─────┬─────┴────────────────────────┘
                                  ▼
                              ┌────────┐
                              │:domain │   pure Kotlin — depends on nothing
                              └────────┘
```

Rules in plain terms:

- **domain → nothing** (only external dep: kotlinx-coroutines for `Flow`/dispatchers).
- **data → domain** (implementations depend on domain interfaces, never the reverse).
- **presentation → domain + data** (in Gradle terms: `:networking`, `:core`, `:design`, `:domain`).
- **`:design` depends on nothing project-local** — it's a pure Compose Multiplatform design system
  with no data or domain concerns.
- **`:app` depends on `:presentation` + everything `:presentation` depends on** — it never bypasses
  `:presentation` to talk to `:networking`/`:core`/`:design` directly for UI purposes; those direct
  dependencies exist only so `mainModule` can bind the Android-only implementations those modules'
  shared code declares as interfaces.

All six Gradle modules are genuine Kotlin Multiplatform modules **except `:app`**, which is
Android-only by design — it's the Android application shell, not shared code. `:domain`,
`:networking`, `:core`, `:design`, and `:presentation` each build for `androidTarget()`, `iosArm64()`,
and `iosSimulatorArm64()`, with source sets split `commonMain` / `androidMain` / `iosMain` (and the
test equivalents).

## Module responsibilities

- **`:domain`** — pure Kotlin core (Multiplatform). Use cases (`GetGoogleDataUseCase`,
  `GetGoogleStatusUseCase`, `GetThemeModeUseCase`, `SetThemeModeUseCase`, `GetIsInstalledFromValidSource`);
  repository interfaces (`TemplateRepository`, `AppInfoRepository`, `AppPreferencesRepository`);
  `BaseResult<Data, Err : BaseError>` + `BaseResultExt` operators; `DispatcherProvider` /
  `expect class DefaultDispatcherProvider()`; `ThemeMode` enum; `NetworkError` sealed type;
  `AnalyticsLogger`/`CrashReporter`/`NotificationManager`/`AdManager` interfaces plus their shared
  NoOp defaults; the pure push-payload layer (`PushDestination`, `PushPayload`, `PushDeepLink`).
- **`:networking`** — Ktor `TemplateService`, repository impls (`DefaultTemplateRepository`),
  `TemplateNetwork`/`TemplateNetworkApi`, `TemplateHttpClientFactory` (the Ktor-plugin equivalent of
  the old Retrofit `RetrofitFactory`), `NetworkUtils` (`toResult` + `safeApiCall`), and
  `networkingModule` (Koin). `expect`/`actual`: `createPlatformHttpClient` (OkHttp on Android, Darwin
  on iOS) and `classifyTransportFailure` (each platform's own transport-failure exception types).
- **`:core`** — cross-cutting platform glue, Android + iOS. `AndroidNotificationManager`, Back4App
  init (`InitializeBack4App`, Android-only — no iOS counterpart), `UserDefaultsAppPreferencesRepository`
  (iOS's `AppPreferencesRepository`), and `coreModule` + `platformCoreModule()` (`expect`/`actual`)
  registering the use cases and platform managers (`Clock`, `DeviceInfo`, `ContentSharer`,
  `AppLifecycle`, `LaunchAttemptStore`).
- **`:design`** — the design system, Compose Multiplatform: `ColorTokens` / `ColorSchemes` /
  `BaseColors`, full M3 `Typography` (+ `AppFontFamily` seam), `Shapes`, `TemplateTheme(isInDarkMode,
  dynamicColor, content)`, the `components` package (`PrimaryButton`, `AppCard`, `LoadingState`,
  `ErrorState(onRetry)`, `EmptyState`, `ComponentCatalog`, …), `TestTags`. `expect`/`actual`:
  `platformColorScheme` (Android adds Material You dynamic color; iOS always uses the brand palette)
  and `AdjustSystemBarAppearance` (Android sets status/nav-bar icon contrast; iOS no-ops). The
  `@ThemePreviews` multipreview is Android-only tooling.
- **`:presentation`** — Compose screens + ViewModels, type-safe navigation (`Route` sealed types,
  `MainNavGraph`, `PushRoutes`), `presentationModule` (Koin), `MainRoot`/`MainRootViewModel`. Two
  `expect`/`actual` seams: `RequestNotificationPermissionOnLaunch` and `ComposeAdView`. Its `iosMain`
  is also the iOS app's Koin bootstrap and entry point: `MainViewController()` and
  `IosKoin.bootstrapKoin()`. See `presentation/README.md`.
- **`:app`** — the Android shell: DI wiring (`di/koin/mainModule`, `KoinLauncher`), framework
  (`TemplateProjectApplication`, edge-to-edge `MainActivity`), and the Android-only data impls
  (`AppConfiguration`/`TemplateHttpClientFactory` construction, `DataStoreAppPreferencesRepository`,
  NoOp/Firebase `AnalyticsLogger` & `CrashReporter`, `DefaultAdManager`, app-info/version providers).
  Android-only — not a Multiplatform module.
- **`iosApp`** — the iOS shell, generated by `xcodegen` from `iosApp/project.yml`: `iOSApp.swift`
  (`@main`, calls `IosKoinKt.bootstrapKoin()` once at startup) and `ContentView.swift` (wraps
  `MainViewControllerKt.MainViewController()` as a `UIViewControllerRepresentable`). See "The iOS
  app" below.

## The iOS app

`:presentation` exports a static Kotlin/Native framework (`baseName = "Presentation"`, both
`iosArm64` and `iosSimulatorArm64`, declared in `presentation/build.gradle.kts`). Nothing else is
`export()`-ed — `:domain`/`:networking`/`:core`/`:design`'s types aren't Swift-visible, since Swift
only ever calls `Presentation`'s own two entry points.

`iosApp/project.yml` is an `xcodegen` spec, not a committed `.xcodeproj` (git-ignored, regenerated
with `xcodegen generate`). Its `preBuildScripts` step runs
`./gradlew :presentation:embedAndSignAppleFrameworkForXcode` before every Xcode build, so the
framework is always current — there's no separate "remember to rebuild Kotlin first" step.

```
iosApp/
├── project.yml              xcodegen spec (targets, Info.plist properties, build settings)
└── iosApp/
    ├── iOSApp.swift          @main App: calls IosKoinKt.bootstrapKoin() once, in init()
    ├── ContentView.swift     UIViewControllerRepresentable wrapping MainViewControllerKt.MainViewController()
    ├── Info.plist            generated by xcodegen from project.yml's info.properties
    └── Assets.xcassets/      empty asset catalog (placeholder AppIcon)
```

`IosKoin.kt` (in `:presentation`'s `iosMain`) is the iOS counterpart of `:app`'s `KoinLauncher` +
`mainModule` combined: there's no separate Kotlin "iOS app" module, so the app-owned bindings
(`AdUnitIds`, `AppVersionInfo`, `AppInfoRepository`, `DispatcherProvider`, `HttpClient`, `AdManager`,
`AnalyticsLogger`, `CrashReporter`) live in its `iosAppModule`, using a real implementation where one
exists and `:domain`'s shared NoOp/always-true default otherwise. See `presentation/README.md` for
the full binding-by-binding breakdown, and `IosKoinGraphTest` for how the whole graph is verified to
actually resolve (not just compile).

**Building locally:**

```bash
cd iosApp && xcodegen generate
xcodebuild -project iosApp.xcodeproj -scheme iosApp \
  -destination "platform=iOS Simulator,name=iPhone 17" build
```

(`platform=iOS Simulator,name=<device>`, not the generic `platform=iOS Simulator` destination — the
generic form defaults to a multi-arch build including `x86_64`, which this project doesn't declare a
Kotlin/Native target for.)

## Key patterns

### `BaseResult` + extensions

Domain operations return `BaseResult<Data, Err : BaseError>`, a sealed `Success`/`Error` type.
`BaseResultExt` provides functional operators: `fold`, `map`, `mapError`, `flatMap`, `onSuccess`,
`onError`, `getOrNull`, `errorOrNull`, `getOrElse`, `getOrDefault`, `recover`,
`isSuccess`/`isError`.

### `NetworkError` + `safeApiCall`

`:networking` maps Ktor `HttpResponse`s and thrown exceptions into `BaseResult` via `toResult { }`
and `safeApiCall`. Failures become a `NetworkError`: `Http`, `Connectivity`, `Timeout`,
`Serialization`, or `Unknown` — each carries a non-null message. See `networking/README.md` for the
full exception-to-`NetworkError` mapping table, including the platform-`expect`/`actual`
`classifyTransportFailure`.

### `DispatcherProvider`

Threading is injected, never hardcoded. Repositories switch context with
`withContext(dispatcherProvider.io)`. Tests substitute a fake `DispatcherProvider`
(`kotlin.test`-based, not a mocking library — this type is used from Multiplatform tests too).

### `UiState` + `UiStateContent`

`UiState<T>` (in `com.jj.templateproject.presentation.ui.state`) is a sealed
`Loading`/`Success`/`Error`/`Empty` with helpers (`isLoading`, `dataOrNull`, `map`) and a
`BaseResult.toUiState()` bridge. `UiStateContent(state, onRetry, success { })` renders the
matching design-system slot (`LoadingState`, `ErrorState(onRetry)`, `EmptyState`, or your
success content).

### Theming / `ThemeMode`

`ThemeMode` (`SYSTEM`/`LIGHT`/`DARK`) is persisted via `AppPreferencesRepository` (DataStore on
Android, `NSUserDefaults` on iOS). `MainRootViewModel` exposes a `themeMode` `StateFlow`; `MainRoot`
maps it to dark/light and passes it to `TemplateTheme`. `TemplateTheme` uses dynamic color on
Android API 31+ and the brand palette otherwise (always the brand palette on iOS). Settings offers a
System/Light/Dark selector.

### Preferences

`AppPreferencesRepository` has two implementations, in two different modules: Android's
`DataStoreAppPreferencesRepository` (`:app`, needs a `Context`) and iOS's
`UserDefaultsAppPreferencesRepository` (`:core`, needs none) — see `core/README.md`.

### Analytics / crash seams

`AnalyticsLogger` and `CrashReporter` are bound to `:domain`'s shared `NoOpAnalyticsLogger`/
`NoOpCrashReporter` by default on both platforms, so the template runs with no
`google-services.json`. On Android, `AnalyticsFactory` swaps in the Firebase implementations once a
config file is present and the build is a reporting one; iOS has no Firebase wiring yet.

### Design system

Screens read `MaterialTheme.colorScheme` and compose from the `:design` `components` package;
they never reference raw color vals directly (enforced — see below).

## Request lifecycle: the Settings screen

A walkthrough of how a result flows end-to-end (Settings uses this with a working Retry), unchanged
on both platforms since `:presentation` is fully shared:

1. **ViewModel → UseCase** — the ViewModel invokes a use case (e.g. `GetGoogleStatusUseCase`).
2. **UseCase → Repository** — the use case calls a domain repository interface
   (`TemplateRepository`).
3. **Repository → `safeApiCall`** — `DefaultTemplateRepository` runs the Ktor call inside
   `withContext(dispatcherProvider.io)`, wrapping it with `safeApiCall` / `toResult`.
4. **→ `BaseResult`** — success yields `Success`; failures become `Error(NetworkError…)`.
5. **→ `UiState`** — the presentation layer bridges with `BaseResult.toUiState()`.
6. **→ `UiStateContent`** — the composable renders `LoadingState`, the success slot, or
   `ErrorState(onRetry)`; tapping Retry re-runs step 1.

## Konsist-enforced rules

Architecture invariants live as JUnit5 tests in `app/src/test/java/konsist` — they run on the JVM,
in `:app`'s own test source set, but `Konsist.scopeFromProject()` scans the whole multi-module
Gradle project (package-based layer matching, not module-based), so they hold across the KMP module
split too:

- The dependency direction above: **domain → nothing**, **data → domain**,
  **presentation → domain + data** (by package: `com.jj.templateproject.domain..`,
  `com.jj.templateproject.data..`, `com.jj.templateproject.presentation..`).
- Use cases are named `*UseCase` and reside in the `domain` package.
- ViewModels have a single constructor of private dependencies.
- Presentation must not import raw design color vals (use `MaterialTheme.colorScheme`).

Keep these green when adding or restructuring code — they run as part of
`./gradlew testFlavor1DebugUnitTest`.
