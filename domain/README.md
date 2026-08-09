# :domain

Pure-Kotlin module at the center of the architecture, and a genuine Kotlin Multiplatform module
(`commonMain` + `androidMain` + `iosMain`). It defines **what** the app does (use cases, contracts,
result types) without knowing **how** anything is implemented, or on which platform it's running.

## Purpose

- Depends on **nothing** else in the project — no `:app`, `:networking`, `:core`, `:design`, or
  `:presentation`.
- No Android or iOS framework dependencies in `commonMain`. `androidMain`/`iosMain` exist only for
  the rare type an interface's contract genuinely needs an `expect`/`actual` for (see
  `DispatcherProvider` below) — everything else in this module is plain, portable Kotlin.
- Other layers depend on `:domain`; the dependency direction is enforced by Konsist.

## Contents

### Use cases (`*UseCase`)
- `GetGoogleDataUseCase`
- `GetGoogleStatusUseCase`
- `GetThemeModeUseCase`
- `SetThemeModeUseCase`
- `GetIsInstalledFromValidSource`

### Repository interfaces
- `TemplateRepository`
- `AppInfoRepository` — ships an `AlwaysInstalledFromValidSource` default (both Android build
  types' real implementation is unconditionally `true` today too; iOS binds this same default
  rather than a third copy of the same placeholder).
- `AppPreferencesRepository`

Implementations live in other modules (`:networking`, `:app`, `:core`) and are wired via Koin.

### Result types
- `BaseResult<Data, Err : BaseError>` — sealed `Success` / `Error`.
- `BaseResultExt` functional operators: `fold`, `map`, `mapError`, `flatMap`, `onSuccess`,
  `onError`, `getOrNull`, `errorOrNull`, `getOrElse`, `getOrDefault`, `recover`,
  `isSuccess` / `isError`.

### Threading
- `DispatcherProvider` interface + `expect class DefaultDispatcherProvider()`. Repositories and use
  cases inject this instead of referencing `Dispatchers` directly, which keeps them testable. The
  Android actual uses `Dispatchers.IO`; the iOS actual maps to `Dispatchers.Default`, since
  `Dispatchers.IO` is `internal` on Kotlin/Native (nothing on iOS's Kotlin side parks a thread on a
  blocking syscall the way `IO` exists for).

### Domain models & errors
- `ThemeMode` enum — `SYSTEM` / `LIGHT` / `DARK`.
- `NetworkError` sealed — `Http` / `Connectivity` / `Timeout` / `Serialization` / `Unknown`,
  each carrying a non-null `message`.

### Observability & platform contracts
- `AnalyticsLogger`, `CrashReporter`, `NotificationManager` interfaces. The domain declares these
  seams; concrete (Firebase / Android / NoOp) implementations live in outer modules.
- `NoOpAnalyticsLogger` / `NoOpCrashReporter` ship here (not in `:app`), since both Android's
  `AnalyticsFactory` fallback and iOS's `IosKoin` bind the exact same no-op pair.

### Push
- `PushDestination`, `PushPayload`, `PushRequestCodes`, `PushDeepLink` — the pure layer that parses
  an FCM payload into an in-app destination or an outbound intent. See `PUSH.md`.

### Ad
- `AdManager` interface + `NoOpAdManager` (iOS has no ad SDK wired up yet); `AdUnitIds`,
  `GetMainAdUnitId`, `GetInterstitialAdUnitId`.

### App
- `AppVersionInfo` interface (Android reads `BuildConfig`, iOS reads `NSBundle.mainBundle`).

## Rules (enforced by Konsist)

- Use cases must be named with the `*UseCase` suffix.
- Use cases must reside in the `domain` package.
- No dependencies on other project modules (Konsist's architecture assertion:
  `domain.dependsOnNothing()`).

## Why kotlinx-coroutines is allowed

`kotlinx-coroutines` is the **only** external dependency. It is permitted because `Flow` and the
dispatcher abstraction (`DispatcherProvider`) are part of the domain's own vocabulary for expressing
asynchronous contracts — it is a genuinely multiplatform, pure-Kotlin library with no platform or
infrastructure coupling, so it does not compromise the module's independence.
