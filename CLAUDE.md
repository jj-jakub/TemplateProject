# CLAUDE.md

Guidance for working in this repository.

## What this is

`TemplateProject` is a multi-module Android **starter template** (Jetpack Compose, Koin,
Retrofit). New apps are branched from it, so prefer changes that keep it a clean, generic
foundation rather than adding app-specific features.

## Modules & architecture

- `:app` — presentation (Compose screens + ViewModels), navigation, DI wiring (`di/koin`).
- `:domain` — use cases, repository interfaces, `BaseResult`/`BaseError`. Depends on nothing.
- `:networking` — Retrofit services + repository implementations.
- `:core` — platform glue (notifications, Back4App/Parse init).
- `:design` — theme, colors, typography, shapes.

The dependency direction is **enforced by Konsist** in `app/src/test/java/konsist/KonsistTests.kt`:
`domain` → nothing, `data` → `domain`, `presentation` → `domain` + `data`. Use cases must
reside in the `domain` package; ViewModels must have a single constructor of private deps;
**presentation must not import raw design color vals** (read `MaterialTheme.colorScheme`).
If you add/restructure code, keep these rules satisfied (they run as unit tests).

See `ARCHITECTURE.md` for the full design and `CONTRIBUTING.md` for the "add a feature" recipe;
each module has its own `README`.

## Core patterns (use these, don't reinvent)

- **Results:** domain returns `BaseResult<Data, Err : BaseError>`; use the inline operators in
  `domain/.../BaseResultExt.kt` (`fold`/`map`/`mapError`/`flatMap`/`onSuccess`/`onError`/
  `getOrNull`/`getOrElse`/`recover`) instead of unwrapping by hand.
- **Screen state:** model it with `UiState<T>` (`presentation/ui/state/UiState.kt`) — Loading/
  Success/Error/Empty — and render it with `UiStateContent(state, onRetry) { … }`. Bridge domain
  results with `BaseResult.toUiState()`. `SettingsScreen` is the worked example (with retry).
- **Threading:** inject `DispatcherProvider` (`domain/.../coroutines/`) and switch with
  `withContext(dispatcherProvider.io)`; never hardcode `Dispatchers`.
- **Design system (`:design`):** compose screens from `design/.../components/` (`PrimaryButton`,
  `SecondaryButton`, `AppCard`, `SectionHeader`, `BodyText`, `LoadingState`, `ErrorState`,
  `EmptyState`). Colors/typography/shapes come from `MaterialTheme.*`; the palette is in
  `ColorTokens`/`ColorSchemes`, re-brand from the seeds in `BaseColors`. Use `TestTags` for test
  hooks and `@ThemePreviews` for light+dark previews; see `ComponentCatalog`.
- **Theme:** `ThemeMode` (System/Light/Dark) is persisted via `AppPreferencesRepository` (DataStore)
  and applied through `MainRootViewModel.themeMode` → `MainRoot` → `TemplateTheme`. `TemplateTheme`
  defaults to dynamic color on API 31+ (`dynamicColor = false` to brand-lock).
- **Preferences:** add prefs to `AppPreferencesRepository` (domain interface) + its DataStore impl.
- **Observability:** log via the `AnalyticsLogger` / `CrashReporter` domain interfaces.
  `AnalyticsFactory` decides between Firebase and NoOp; don't bind them by hand. Numbers go through
  the `logEvent(name, params, metrics)` overload — a metric sent as a string can be counted but never
  summed or averaged.
- **Who reports:** `BuildProfile.isReportingBuild` is the one place that answers "could a real user be
  running this". Held on two layers: the factory picks NoOp reporters, and `src/debug/AndroidManifest.xml`
  turns the SDKs' own collection off. New reporting keys off this, never off `BuildConfig.DEBUG` alone.
- **Platform seams:** a platform capability is an interface in `:domain`, an implementation in `:core`,
  and **a double beside the interface** (`FixedClock`, `FixedDeviceInfo`, `FakeAppLifecycle`,
  `NoOpContentSharer`, `NoOpNotificationManager`, `NoOpRemoteFlags`). That rule is why these need no
  device and no mocking framework in tests. Follow it for anything new; `KoinGraphTest` asserts each
  binding resolves, which is the only place a missing one shows up.
- **Time:** inject `Clock`. `nowMillis()` for a value that means something outside this process,
  `elapsedMillis()` for **every** duration — a stopwatch built on wall-clock time reports a jump,
  negative ones included, the moment the clock is corrected mid-measure.
- **Device class:** `DeviceInfo.isTablet` reads `smallestScreenWidthDp`, so it is stable for the
  hardware. Ask the window instead when the answer should follow the space actually available.
- **Remote config:** `RemoteFlags`, and only for values re-evaluated on every decision or for kill
  switches over a gate that already exists in exactly one place. Defaults are the in-code constants at
  the call site, never a second copy in a console.
- **Push:** `domain/push` parses and routes (pure, unit-tested), `core/data/notifications` builds the
  notification. Campaigns are **data-only**; read `PUSH.md` before touching the payload or the channel.
- **Startup:** `LaunchStability` banks an attempt before anything reads persisted state and reports
  `LaunchMode.SAFE` after two failed launches. Anything that rehydrates state at startup should
  consult it and skip once. Nothing on the launch path may fail loudly.

## Build configuration (important)

