# TemplateProject plan

This is a living document. The **current pass** is at the top; previous passes are kept below
as history. Every item is implemented as its own commit, and the project is rebuilt/retested
after each change (`./gradlew testFlavor1DebugUnitTest` + the relevant module test tasks).

---

# Kotlin Multiplatform conversion pass (`develop`)

Goal: turn the Android-only template into a genuine **Kotlin Multiplatform + Compose Multiplatform**
template targeting Android **and iOS**, roughly matching the shape of the author's other KMP
projects. Eight phases, each its own commit, each fully verified (compiles, tests pass, detekt
clean) before moving on.

## Roadmap & status

- [x] **A. Toolchain** — bumped to the KMP-capable trio: Kotlin 2.2.0 → 2.3.20, AGP 8.10.1 → 8.13.2,
  Gradle 8.14.3 → 8.14.5, plus detekt/Konsist bumps.
- [x] **B. `:domain` → KMP** — `src/main` → `commonMain`, `src/test` → `commonTest`. New
  `build-logic` convention plugin (`templateproject.kmp.library`). `DispatcherProvider` became
  `expect class DefaultDispatcherProvider()` (Android: `Dispatchers.IO`; iOS: `Dispatchers.Default`
  — `Dispatchers.IO` is `internal` on Kotlin/Native).
- [x] **C. `:networking` → Ktor** — replaced Retrofit + OkHttp with Ktor 3 (OkHttp engine on
  Android, Darwin engine on iOS). `TemplateHttpClientFactory` ports every interceptor to a Ktor
  client plugin (`HttpRequestRetry`/`HttpTimeout`/`ContentNegotiation`/`Logging`). New
  `expect fun classifyTransportFailure` — each platform's own transport-exception types, since no
  single multiplatform type safely means "unreachable" without swallowing unrelated failures.
- [x] **D. `:core` → KMP** — `coreModule` (shared) + `expect fun platformCoreModule()` split; real
  iOS actuals for `DeviceInfo`, `AppLifecycle`, `LaunchAttemptStore`, `ContentSharer`, `Clock`.
  `NotificationManager` is the domain `NoOp` on iOS; `InitializeBack4App` isn't bound there at all
  (the Parse SDK is Android-only).
- [x] **E. `:design` → Compose Multiplatform** — new `templateproject.kmp.library.compose`
  convention plugin. `PlatformTheme.kt`: `expect`/`actual` `platformColorScheme` (Android adds
  Material You; iOS always uses the brand palette) and `AdjustSystemBarAppearance` (Android sets
  status/nav-bar contrast; iOS no-ops). `@ThemePreviews`/`ComponentCatalog` stayed Android-only
  (tooling-only annotations, no CMP equivalent).
