# :domain

Pure-Kotlin module at the center of the architecture. It defines **what** the app does
(use cases, contracts, result types) without knowing **how** anything is implemented.

## Purpose

- Depends on **nothing** in the project — no `:app`, `:data`, `:networking`, `:core`, or `:design`.
- No Android dependencies. This module is plain Kotlin, so its types and rules stay portable
  and testable in isolation.
- Other layers depend on `:domain`; the dependency direction is enforced by Konsist.

## Contents

### Use cases (`*UseCase`)
- `GetGoogleDataUseCase`
- `GetGoogleStatusUseCase`
- `GetThemeModeUseCase`
- `SetThemeModeUseCase`

### Repository interfaces
- `TemplateRepository`
- `AppInfoRepository`
- `AppPreferencesRepository`

Implementations live in other modules (`:networking`, `:app`) and are wired via Koin.

### Result types
- `BaseResult<Data, Err : BaseError>` — sealed `Success` / `Error`.
- `BaseResultExt` functional operators: `fold`, `map`, `mapError`, `flatMap`, `onSuccess`,
  `onError`, `getOrNull`, `errorOrNull`, `getOrElse`, `getOrDefault`, `recover`,
  `isSuccess` / `isError`.

### Threading
- `DispatcherProvider` interface + `DefaultDispatcherProvider`. Repositories and use cases
  inject this instead of referencing `Dispatchers` directly, which keeps them testable.

### Domain models & errors
- `ThemeMode` enum — `SYSTEM` / `LIGHT` / `DARK`.
- `NetworkError` sealed — `Http` / `Connectivity` / `Timeout` / `Serialization` / `Unknown`,
  each carrying a non-null `message`.

### Observability & platform contracts
- `AnalyticsLogger`, `CrashReporter`, `NotificationManager` interfaces. The domain declares
  these seams; concrete (Firebase / Android / NoOp) implementations live in outer modules.

## Rules (enforced by Konsist)

- Use cases must be named with the `*UseCase` suffix.
- Use cases must reside in the `domain` package.
- No Android dependencies and no dependencies on other project modules.

## Why kotlinx-coroutines is allowed

`kotlinx-coroutines` is the **only** external dependency. It is permitted because `Flow` and
the dispatcher abstraction (`DispatcherProvider`) are part of the domain's own vocabulary for
expressing asynchronous contracts — it is a pure-Kotlin library with no Android or
infrastructure coupling, so it does not compromise the module's independence.
