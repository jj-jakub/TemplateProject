# `:app` — presentation & composition root

The application module: Compose screens + ViewModels (MVVM), type-safe navigation, the Koin
composition root, and the Android-specific `:data` implementations. It depends on `:domain`,
`:design`, `:networking`, and `:core`.

## Layout

```
framework/      TemplateProjectApplication, MainActivity, navigation (Route, MainNavGraph)
presentation/   MainRoot + screens (main, secondary, settings) and their ViewModels, ui/state
di/koin/        mainModule, KoinLauncher
data/           Android-bound impls: network, preferences, analytics, ad, app, config, firebase
```

## Screens & ViewModels (MVVM)

Each screen is a `@Composable` driven by a Koin-provided ViewModel (`koinViewModel()`), following a
consistent contract:

- **State out** — the ViewModel exposes an immutable `*ViewState` via a `StateFlow`
  (`MutableStateFlow` private, `asStateFlow()` public). The screen reads it with
  `collectAsState()`.
- **One-off navigation events** — emitted through a `MutableSharedFlow<…Navigation>`
  (`asSharedFlow()`), collected once in a `LaunchedEffect` and turned into `navController.navigate(...)`.
  This keeps navigation out of the rendered state so it isn't replayed on recomposition.

Screens:

- `MainScreen` / `MainScreenViewModel` — demonstrates the nav-event pattern (four
  `navigateWith…OptionalArgs()` actions emit `MainScreenNavigation.SecondaryScreen`), requests
  `POST_NOTIFICATIONS` on Android 13+ via `accompanist-permissions`, and pings `AdManager`.
- `SecondaryScreen` / `SecondaryScreenViewModel` — reads its typed args from `SavedStateHandle`.
- `SettingsScreen` / `SettingsScreenViewModel` — Google status/data, app version & install source,
  and a System/Light/Dark theme selector. Uses `UiStateContent` with a working **Retry**.

### `MainRoot` & `MainRootViewModel`

`MainRoot` is the top-level composable. `MainRootViewModel` exposes a `themeMode: StateFlow<ThemeMode>`
(via `GetThemeModeUseCase().stateIn(...)`) and the banner ad unit id; `MainRoot` maps `ThemeMode`
(`SYSTEM`→`isSystemInDarkTheme()`, `LIGHT`/`DARK`) to a boolean and passes it to `TemplateTheme`,
then renders `ComposeAdView` + `MainNavGraph`.

### `UiState`

`presentation.ui.state.UiState<T>` is a sealed `Loading/Success/Error/Empty` with helpers
(`isLoading`, `dataOrNull`, `map`) and a `BaseResult.toUiState()` bridge. `UiStateContent(state, onRetry, success { })`
renders the matching `:design` state slot (`LoadingState`, `ErrorState`, `EmptyState`).

## Navigation (type-safe Compose)

- `framework/navigation/model/Route.kt` — a `sealed interface Route` of `@Serializable` destinations
  (`MainScreen`, `SecondaryScreen(text, textSecondary?, textTertiary?)`, `SettingsScreen`), plus
  `matchesCurrentEntry` / `findSelectedIndex` helpers for bottom-bar selection.
- `framework/navigation/MainNavGraph.kt` — a `Scaffold` with a Material 3 `NavigationBar`
  (Home / Secondary / Settings) over a `NavHost` whose `composable<Route.*>` entries resolve each
  ViewModel via `koinViewModel()`. Bottom-bar taps `navigate` with `popUpTo(startDestination)`,
  `launchSingleTop`, and `restoreState`.
- Screen-local navigation intents live in `…/model/*Navigation.kt` (e.g. `MainScreenNavigation`),
  which wrap a concrete `Route`.

## DI wiring

`framework.TemplateProjectApplication.onCreate()` runs `KoinLauncher().startKoin(this)`, then starts
the `ActivityProvider`, initializes ads (`AdManager.initAds()`), and `InitializeBack4App()`.

`KoinLauncher` registers three modules: `mainModule` (this module) + `networkingModule` (`:networking`)
+ `coreModule` (`:core`).

`di/koin/mainModule.kt` is where the **Android `:data` implementations are bound to `:domain`
interfaces**:

- **Networking** — `RetrofitFactory` (OkHttp timeouts, `RetryInterceptor`, a `headerProvider` auth
  seam, logging in debug) builds the `Retrofit` from `AppConfiguration(baseUrl = BuildConfig.ServerBaseUrl)`.
