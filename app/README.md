# `:app` — Android composition root

The Android application module. It is deliberately thin: every screen, ViewModel, and navigation
graph lives in `:presentation` (a shared Kotlin Multiplatform + Compose Multiplatform module — see
`presentation/README.md`) so it renders unmodified on iOS too. `:app`'s own job is everything only
an Android app target can provide: the Android framework entry points (`Application`, `Activity`),
the Android-specific `:data` implementations (Retrofit's replacement `HttpClient`, DataStore,
Firebase, AdMob, Back4App), and the DI wiring that assembles all of it together. It depends on
`:presentation`, `:domain`, `:networking`, `:core`, and `:design`.

For the iOS equivalent of everything below (`IosKoin.kt`, `MainViewController()`), see
`presentation/README.md`'s iOS section and `iosApp/project.yml`.

## Layout

```
framework/      TemplateProjectApplication, MainActivity
di/koin/        mainModule, KoinLauncher
data/           Android-bound impls: network, preferences, analytics, ad, app, config, firebase
```

## DI wiring

`framework.TemplateProjectApplication.onCreate()` runs `KoinLauncher().startKoin(this)`, checks
`LaunchStability.beginLaunch()` (falls back to safe mode after repeated crashed launches — see
`CLAUDE.md`'s reliability notes), then starts the `ActivityProvider`, initializes ads
(`AdManager.initAds()`), `InitializeBack4App()`, creates the push notification channel, and
registers for FCM (`PushRegistrar`).

`KoinLauncher` assembles five modules: `mainModule` (this module) + `networkingModule`
(`:networking`) + `coreModule` + `platformCoreModule()` (`:core`) + `presentationModule`
(`:presentation`).

`di/koin/mainModule.kt` is where the **Android `:data` implementations are bound to `:domain`
interfaces** — everything only `:app` can provide, chiefly because it is the one module with a real
`BuildConfig` and a real `Context`/`Application`:

- **Networking** — `TemplateHttpClientFactory.create(baseUrl = get<AppConfiguration>().baseUrl, logBody = BuildProfile.isDebugBuild)`
  builds the `HttpClient` (`:networking`'s Ktor factory — see `networking/README.md`) from
  `AppConfiguration(baseUrl = BuildConfig.ServerBaseUrl)`. `DispatcherProvider` is also bound here
  (`DefaultDispatcherProvider()`), since `:networking` only ever consumes it via `get()`.
- **Preferences** — `AppPreferencesRepository` → `DataStoreAppPreferencesRepository` over a
  `DataStore<Preferences>` (`app_preferences`). iOS's counterpart
  (`UserDefaultsAppPreferencesRepository`) lives in `:core` instead — see `core/README.md`.
- **Observability** — `AnalyticsLogger`/`CrashReporter` bound via `AnalyticsFactory`, which falls
  back to `:domain`'s `NoOpAnalyticsLogger`/`NoOpCrashReporter` (the same pair iOS binds) unless the
  build is one a real user could be running **and** a `google-services.json` is present. `RemoteFlags`
  (Firebase Remote Config, an override-only layer) is Android-only for now.
- **Ads** — `AdManager` → `DefaultAdManager` (uses `ActivityProvider`, `GetInterstitialAdUnitId`);
  `AdUnitIds` → `AndroidAdUnitIds` reading the AdMob unit ids from `BuildConfig`.
- **App info / config** — `AppInfoRepository` → `DefaultAppInfoRepository` (per build type, `src/debug`
  and `src/release`, both unconditionally `true` today — see `domain/README.md`'s note on
  `AlwaysInstalledFromValidSource`), `AppVersionInfo` → `AndroidAppVersionInfo` (reads `BuildConfig`).

## `MainActivity` (edge-to-edge)

`MainActivity : ComponentActivity`, `singleTop` (a notification tap or deep link that arrives while
the app is already open comes through `onNewIntent`, not a fresh `onCreate`). Calls
`enableEdgeToEdge()` and `setContent { MainRoot(rememberNavController(), koinViewModel(), pushDestination, onPushDestinationHandled) }`
— `MainRoot` itself lives in `:presentation`. Resolves a notification tap or `templateproject://`
deep link into a `PushDestination` (`PushIntents.destinationOf` / `PushDeepLink.parse`, both
untrusted input — an unrecognised value parses to `null` and nothing happens) and holds it as state
until `MainRoot`'s push-handling effect consumes it. `onResume()` calls `LaunchStability.markStable()`
— deliberately not in `onCreate()`, so a crash while composing the first screen still counts as a
failed launch. Inset handling lives in `MainRoot` (top + horizontal `safeDrawing`; the
`NavigationBar` consumes the bottom inset) and in `TemplateTheme` (status-bar icon contrast) — don't
reintroduce `accompanist-systemuicontroller`.

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
  Covers the DI graph (`di/KoinGraphTest` — the Android counterpart of `:presentation`'s
  `IosKoinGraphTest`), the Android-only data impls (DataStore prefs, analytics, ads, config,
  firebase utils), navigation smoke tests, and app-level screen integration.
- `src/test/java/konsist/KonsistTests.kt` — architecture/dependency-direction rules (run as unit
  tests; scans the whole multi-module project, not just `:app`, via `Konsist.scopeFromProject()`).
- `src/androidTest/` — instrumented UI tests on a device/emulator
  (`./gradlew :app:connectedFlavor1DebugAndroidTest`). `AppFlowsUiTest` drives the real
  `MainNavGraph` (navigation, Main→Secondary args, Settings content, theme switching) against a
  hermetic Koin graph: `HermeticTestRunner` → `HermeticTestApplication` loads `testsupport/`
  fakes (fake repository + no-op ads). `src/androidTestFlavor1` / `androidTestFlavor2` hold the
  flavor-specific `ExampleInstrumentedTest`.
