# Contributing to TemplateProject

`TemplateProject` is a multi-module **starter template** targeting Android and iOS.
`:domain`/`:networking`/`:core`/`:design`/`:presentation` are all Kotlin Multiplatform (+ Compose
Multiplatform for `:design`/`:presentation`); `:app` (Android) and `iosApp` (Swift, xcodegen-generated)
are thin platform shells around `:presentation`, providing only the DI bindings and entry point each
platform needs. Compose Multiplatform + Material 3, Koin DI, Ktor + kotlinx-serialization. New apps
are branched from it, so prefer changes that keep it a clean, generic foundation over app-specific
features.

## Prerequisites

- **JDK 17+** (required to run Gradle).
- **Android SDK 36** (`compileSdk 36`, `targetSdk 35`, `minSdk 23`).
- Android Studio (latest stable) is recommended but not required.

## First-run setup

1. Clone the repo and open it in Android Studio (or use the Gradle wrapper from the CLI).
2. Firebase config is git-ignored. Copy the example and fill in real values **only if you need
   Firebase** — the template runs without it (analytics/crash reporting default to NoOp):
   ```bash
   cp app/google-services.json.example app/google-services.json
   ```
   When the file exists, the `google-services` plugin applies automatically (see
   `app/build.gradle.kts`). Register all four variant application ids in the Firebase console
   (`com.jj.templateproject.fl1(.debug)` and `.fl2(.debug)`) — the example has a client per variant —
   then switch the Koin `AnalyticsLogger`/`CrashReporter` bindings to the Firebase implementations.
3. Build to verify your toolchain:
   ```bash
   ./gradlew assembleFlavor1Debug
   ```

There are two product flavors (`flavor1`/`flavor2`) and two build types (`debug`/`release`).
Release minifies and reads signing creds from the environment, so day-to-day work uses
`Flavor1Debug`.

## Commands

```bash
./gradlew assembleFlavor1Debug             # build
./gradlew testFlavor1DebugUnitTest         # app unit tests + Konsist architecture checks
./gradlew :domain:allTests :networking:allTests :core:allTests :design:allTests :presentation:allTests
                                            # every multiplatform module's tests, Android AND iOS
./gradlew :app:assembleFlavor1Release      # the only check that R8 accepts the keep rules
./gradlew :app:connectedFlavor1DebugAndroidTest  # instrumented UI tests (needs a device/emulator)
./gradlew :app:lintFlavor1Debug            # Android lint
./gradlew detekt                           # static analysis
./gradlew detektBaseline                   # regenerate the detekt baseline
./gradlew build sonar                      # full build + SonarCloud (needs network/token)

# iOS, from a Mac (see ARCHITECTURE.md's "The iOS app" section):
cd iosApp && xcodegen generate
xcodebuild -project iosApp.xcodeproj -scheme iosApp \
  -destination "platform=iOS Simulator,name=<device>" build
```

Unit tests use **JUnit 5** (`useJUnitPlatform`); keep `junit-platform-launcher` on the test
runtime classpath or test discovery fails.

## Conventions that MUST hold

These are enforced by tooling — breaking them fails the build:

- **Versions live only in `gradle/libs.versions.toml`.** Never hardcode a version in a
  `build.gradle.kts`; reference the catalog.
- **Shared module config lives in convention plugins**, not in module build files. compileSdk,
  minSdk, Java 17, JUnit5, Compose, etc. are set in
  `build-logic/convention/src/main/kotlin/com/jj/templateproject/buildlogic/KotlinAndroid.kt` and
  `AndroidCompose.kt`, applied via `AndroidApplicationConventionPlugin.kt`,
  `AndroidLibraryConventionPlugin.kt`, and their `.compose` variants. A module's
  `build.gradle.kts` should only declare its convention plugin(s), `namespace`, and dependencies.
  To change e.g. compileSdk everywhere, edit the convention plugin.