- **Preferences** — `AppPreferencesRepository` → `DataStoreAppPreferencesRepository` over a
  `DataStore<Preferences>` (`app_preferences`).
- **Observability** — `AnalyticsLogger`/`CrashReporter` bound to `NoOpAnalyticsLogger`/`NoOpCrashReporter`
  by default so the template runs with no `google-services.json`. Switching to Firebase is a documented
  one-line swap (see the comment in `mainModule`) to the `FirebaseAnalyticsLogger` / `FirebaseCrashReporter`
  impls.
- **Ads** — `AdManager` → `DefaultAdManager` (uses `ActivityProvider`, `GetInterstitialAdUnitId`);
  `GetMainAdUnitId` / `GetInterstitialAdUnitId` read the AdMob unit ids from `BuildConfig`.
- **App info / config** — `AppInfoRepository` → `DefaultAppInfoRepository`, `GetIsInstalledFromValidSource`,
  `VersionTextProvider`, `DispatcherProvider` → `DefaultDispatcherProvider`.
- **ViewModels** — `MainRootViewModel`, `MainScreenViewModel`, `SecondaryScreenViewModel`,
  `SettingsScreenViewModel` (use cases come from `coreModule`).

## `MainActivity` (edge-to-edge)

`MainActivity : ComponentActivity` calls `enableEdgeToEdge()` and `setContent { MainRoot(rememberNavController(), koinViewModel()) }`.
Inset handling lives in `MainRoot` (top + horizontal `safeDrawing`; the `NavigationBar` consumes the
bottom inset) and in `TemplateTheme` (status-bar icon contrast) — don't reintroduce
`accompanist-systemuicontroller`.

## Build flavors & types

Configured in `app/build.gradle.kts` (shared Android config comes from the
`templateproject.android.application[.compose]` convention plugins):

- **Flavors** (`version` dimension): `flavor1` (`.fl1`) / `flavor2` (`.fl2`).
- **Build types**: `debug` (no minify, `applicationIdSuffix .debug`, test ad ids) and `release`
  (minify + shrink, ProGuard, signing creds from env: `SIGNING_STORE_PASSWORD` / `SIGNING_KEY_ALIAS`
  / `SIGNING_KEY_PASSWORD`).
- `BuildConfig` fields: `ServerBaseUrl`, AdMob unit ids, `currentRevisionHash` (git short hash read
  lazily via `providers.exec` for config-cache safety), `ciBuildNumber`.
- `applicationId = com.jj.templateproject`. `DefaultAppInfoRepository` is provided per build type
  (`src/debug` and `src/release`).

```bash
./gradlew assembleFlavor1Debug       # build
./gradlew testFlavor1DebugUnitTest   # unit tests + Konsist
./gradlew :app:lintFlavor1Debug      # lint
```

## Where tests live

- `src/test/java/com/jj/templateproject/…` — JUnit5 unit tests with MockK, Turbine, coroutines-test,
  Koin-test, plus Robolectric Compose UI tests (`createAndroidComposeRule` via
  `util/ComposeComponentTest`, `BaseInstrumentedKoinTest`, `KoinTestRule`, `MainDispatcherExtension`).
  Covers ViewModels, screens (`*UiTest`), navigation (`MainNavGraphKtTest`, `NavigationBarTest`,
  `RouteTest`), `UiState`/`UiStateContent`, the DI graph (`di/KoinGraphTest`), networking
  (`RetrofitFactoryTest`, `RetryInterceptorTest`, `NetworkingIntegrationTest` with MockWebServer),
  DataStore prefs, ads, analytics, config and firebase utils.
- `src/test/java/konsist/KonsistTests.kt` — architecture/dependency-direction rules (run as unit tests).
- `src/androidTest/` — instrumented UI tests on a device/emulator
  (`./gradlew :app:connectedFlavor1DebugAndroidTest`). `AppFlowsUiTest` drives the real
  `MainNavGraph` (navigation, Main→Secondary args, Settings content, theme switching) against a
  hermetic Koin graph: `HermeticTestRunner` → `HermeticTestApplication` loads `testsupport/`
  fakes (fake repository + no-op ads). `src/androidTestFlavor1` / `androidTestFlavor2` hold the
  flavor-specific `ExampleInstrumentedTest`.
