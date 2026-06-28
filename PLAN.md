# TemplateProject plan

This is a living document. The **current pass** is at the top; previous passes are kept below
as history. Every item is implemented as its own commit, and the project is rebuilt/retested
after each change (`./gradlew testFlavor1DebugUnitTest` + the relevant module test tasks).

---

# Pro enhancement pass (`feature/template-pro-enhancements`)

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