- **Konsist architecture rules** (`app/src/test/java/konsist/KonsistTests.kt`) run as unit tests:
  - Dependency direction: `domain` → nothing, `data` → `domain`, `presentation` →
    `domain` + `data`.
  - Use cases (`*UseCase`) must reside in the `domain` package.
  - **Every `ViewModel` must have a single constructor whose parameters are all `private`.**
  - **Presentation must not import raw design color vals** — read colors from
    `MaterialTheme.colorScheme` so dark/dynamic theming works.
- **kotlinx-serialization** for JSON (no Gson). Networking runs on Ktor
  (`TemplateHttpClientFactory`), not Retrofit — every retry/timeout/logging concern is a client
  plugin, not a per-platform interceptor.
- **Edge-to-edge UI**: `MainActivity` calls `enableEdgeToEdge()`; `TemplateTheme` adjusts
  status-bar contrast via a `platformColorScheme`/`AdjustSystemBarAppearance` `expect`/`actual`
  (Android sets it via `WindowCompat`; iOS no-ops). Don't reintroduce `accompanist-systemuicontroller`.
- **Type-safe navigation**: routes are `Route` sealed types
  (`presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/navigation/model/Route.kt`),
  wired in `MainNavGraph.kt`, both shared by Android and iOS.
- **Threading**: inject `DispatcherProvider` and switch with
  `withContext(dispatcherProvider.io)` — don't hardcode `Dispatchers`.
- **i18n**: all user-facing strings go in `app/src/main/res/values/strings.xml` (a sample
  `values-es/strings.xml` ships).
- **Configuration cache is ON**: don't read env vars, run `exec {}`, or read files at
  configuration time outside `providers` APIs (see the `providers.exec` git-hash pattern in
  `app/build.gradle.kts`).

## Recipe: add a new feature/screen

End-to-end, following the layering. Example: a "profile" feature.

1. **Domain — use case + repository interface** (`:domain`, depends on nothing):
   - Interface: `domain/src/main/java/com/jj/templateproject/domain/profile/ProfileRepository.kt`
     returning `BaseResult<Data, NetworkError>`.
   - Use case: `domain/src/main/java/com/jj/templateproject/domain/profile/GetProfileUseCase.kt`
     (name must end in `UseCase`, must live in `domain`).
   - Reuse `BaseResult`/`BaseResultExt` operators (`fold`, `map`, `flatMap`, `onSuccess`, …)
     instead of unwrapping manually.

2. **Data — implementation** (`:networking` for remote, app `data` package for platform):
   - `:networking` is a multiplatform module now (`commonMain`/`androidMain`/`iosMain`); put
     shared logic in `commonMain` and only reach for `expect`/`actual` where a platform genuinely
     forces it (see `PlatformHttpClient.kt`/`NetworkTransportClassification.kt` for the pattern).
   - Service: add to / mirror
     `networking/src/commonMain/kotlin/com/jj/templateproject/data/google/service/TemplateService.kt`.
   - Repo impl:
     `networking/src/commonMain/kotlin/com/jj/templateproject/data/profile/DefaultProfileRepository.kt`,
     using `withContext(dispatcherProvider.io)` and mapping responses via `toResult`/
     `safeApiCall` from `networking/.../data/utils/NetworkUtils.kt`. Networking runs on Ktor
     (`TemplateHttpClientFactory`), not Retrofit; read that file's doc comments for why each
     plugin (retry, timeout, logging) is configured where it is.
   - Persistence instead goes through `AppPreferencesRepository`, whose two implementations live
     one per platform: `DataStoreAppPreferencesRepository` in `:app` (Android), `UserDefaultsAppPreferencesRepository`
     in `:core` (iOS — needs no per-app config, so it doesn't need an app-layer module to build it).

3. **Presentation — ViewModel exposing `UiState`** (`:presentation`, shared by both platforms):
   - `presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/ui/profile/ProfileScreenViewModel.kt`.
   - Single constructor, all params `private` (Konsist). Expose a `StateFlow<UiState<T>>`; bridge
     domain results with `BaseResult.toUiState()`
     (`presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/ui/state/UiState.kt`).

4. **Compose screen using design components + `UiStateContent`** (`:presentation`, shared):
   - `presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/ui/profile/ProfileScreen.kt`.
   - Render with `UiStateContent(state, onRetry = …) { data -> … }`
     (`.../presentation/ui/state/UiStateContent.kt`) so Loading/Error/Empty slots come from the
     design system.
   - Build UI from `design/src/commonMain/kotlin/com/jj/templateproject/design/components/` (`PrimaryButton`,
     `AppCard`, `SectionHeader`, `BodyText`, `ErrorState(onRetry)`, …). Colors come from
     `MaterialTheme.colorScheme` only. See `SettingsScreen.kt` for a complete working-Retry
     example. This screen renders unmodified on iOS — no platform branching needed unless it uses
     one of `:presentation`'s two `expect`/`actual` seams (`ComposeAdView`,
     `RequestNotificationPermissionOnLaunch`).

