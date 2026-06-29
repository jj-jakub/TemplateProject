# TemplateProject

A modern, multi-module **Android starter template**. It wires up what most apps need on day one —
Jetpack Compose UI, a Material 3 design system, dependency injection, networking, persistence,
theming, tests, static analysis and CI — behind an architecture that's **enforced as tests**, so you
can branch it and start building features immediately instead of plumbing.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jj-jakub_TemplateProject&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jj-jakub_TemplateProject)

> Read [`ARCHITECTURE.md`](ARCHITECTURE.md) for the design and the request lifecycle, and
> [`CONTRIBUTING.md`](CONTRIBUTING.md) for setup and the "add a feature" recipe. Every module also
> has its own `README`.

---

## Highlights

**Architecture & code patterns**
- Multi-module **clean architecture** with a one-way dependency direction that is **enforced by
  Konsist tests** (it fails the build if you break it).
- A single screen-state type, **`UiState<T>`** (Loading / Success / Error / Empty), plus
  `UiStateContent` that renders the matching UI — every screen handles async state the same way.
- A **`BaseResult`** result type with a functional toolkit (`map`, `flatMap`, `fold`, `getOrElse`, …).
- An injectable **`DispatcherProvider`** so coroutine code is deterministic in tests.

**UI**
- A complete **Material 3 design system** in `:design`: full light/dark color schemes, a typography
  scale, shape tokens, and **opt-in dynamic color** (Material You) with a brand fallback.
- A reusable **component library** (buttons, card, text, and loading/error/empty state views) with
  stable test tags, light+dark multipreviews, and a `ComponentCatalog` gallery.
- Jetpack Compose, **type-safe navigation**, edge-to-edge system bars, and **persisted theme
  switching** (System / Light / Dark).

**Data & platform**
- **Resilient networking** — Retrofit + kotlinx-serialization, a typed `NetworkError` hierarchy and a
  `safeApiCall` boundary (no crashes on bad networks), plus OkHttp timeouts, retries and an
  auth-header seam.
- **Jetpack DataStore** preferences behind a domain repository interface.
- **Observability seams** — `AnalyticsLogger` / `CrashReporter` interfaces (no-op by default,
  Firebase one line away). **Firebase** and **AdMob** are pre-wired.

**Quality & DX**
- **Koin** dependency injection with a verified DI graph test.
- A device-free test suite (**JUnit 5 + Robolectric + MockK + Turbine + MockWebServer**) plus an
  on-device **instrumented UI suite**.
- **Konsist** architecture rules + **Detekt** static analysis (with a clean baseline).
- **Localization-ready** — all UI strings externalized, with a sample Spanish (`values-es`) locale.
- **Gradle convention plugins** (`build-logic`) keep module build files tiny; all versions live in
  one catalog. **GitHub Actions** CI for build, tests and signed APK/AAB per flavor.

---

## Tech stack

Kotlin · Jetpack Compose · Material 3 · Koin · Retrofit 3 · kotlinx-serialization · Kotlin
Coroutines · Jetpack DataStore · Firebase (Analytics / Messaging / Crashlytics) · Google AdMob ·
Back4App/Parse · JUnit 5 · Robolectric · MockK · Turbine · MockWebServer · Konsist · Detekt.

Exact versions live in [`gradle/libs.versions.toml`](gradle/libs.versions.toml) — the single source
of truth, so nothing drifts out of date.

---

## Architecture

Five modules. `data` is a **layer** (the `com.jj.templateproject.data..` package), not a module — its
implementations live in `:app` and `:networking`.

```
                    ┌───────────────────────────────────────────┐
                    │                    :app                    │
                    │  Compose screens · ViewModels · navigation │
                    │  DI wiring · Android data implementations  │
                    └───────┬───────────┬───────────┬───────────┘
                            │           │           │
                    ┌───────▼─────┐ ┌───▼───┐ ┌─────▼──────┐
                    │ :networking │ │ :core │ │  :design   │
                    └───────┬─────┘ └───┬───┘ └────────────┘
                            │           │
                            └─────┬─────┘
                                  ▼
                              ┌────────┐
                              │:domain │   pure Kotlin — depends on nothing
                              └────────┘
```

| Module        | Responsibility |
|---------------|----------------|
| `:app`        | Presentation (Compose screens + ViewModels), type-safe navigation, DI wiring, and the Android-specific data implementations (Retrofit factory, DataStore, analytics, ads). |
| `:domain`     | Pure Kotlin core: use cases, repository interfaces, `BaseResult`/`NetworkError`, `DispatcherProvider`, `ThemeMode`. Depends on nothing. |
| `:networking` | Retrofit services and repository implementations; the `safeApiCall` boundary. |
| `:core`       | Cross-cutting platform glue (notifications, Back4App/Parse init). |
| `:design`     | The design system: color/type/shape tokens, theme, and the component library. |