- **Versions** live only in `gradle/libs.versions.toml`. Don't hardcode versions in build files.
- **Shared module config** (compileSdk 36, minSdk 23, Java 17, `JvmTarget` via `compilerOptions`,
  JUnit5, Compose) lives in **convention plugins** under `build-logic/`. A module's
  `build.gradle.kts` should only declare its convention plugin(s), `namespace`, and dependencies.
  To change e.g. compileSdk for all modules, edit
  `build-logic/convention/src/main/kotlin/com/jj/templateproject/buildlogic/KotlinAndroid.kt`.
- The root `build.gradle.kts` declares AGP/Kotlin/Compose plugins with `apply false` so the
  convention plugins can apply them by id — keep them there.
- The **configuration cache is on** (`gradle.properties`). Don't read env vars / run `exec {}` /
  read files at configuration time outside of `providers` APIs (see the `providers.exec`
  git-hash pattern in `app/build.gradle.kts`).

## Commands

```bash
./gradlew assembleFlavor1Debug          # build (flavors: flavor1/flavor2)
./gradlew testFlavor1DebugUnitTest      # app unit tests + Konsist architecture checks
./gradlew :domain:test :core:test       # the pure and platform-layer tests
./gradlew :app:assembleFlavor1Release   # the only check that R8 accepts the keep rules
./gradlew :app:lintFlavor1Debug         # lint
./gradlew detekt                        # static analysis (config + baseline in config/detekt)
./gradlew detektBaseline                # regenerate the detekt baseline after accepted changes
./gradlew build sonar                   # full build + SonarCloud (needs network/token)
```

Java 17+ is required to run Gradle here. Unit tests use **JUnit 5** (`useJUnitPlatform`);
`junit-platform-launcher` must stay on the test runtime classpath or test discovery fails.
Detekt is a **root task** (not wired into `check`), so the unit-test loop stays fast; run it
explicitly. Its bundled Kotlin frontend trails the project's `kotlin` version (detekt 1.23.x ships
the Kotlin 2.0 analysis API vs. the project's 2.2) — harmless today, but bump detekt when a build
targeting the Kotlin 2.x frontend is stable if you start using 2.2-only syntax. Compose tests run
device-free via Robolectric + `createAndroidComposeRule` (base classes register `ComponentActivity`
with the shadow `PackageManager`).

## Conventions

- Networking maps Retrofit `Response` and thrown exceptions to `BaseResult` via `toResult { }` and
  `safeApiCall { }` in `networking/.../data/utils/NetworkUtils.kt`; failures become a typed
  `NetworkError` (Http/Connectivity/Timeout/Serialization/Unknown). `RetrofitFactory` (in `:app`)
  sets OkHttp timeouts, a `RetryInterceptor` and a `headerProvider` auth seam. Serialization is
  **kotlinx-serialization** (no Gson); Retrofit `suspend` functions are used directly (no call adapter).
- UI is edge-to-edge: `MainActivity` calls `enableEdgeToEdge()`, `TemplateTheme` adjusts
  status-bar icon contrast via `WindowCompat`, and screens handle insets (don't reintroduce
  `accompanist-systemuicontroller`).
- Navigation is type-safe Compose nav with `Route` sealed types.

## Working conventions

- **Verify what you changed, per change.** `./gradlew testFlavor1DebugUnitTest :core:test :domain:test`
  plus `:app:lintFlavor1Debug` and `detekt`. Anything touching R8 rules or the release path also needs
  a real `:app:assembleFlavor1Release`, since keep rules only fail when the shrinker actually runs.
- **One focused commit per change**, with the *why* in the message. Unrelated changes go in separate
  commits even when they were made in the same sitting.
- **A regression test must be shown to fail without its fix.** Revert the fix, watch the test go red,
  put it back. A test written after the fix that was never seen failing proves nothing about the bug.
- **Verify status against the source, not against a plan document.** `PLAN.md` records intent and goes
  stale; the code and the git history are what actually happened.
- **New behaviour ships with its test in the same commit.** Pure logic belongs in `:domain` where it
  can be tested without a device, which is most of the reason the layering exists.

## Release process

- `ACTIONS.md` (gitignored, copy `ACTIONS.template.md`) holds the console work only a human can do,
  ordered by what blocks a release. Everything in the code is finished; that file is what is left.
- `SmokeTestRunInput.md` drives an agent through a device walk before a release: every screen
  captured, deep links exercised, push and the analytics gate checked, and a written verdict.
- A release is a `v*` tag. CI refuses to build one whose `versionCode` did not move.

## Setup gotchas

- `app/google-services.json` is **git-ignored**. Copy `app/google-services.json.example` and fill
  in real Firebase values to enable Firebase. The `google-services` **and Crashlytics** plugins are
  applied **conditionally** in `app/build.gradle.kts` — only when that file exists — because both
  hard-fail without one, so the template builds without either. The config must contain a client for
  each variant id (`.fl1`/`.fl2` + `.debug`). Nothing else has to be switched on: `AnalyticsFactory`
  and `FirebaseRemoteFlags.create` pick the real implementations as soon as Firebase initializes on a
  build that reports.
- Release signing is opt-in through the environment: `SIGNING_STORE_FILE` (a path) plus
  `SIGNING_STORE_PASSWORD` / `SIGNING_KEY_ALIAS` / `SIGNING_KEY_PASSWORD`. With none set the release
  build comes out **unsigned** rather than failing, which is what lets a fresh clone build one.
- `android:allowBackup` is **off**, deliberately, with both rule files (`backup_rules.xml` for
  pre-31, `data_extraction_rules.xml` for 31+) already written and referenced. Turning backup on is a
  one-word change; the rules that keep per-install state from travelling are already correct.