5. **Koin wiring** — register in the right module:
   - Platform-agnostic use cases: `core/src/commonMain/kotlin/com/jj/templateproject/core/di/coreModule.kt`.
   - Anything needing a platform context (`Context` on Android, nothing on iOS): the `expect fun
     platformCoreModule()` in that same package, with its `androidMain`/`iosMain` actuals. Add a
     new platform-specific singleton to the actual for the platform(s) it applies to; if it is
     Android-only with no iOS story yet (the way `InitializeBack4App` is), simply do not bind it
     in the iOS actual rather than inventing a stand-in.
   - Repo impls / networking: `networking/src/commonMain/kotlin/com/jj/templateproject/di/networkingModule.kt`.
   - The ViewModel itself: `presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/di/presentationModule.kt`
     (`viewModel { ProfileScreenViewModel(...) }`, Koin's multiplatform Compose ViewModel DSL).
   - A real dependency `presentationModule` needs but can't build itself (e.g. `AdUnitIds`,
     `AppVersionInfo`, `HttpClient`): bind it once per platform-owning module — `:app`'s
     `mainModule` (Android) and `IosKoin`'s `iosAppModule` (iOS, in `presentation/src/iosMain`).
     `KoinLauncher.kt` (Android) / `IosKoin.bootstrapKoin()` (iOS) assemble the final module list.

6. **Route** — add a `Route` entry in
   `presentation/src/commonMain/kotlin/com/jj/templateproject/presentation/navigation/model/Route.kt`
   and a `composable<Route.Profile> { … }` destination in `MainNavGraph.kt` — both shared, so this
   is the only navigation change either platform needs.

7. **Strings** — add labels to `presentation/src/commonMain/composeResources/values/strings.xml`
   (and translations, e.g. `values-es/strings.xml`) — Compose Multiplatform resources, not Android
   `res/values`, since this module renders on iOS too.

8. **Tests** — cover the use case, repo impl (`MockEngine`), ViewModel (Turbine), and screen
   (see below).

## Writing device-free tests

The whole suite runs without an emulator. Which tools are available depends on where the test
lives, because `:domain`/`:networking`/`:core`/`:design`/`:presentation` are multiplatform modules
and `:app` is Android-only by design (the Android shell, not shared code):

- **`commonTest`** (in any multiplatform module): `kotlin.test` only. No MockK, no Robolectric —
  neither is available on a non-JVM target. Fake a dependency by hand-writing a small class that
  implements the same interface (see `FakeTemplateRepository`/`FakeTemplateNetwork` in `:domain`/
  `:networking` for the house style) rather than reaching for a mocking library.
- **`androidUnitTest`** (in a multiplatform module) / `:app`'s `src/test` (JUnit5): Robolectric
  and MockK are both fine here, since the test only ever runs on the JVM. Note the JUnit runner
  differs: `:app` uses JUnit5 (`useJUnitPlatform()`), while a multiplatform module's
  `androidUnitTest` does not — write those with `kotlin.test` assertions plus plain
  `org.junit.Before`/`org.junit.After` (JUnit4-style) rather than JUnit5 annotations.