**Enforced rules** (Konsist tests in `app/src/test/java/konsist`, run as part of the unit tests):

- Dependency direction: `domain` → nothing · `data` → `domain` · `presentation` → `domain` + `data`.
- Use cases are named `*UseCase` and live in the `domain` package.
- Each ViewModel has a single constructor of private dependencies.
- Screens read colors from `MaterialTheme`, never raw design-system color values.

---

## Project layout

```
TemplateProject/
├── app/          presentation, navigation, DI wiring, Android data impls, tests
├── domain/       use cases, repository interfaces, result/error & coroutine types
├── networking/   Retrofit services + repository implementations
├── core/         notifications, Back4App/Parse init
├── design/       Material 3 theme, tokens, and reusable components
├── build-logic/  Gradle convention plugins (shared module configuration)
├── config/detekt/ Detekt config + baseline
└── gradle/libs.versions.toml   single source of truth for versions
```

---

## How it fits together (the result flow)

1. A **use case** in `:domain` returns a `BaseResult<Data, NetworkError>`.
2. `:networking` produces it from Retrofit via `safeApiCall` (mapping HTTP errors and exceptions to
   typed `NetworkError`s).
3. A **ViewModel** turns the `BaseResult` into a `UiState<T>` and exposes it as a `StateFlow`.
4. The **screen** renders it with `UiStateContent { … }`, which shows the design-system loading,
   error (with retry) or empty view, or your success content.

The Settings screen is a complete, working example (including error → retry).

---

## Requirements

- **JDK 17+** (required to run Gradle).
- **Android SDK Platform 36** (`compileSdk 36`, `targetSdk 35`, `minSdk 23`).
- Android Studio (latest stable) recommended.

---

## Getting started

```bash
git clone https://github.com/jj-jakub/TemplateProject.git
cd TemplateProject
./gradlew assembleFlavor1Debug   # verifies your toolchain
```

The app **builds and runs without any secrets**. Optional setup:

1. **Firebase (optional).** The app runs fine without it (analytics/crash reporting default to
   no-op). To enable Firebase, copy the example and add your project values — the real file is
   git-ignored so keys aren't committed:
   ```bash
   cp app/google-services.json.example app/google-services.json
   ```
2. **`local.properties`.** Android Studio creates it with `sdk.dir`. CI also reads an optional
   `ciBuildNumber`.
3. **Make it yours.** Change the `applicationId`/`namespace` (`com.jj.templateproject`) and the
   `app_name` string, then re-brand the design system from the seed colors in `:design`.

---

## Build, test & quality

```bash
./gradlew assembleFlavor1Debug                   # build a debug variant
./gradlew testFlavor1DebugUnitTest               # unit tests + Konsist architecture checks
./gradlew :app:connectedFlavor1DebugAndroidTest  # instrumented UI tests (needs a device/emulator)
./gradlew :app:lintFlavor1Debug                  # Android Lint
./gradlew detekt                                 # static analysis (config + baseline in config/detekt)
./gradlew build sonar                            # full build + SonarCloud analysis (needs a token)
```

The unit, lint and Detekt checks run without a device. The Gradle **configuration cache** is on by
default (`gradle.properties`).

### Build variants

Two product flavors (`flavor1`, `flavor2`) × two build types (`debug`, `release`) — e.g.
`assembleFlavor1Release`. Each variant gets a distinct application id (suffixes `.fl1`/`.fl2`, and
`.debug` for debug), so they install side by side.

### Release signing

The `release` build type reads the keystore path and the `SIGNING_STORE_PASSWORD` /
`SIGNING_KEY_ALIAS` / `SIGNING_KEY_PASSWORD` credentials from environment variables (see
`app/build.gradle.kts`), so signing secrets stay out of the repo.

---

## Continuous integration

GitHub Actions (`.github/workflows`):

- **On every push / PR** — Android Lint, the full Gradle build, and the unit tests. SonarCloud runs
  only when a `SONAR_TOKEN` is configured and never blocks the build.
- **On demand** — instrumented UI tests on an emulator (`connectedCheck`).
- **On tag / dispatch** — signed release APK/AAB per flavor.

---

## Documentation

- [`ARCHITECTURE.md`](ARCHITECTURE.md) — modules, enforced rules, patterns, and the request lifecycle.
- [`CONTRIBUTING.md`](CONTRIBUTING.md) — setup, conventions, the "add a feature" recipe, and how to
  write device-free tests.
- Per-module READMEs: [`app`](app/README.md) · [`domain`](domain/README.md) ·
  [`networking`](networking/README.md) · [`core`](core/README.md) · [`design`](design/README.md).
