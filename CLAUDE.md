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
reside in the `domain` package; ViewModels must have a single constructor of private deps.
If you add/restructure code, keep these rules satisfied (they run as unit tests).

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
./gradlew build sonar                   # full build + SonarCloud (needs network/token)
```

Java 17+ is required to run Gradle here. Unit tests use **JUnit 5** (`useJUnitPlatform`);
`junit-platform-launcher` must stay on the test runtime classpath or test discovery fails.

## Conventions

- Networking maps Retrofit `Response` to `BaseResult` via `Response.toResult { }` in
  `networking/.../data/utils/NetworkUtils.kt`; serialization is **kotlinx-serialization**
  (no Gson). Retrofit `suspend` functions are used directly (no call adapter).
- UI is edge-to-edge: `MainActivity` calls `enableEdgeToEdge()`, `TemplateTheme` adjusts
  status-bar icon contrast via `WindowCompat`, and screens handle insets (don't reintroduce
  `accompanist-systemuicontroller`).
- Navigation is type-safe Compose nav with `Route` sealed types.

## Setup gotchas

- `app/google-services.json` is **git-ignored**. Copy `app/google-services.json.example` and
  fill in real Firebase values before expecting Firebase to work.
- Release signing reads `SIGNING_STORE_PASSWORD` / `SIGNING_KEY_ALIAS` / `SIGNING_KEY_PASSWORD`
  and a keystore path from the environment (`app/build.gradle.kts`).
