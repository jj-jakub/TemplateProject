# TemplateProject

A modern, multi-module Android starter that wires up the things most apps need on day
one — Compose UI, dependency injection, networking, theming, CI and an enforced
architecture — so a new app can be branched from it and built on immediately.

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jj-jakub_TemplateProject&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jj-jakub_TemplateProject)

## Highlights

- **Multi-module clean architecture** with an enforced dependency direction (see below).
- **Jetpack Compose + Material 3**, type-safe navigation and edge-to-edge system bars.
- **Koin** for dependency injection.
- **Retrofit + kotlinx-serialization** networking with a `BaseResult`/`BaseError` result type.
- **Firebase** (Analytics, Messaging, Crashlytics) and **AdMob** preconfigured.
- **Gradle convention plugins** (`build-logic`) so module build files stay tiny and versions
  live in a single catalog.
- **CI** via GitHub Actions (build, UI tests, signed APK/AAB per flavor).

## Modules & architecture

| Module        | Responsibility                                                        |
|---------------|-----------------------------------------------------------------------|
| `:app`        | Presentation (Compose screens, ViewModels), navigation, DI wiring.    |
| `:domain`     | Use cases, repository interfaces, result/error types. No dependencies.|
| `:networking` | Retrofit services and repository implementations.                     |
| `:core`       | Cross-cutting platform glue (notifications, Back4App init).           |
| `:design`     | Theme, colors, typography, shapes — the design system.                |

The layering is **enforced by Konsist tests** (`app/src/test/java/konsist`): `domain`
depends on nothing, `data` depends on `domain`, and `presentation` depends on `domain` and
`data`. Use cases must live in the `domain` package and ViewModels must expose a single
constructor of private dependencies.

## Versions

All dependency, plugin and SDK versions are declared in
[`gradle/libs.versions.toml`](gradle/libs.versions.toml) — that file is the single source of
truth (no hand-maintained version table here that can drift out of date). The shared Android
configuration (compileSdk, minSdk, Java/JVM target, JUnit5, Compose) lives in the
convention plugins under [`build-logic`](build-logic).

## Getting started

1. **Firebase config:** copy `app/google-services.json.example` to
   `app/google-services.json` and replace the placeholders with your own Firebase project
   values. The real file is git-ignored so project keys are not committed.
2. **`local.properties`:** Android Studio creates it with `sdk.dir`. CI also reads an
   optional `ciBuildNumber` from it.
3. **Rename the app:** change the `applicationId`/`namespace` (`com.jj.templateproject`) and
   the `app_name` string for your own app.
4. Open in Android Studio and run a `flavor1`/`flavor2` × `debug`/`release` variant.

### Release signing

The `release` build type reads its keystore path and the
`SIGNING_STORE_PASSWORD` / `SIGNING_KEY_ALIAS` / `SIGNING_KEY_PASSWORD` credentials from
environment variables (see `app/build.gradle.kts`), so secrets stay out of the repo.

## Build & test

```bash
./gradlew assembleFlavor1Debug      # build a variant
./gradlew testFlavor1DebugUnitTest  # unit tests + Konsist architecture checks
./gradlew build sonar               # full build + SonarCloud analysis
```

The configuration cache is enabled by default (`gradle.properties`).
