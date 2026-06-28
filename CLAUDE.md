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
- **Observability:** log via the `AnalyticsLogger` / `CrashReporter` domain interfaces — bound to
  NoOp by default; the Firebase impls are a one-line Koin swap (see `mainModule`).

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
./gradlew testFlavor1DebugUnitTest      # unit tests + Konsist architecture checks
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

## Setup gotchas

- `app/google-services.json` is **git-ignored**. Copy `app/google-services.json.example` and
  fill in real Firebase values before expecting Firebase to work.
- Release signing reads `SIGNING_STORE_PASSWORD` / `SIGNING_KEY_ALIAS` / `SIGNING_KEY_PASSWORD`
  and a keystore path from the environment (`app/build.gradle.kts`).