- [x] **F. `:presentation` — new shared KMP + CMP module** — the biggest phase: every screen,
  ViewModel, and the navigation graph moved out of `:app` into this new module. Koin bumped
  3.5.6 → 4.2.2 for `koin-compose-viewmodel` (the multiplatform Compose ViewModel DSL). New JetBrains
  multiplatform ports: `navigation-compose`, `lifecycle-viewmodel*` (frozen at 2.10.0 — 2.11.0's
  Android variant declares an AGP 9.1.0+ requirement this project doesn't meet yet). Compose
  Multiplatform resources replace `res/values` for this module's strings.
- [x] **G. `iosApp` — the real iOS app** — `:presentation` exports a static Kotlin/Native framework
  (`baseName = "Presentation"`). `iosApp/project.yml` (xcodegen, not a committed `.xcodeproj`),
  `iOSApp.swift` (calls `IosKoinKt.bootstrapKoin()` once at startup — named `bootstrapKoin`, not
  `initKoin`, since Kotlin/Native's Objective-C export renames any function starting with `init`),
  `ContentView.swift` (wraps `MainViewControllerKt.MainViewController()`). Getting the app to
  actually **launch** (not just build) surfaced several bindings the iOS Koin graph never had —
  `AppPreferencesRepository`, `HttpClient`/`DispatcherProvider`, `AppInfoRepository`,
  `AnalyticsLogger`/`CrashReporter` — each fixed with either a real iOS implementation
  (`UserDefaultsAppPreferencesRepository`, backed by `NSUserDefaults`) or a shared `:domain` default
  promoted out of `:app` (`NoOpAnalyticsLogger`/`NoOpCrashReporter`, `AlwaysInstalledFromValidSource`).
  New `IosKoinGraphTest` (`:presentation`'s `iosTest`) — the iOS counterpart of `:app`'s
  `KoinGraphTest` — closes the gap that let this ship silently in the first place: a Gradle compile
  or even an `xcodebuild` link success proves the Kotlin/Swift boundary compiles, not that the Koin
  graph behind it resolves. Verified end to end: the app installs, launches, and stays alive
  rendering the real shared Compose UI (navigation, bottom bar, Settings) on the iOS Simulator.
- [x] **H. CI + docs** — a new `iOS_framework_build` CI job (`xcodegen generate` + a real
  `xcodebuild` on a macOS runner — catches an `iosApp/project.yml` or Swift-side mistake a
  Gradle-only check would miss). Full doc pass: `README.md`, `ARCHITECTURE.md`, `CLAUDE.md`,
  `CONTRIBUTING.md`, `PUSH.md`, `docs/diagrams/module-graph.mmd`, and every per-module `README`
  (including a new `presentation/README.md`) rewritten for the six-module + `iosApp` graph — these
  had accumulated real staleness through phases C–F, deliberately deferred to this final phase since
  the module graph kept moving underneath them.

## Verification

Every phase: `./gradlew :<module>:allTests` (Android **and** `iosSimulatorArm64`),
`testFlavor1DebugUnitTest`, `detekt`, and (for `:app`-touching phases) a real
`:app:assembleFlavor1Release`. Phase G additionally verified `xcodebuild` for the simulator (not
just `compileKotlinIosSimulatorArm64`/`linkDebugFrameworkIosSimulatorArm64`), and an actual
install + launch on the iOS Simulator via `xcrun simctl`.

## Follow-ups / out of scope

- **No real ad SDK on iOS.** `ComposeAdView`'s iOS actual renders nothing; `AdManager` is bound to
  `NoOpAdManager`. Wiring up the Google Mobile Ads SDK's iOS framework needs a Kotlin/Native
  cinterop or a Swift-side bridge (the `AdBannerBridge` pattern in the author's other KMP apps).
- **No Firebase on iOS.** `AnalyticsLogger`/`CrashReporter` are bound to the shared `NoOp` pair.
  Firebase's iOS SDKs have no direct Kotlin/Native cinterop either — the same Swift-side-bridge
  shape as ads.
- **`material-icons-core`'s Compose Multiplatform release line isn't verified version-paired**
  with this project's CMP version, so `MainNavGraph`'s bottom nav bar stayed text-only rather than
  risk an unverified pairing. Worth revisiting once a paired release is confirmed.
- **No push notifications on iOS** — `PUSH.md`'s payload/routing layer is pure and already
  reachable from `:presentation`, but there's no `UNUserNotificationCenter`/APNs wiring yet.

---

# Previous pass — Pro enhancement pass (`feature/template-pro-enhancements`)

Goal: turn the template into a **top-class deliverable** — a developer who branches it inherits
a real design system, reusable state/result/dispatcher primitives, a resilient networking layer,
persisted preferences, an observability seam, localization-readiness, static analysis and docs.

Hard constraints respected throughout: Konsist layering (`domain` → nothing, `data` → `domain`,
`presentation` → `domain`+`data`; UseCases named `*UseCase` in `domain`; ViewModels = single
constructor of plain/`private val` params); all versions in `gradle/libs.versions.toml`; module
build files only declare convention plugins + namespace + deps; configuration-cache safe; JUnit5;
kotlinx-serialization; edge-to-edge; type-safe `Route` navigation; **stays a generic template**;
**builds and is unit-testable without a device** (Robolectric/JUnit5).

Sequenced so foundational primitives land before their consumers.

## Roadmap & status

### Design system (`:design`)
- [x] **1. Material 3 color scheme + real dark theme** — brand seed colors in `BaseColors`,
  full light/dark `ColorScheme`s (every M3 role), a genuinely dark dark-theme background.
- [x] **2. Material 3 typography scale** — complete type scale + a documented `FontFamily` seam.
- [x] **3. Shape token system** — `Shapes(extraSmall..extraLarge)`; screens reference `MaterialTheme.shapes`.
- [x] **4. Opt-in dynamic color (Material You)** — `dynamicColor` flag gated on API 31+, brand fallback.
- [x] **5. Reusable themed component library** — `PrimaryButton`/`SecondaryButton`, `AppCard`,
  `SectionHeader`/`BodyText`; 48dp min touch target; `testTag` seams; screens refactored to use them.
- [x] **6. Loading / Error / Empty state components + `TestTags`** — replaced the alpha-hacked
  Settings spinner with a real conditional `LoadingState`; `ErrorState` exposes `onRetry`.

### Core patterns
- [x] **7. `BaseResult` extension toolkit** — `map`/`mapError`/`fold`/`onSuccess`/`onError`/
  `getOrNull`/`getOrElse`/`flatMap`/`recover`; pure, fully unit-tested.
- [x] **8. `DispatcherProvider`** — injected so coroutine code is testable; networking repo uses it.
- [x] **9. Networking resilience** — richer `NetworkError` hierarchy, `safeApiCall` boundary
  (maps IO/serialization/HTTP exceptions), OkHttp timeouts + retry + auth-header interceptor seam.
- [x] **10. Generic `UiState<T>` + `UiStateContent`** — canonical screen-state type; Settings refactored
  with functional retry.

### Data, persistence & observability
- [x] **11. DataStore preferences repository** — `AppPreferencesRepository` interface in `domain`,
  DataStore-backed impl; unit-tested against a temp store.
- [x] **12. End-to-end theme switching** — `ThemeMode` + `Get/SetThemeModeUseCase` (domain) backed
  by DataStore; `MainRootViewModel` exposes it; `TemplateTheme` consumes it; Settings control.
- [x] **13. Analytics + CrashReporter abstraction** — domain interfaces, Firebase-backed impls,
  no-op default (bound by default; Firebase a one-line Koin swap); ad-click event wired.

### DX, tooling & docs
- [x] **14. String externalization + i18n sample** — moved hardcoded UI text to resources,
  marked translatable, added a sample `values-es`.
- [x] **15. Konsist guard rules + component multipreview/catalog** — new "no raw design colors in
  presentation" rule; `@ThemePreviews`; a design-system `ComponentCatalog`.
- [x] **16. Detekt static analysis** — root `detekt` task + config + baseline (kept out of `check`).
- [x] **17. Docs** — `ARCHITECTURE.md`, `CONTRIBUTING.md`, per-module READMEs; refreshed `README`/`CLAUDE.md`.
- [x] **18. Final adversarial review** — multi-agent review of the full diff; findings fixed; green build.

## Review outcome
A 6-lens adversarial review workflow (correctness, architecture/Konsist, Compose, concurrency,
tests, build) independently verified each finding. **No blockers or highs**; all hard constraints
held. The confirmed findings were all addressed:
- **Correctness:** OkHttp call-timeout (`InterruptedIOException`) was misclassified as Connectivity
  instead of Timeout — fixed + regression test.
- **i18n:** the Loading/Error/Empty slots used in Settings carried hardcoded English — `UiStateContent`
  now defaults its labels from string resources (added in `values` + `values-es`).
- **Tests:** added coverage for the Settings `retry()` flow, the `RetryInterceptor` through the real
  OkHttp stack (retry-on-IO, no-retry-on-5xx), and the Firebase analytics/crash adapter routing.
- **Hardening:** DataStore flows recover from read `IOException`s; `LightColorScheme`/`DarkColorScheme`
  made `internal` (type-enforces the no-raw-colors rule); theme switcher exposes `selected` semantics
  for TalkBack; preview wrapped in `TemplateTheme`; detekt frontend-lag noted.

## How this pass was planned
A multi-agent planning workflow proposed candidates from four lenses (UX/design, architecture/state,
data/observability, DX/tooling), then synthesized this dependency-ordered roadmap. Each item is
verified by its own JUnit5/Robolectric/Compose tests (no device required) and committed individually.

## Verification commands
```bash
./gradlew :app:testFlavor1DebugUnitTest         # app unit + UI (Robolectric) + Konsist
./gradlew :design:testDebugUnitTest             # design-system tests
./gradlew :domain:testDebugUnitTest             # domain tests
./gradlew :networking:testDebugUnitTest         # networking (MockWebServer) tests
./gradlew assembleFlavor1Debug                  # APK builds
```

## Follow-ups / TODO (next passes)

Tracked but intentionally out of scope for this pass.

### CI & tooling
- **Sonar + configuration cache (fixed).** The `sonar` step was failing with a Gradle
  configuration-cache error — the SonarQube plugin isn't configuration-cache compatible. Fixed by
  running that invocation with `--no-configuration-cache`. Remaining: confirm a clean SonarCloud
  run with a valid token, then optionally drop `continue-on-error` to make Sonar a hard gate. (If
  it still fails after this, check that *Automatic Analysis* is disabled on the SonarCloud project.)
- **Bump Detekt** once a release targeting the Kotlin 2.x frontend is stable (its bundled analysis
  frontend trails the project's Kotlin version).
- **Bump Konsist** (0.17.2 → current) and drop the deprecated `hasValModifier` usage in the rules.
- **Burn down the Detekt baseline** (5 accepted findings: one long composable + build-config magic
  numbers) instead of suppressing them.
- **Instrumented tests in CI (done).** Added an `Instrumented_tests` job to `automaticGradleBuild.yml`
  that runs `:app:connectedFlavor1DebugAndroidTest` on a macOS emulator with a cached AVD snapshot.
  It gates PRs and pushes to `develop`/`master`. `runUiTests.yml` remains the on-demand full-suite
  (`connectedCheck`, both flavors). The job is pinned to `macos-15-intel` because GitHub's Apple
  Silicon runners don't give the Android emulator a hypervisor (it never boots). **Revisit before
  ~Aug 2027**, when `macos-15-intel` (the last hosted x86_64 image) retires — at that point hosted
  emulator CI likely needs Firebase Test Lab, Gradle Managed Devices on a self-hosted ARM box, or a
  larger ARM runner that exposes virtualization.
- **CI run de-duplication (done).** The push/PR workflow used to fire twice per commit on a branch
  with an open PR (once for `push`, once for `pull_request`). `push` is now limited to
  `develop`/`master`; feature branches are covered through their PR, and a `concurrency` group
  cancels superseded runs.
- *(Optional)* split the branch into staged PRs (modernization → tests → enhancements).

### Security
- **Rotate the Firebase API key.** The real `google-services.json` is untracked now, but the old key
  is still in git history — rotate it in the Firebase console. (Carried over; still open.)

### Features / patterns (proposed during planning, not yet built)
- **`BaseViewModel` + one-off events** (`ObserveAsEvents`) + a root Snackbar host. This changes the
  Konsist ViewModel selector from `withParentOf(ViewModel)` to `withNameEndingWith("ViewModel")` —
  land both in the same commit so the single-private-constructor rule keeps matching.
- **Onboarding / first-run flow** that consumes the existing `onboardingCompleted` preference.
- **`ConnectivityObserver`** (online/offline) abstraction + Android implementation + a fake for tests.
- A shared **`:testing`** fixtures module / Koin override harness to cut per-module test duplication.

### Platform & cleanup
- Move to **`targetSdk 36`** in a dedicated pass (predictive back, large-screen/foldable layouts).
- Replace the remaining `// TODO` seams in `DefaultAdManager` (paid-event + backup-interstitial
  handling) with real behavior or clearer documentation.
- Repo hygiene: add a `LICENSE`, a PR template, and `dependabot.yml`.
- Decide whether `CLAUDE.md` (AI-assistant guidance) should ship in a customer-facing template, or
  be renamed/removed.

---

# Previous pass — Template modernization (`chore/template-modernization`)

Each item below was implemented and the project was rebuilt/retested after every change.

## Done

### 1. Networking correctness & safety
- `TemplateService` now declares a real `getGoogleStatus` endpoint; previously
  `TemplateNetwork.getGoogleStatus()` reused `getGoogleData()` on the service.
- Both calls map `Response` → `BaseResult` through one shared `Response.toResult { }` helper
  (removed the `body()`-presence check that was wrong for `Unit` responses).
- `HttpLoggingInterceptor` logs `BODY` only in debug; `NONE` in release (no payloads in
  production logs).

### 2. Dependency rot removed
- Dropped the unused `kotlin-kapt` plugin (no annotation processors remain).
- Removed the abandoned `retrofit2-kotlin-coroutines-adapter` / `CoroutineCallAdapterFactory`
  (Retrofit 2.6+/3.x supports `suspend` natively).
- Swapped Gson for the official `converter-kotlinx-serialization` (single serialization stack).

### 3. UI: edge-to-edge
- Replaced the end-of-life `accompanist-systemuicontroller` with `enableEdgeToEdge()` +
  `WindowCompat`. `MainRoot` now insets content under the status bar; the bottom
  `NavigationBar` handles the navigation-bar inset.

### 4. Gradle build hygiene
- Moved the SonarQube plugin into the version catalog and bumped `4.2.1.3168` → `7.3.1.8318`.
- Git short hash now read via `providers.exec` (configuration-cache safe; `exec {}` was not).
- Enabled `org.gradle.caching`, `org.gradle.parallel`, `org.gradle.configuration-cache`.
- Aligned `compileSdk` to 36 across all modules (was a 34/35 mix); `targetSdk` stays 35.

### 5. build-logic convention plugins
- Added an included `build-logic` build with four convention plugins
  (`templateproject.android.application[.compose]`, `templateproject.android.library[.compose]`)
  that centralise compileSdk, minSdk, Java 17, the `JvmTarget` `compilerOptions` (replacing the
  deprecated `kotlinOptions` DSL), JUnit5 and Compose. Each module's build file shrank to its
  namespace + dependencies.

### 6. Cleanup & docs
- Collapsed the pass-through `MainNavGraph` wrapper.
- Replaced the bare `// TODO` permission-result callbacks (MainScreen **and** SettingsScreen)
  with a documented template seam.
- Stopped tracking `app/google-services.json` (project keys); added
  `google-services.json.example` and git-ignored the real file.
- Rewrote the README to point at the version catalog instead of a hand-maintained table.

### 7. Test infrastructure (found during verification)
- Added `junit-platform-launcher` to the test runtime. The unit-test task crashed before
  running any test (`OutputDirectoryProvider not available … unaligned launcher`) because
  junit-jupiter 5.13 needs a matching launcher. This was pre-existing on `develop`.
- Review follow-ups: added an explicit `androidx.core` dependency to `:design` (was relying on
  `WindowCompat` resolving transitively) and made the theme's Activity cast defensive.
- Edge-to-edge polish: also adapt navigation-bar icon contrast (`isAppearanceLightNavigationBars`)
  and inset the top-level content for display cutouts (`safeDrawing.only(Horizontal + Top)`).

## Verification
- `./gradlew :app:compileFlavor1DebugKotlin` — green after every commit.
- `./gradlew testFlavor1DebugUnitTest` — all unit tests + Konsist architecture checks pass.
- `./gradlew assembleFlavor1Debug` — APK builds.
- Configuration cache **stores and is reused** with no reported problems.
- An adversarial multi-agent review of the diff found no blockers/highs; its actionable
  low/nit findings were applied (items in §7).

## Follow-ups / out of scope (intentionally not changed)
- **Rotate the leaked Firebase API key.** The real `google-services.json` is now untracked,
  but the previous value remains in git history; rotate it in the Firebase console.
- **CI must have Android SDK Platform 36** installed for `compileSdk 36`.
- **`./gradlew sonar` + configuration cache:** sonar 7.3.1 supports it, but if a sonar run hits
  a cache problem, use `--no-configuration-cache` for that invocation.
- `toResult` maps HTTP errors but lets IO/network exceptions propagate (pre-existing). Consider
  wrapping calls in a `runCatching`-style boundary if you want network failures as `BaseResult`.
  → **addressed in the enhancement pass (item 9).**
- `local.properties`’ `ciBuildNumber` is read with `java.io` at configuration time; fine today,
  but could be migrated to a `providers` API if it ever needs to invalidate the config cache.
- The Konsist test uses `hasValModifier`, deprecated in konsist 0.19.0 — update when bumping.
- Stale remote branches (`master`, `login_screen`, `lvl_library_integration`) could be pruned;
  not done here since it touches the remote.
- `targetSdk 36` deferred — do it in a dedicated pass (predictive back, large-screen layouts).