- **Coroutines/time**: extend with `MainDispatcherExtension`
  (`app/src/test/java/com/jj/templateproject/util/MainDispatcherExtension.kt`) and inject
  `TestDispatcherProvider`
  (`networking/src/commonTest/kotlin/com/jj/templateproject/data/TestDispatcherProvider.kt`) in
  place of the real `DispatcherProvider`.
- **Network**: `:networking`'s own commonTest suite uses Ktor's `MockEngine` (see
  `MockResponses.kt`) to test plugin behaviour without a real socket. `MockWebServer` (real OkHttp
  engine, real loopback socket) is reserved for the one `androidUnitTest` integration test
  (`NetworkingIntegrationTest.kt`) that exists specifically to catch an engine-wiring mistake
  `MockEngine` cannot see — reach for `MockEngine` first when adding a new endpoint's tests.
- **ViewModel**: assert `UiState` emissions with Turbine, from `:presentation`'s `commonTest` — it's
  a genuinely multiplatform library, so ViewModel tests run on both Android and `iosSimulatorArm64`.
- **Compose UI**: use `createAndroidComposeRule` against `ComponentActivity` (Robolectric shadow
  PackageManager). Extend the provided base classes:
  - design components: `design/src/androidUnitTest/kotlin/com/jj/templateproject/design/ComponentUiTest.kt`
  - app screens: `app/src/test/java/com/jj/templateproject/util/ComposeComponentTest.kt`
  - Koin-backed instrumented tests: `app/src/test/java/com/jj/templateproject/BaseInstrumentedKoinTest.kt`
  - Use `TestTags` from the design module for stable node lookups.
- **Architecture**: the Konsist tests in `app/src/test/java/konsist/KonsistTests.kt` run as part
  of `testFlavor1DebugUnitTest` — keep them green.

### Instrumented UI tests (on a device/emulator)

End-to-end UI flows run on a device via `./gradlew :app:connectedFlavor1DebugAndroidTest` (CI runs
this through `connectedCheck`). They use a hermetic harness so they stay deterministic and offline:

- `HermeticTestRunner` (set as `testInstrumentationRunner`) swaps in `HermeticTestApplication`,
  which starts Koin with `testOverrideModule` — a **fake network repository** and **no-op ads** —
  and skips Parse, so no real network/ads run.
- `AppFlowsUiTest` renders the real `MainNavGraph` directly (not `MainActivity`, so the AdMob
  banner's WebView can't block Compose's idle synchronization) and drives the actual navigation,
  Main→Secondary args, Settings content and theme switching.
- All instrumented test code lives in `app/src/androidTest/` (`testsupport/` holds the fakes).

Run everything locally before pushing:

```bash
./gradlew testFlavor1DebugUnitTest :app:lintFlavor1Debug detekt
```

## Commit & PR expectations

- Keep the template **generic** — avoid app-specific features that future branches would have to
  rip out.
- Make sure `testFlavor1DebugUnitTest` (incl. Konsist), every touched multiplatform module's
  `allTests` (Android **and** iOS), `:app:lintFlavor1Debug`, and `detekt` all pass. If you
  intentionally accept new detekt findings, regenerate the baseline with `./gradlew detektBaseline`
  and commit it.
- If you touched R8 keep rules or anything on the release path, run a real
  `:app:assembleFlavor1Release`: keep rules only fail when the shrinker actually runs.
- A regression test must be **seen failing** without its fix. Revert the fix, watch it go red, put it
  back. A test written after the fix and never seen failing proves nothing about the bug.
- One focused change per PR; explain the *why* and call out any new conventions or Koin/Route
  wiring.
- Don't commit `app/google-services.json` (git-ignored) or signing material.
- Touch versions only in `gradle/libs.versions.toml` and shared build config only in
  `build-logic/`.
