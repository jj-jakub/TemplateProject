# :core

Cross-cutting **platform glue** for TemplateProject: the per-platform implementations of
platform-facing domain interfaces, third-party SDK initialization, and the Koin modules that wire it
all together.

A multiplatform module (Android + iOS). Depends on `:domain` (and nothing in the presentation
layer), so these implementations can be reused by any app branched from the template.

## What's here

| Piece | Role |
| --- | --- |
| `AndroidNotificationManager` | Android implementation of the domain `NotificationManager` |
| `InitializeBack4App` | One-shot Back4App/Parse SDK initialization (Android only) |
| `UserDefaultsAppPreferencesRepository` | iOS implementation of the domain `AppPreferencesRepository`, backed by `NSUserDefaults` |
| `coreModule` | Koin module registering the use cases and everything else needing no platform |
| `platformCoreModule()` | Koin module registering the platform capabilities, one actual per target |

`AppPreferencesRepository`'s two implementations don't live in the same place: Android's
(`DataStoreAppPreferencesRepository`) is in `:app`, since it needs a `Context` to build its
`DataStore<Preferences>`. iOS's lives here in `:core` instead, because `NSUserDefaults.standardUserDefaults`
needs no per-app configuration to construct — there's nothing app-specific for an app-layer module
to inject. `NSUserDefaults` has no observation API of its own (unlike DataStore's `Flow`), so each
preference is mirrored into a `MutableStateFlow` seeded from the store at construction and updated
on every write.

## AndroidNotificationManager

Implements `com.jj.templateproject.domain.notifications.NotificationManager`, keeping the presentation
and domain layers free of Android framework types. It builds and posts a system push notification:

- `showPushNotification(title, body, intent)` constructs a `NotificationCompat.Builder`
  (big-text style, default sound, auto-cancel) and attaches a `PendingIntent` for the tap action.
- On API 26+ (`Build.VERSION_CODES.O`) it lazily creates the notification channel before posting.

Channel/ID constants live in `PushMessageKeys` (e.g. `PUSH_NOTIFICATION_CHANNEL_ID`,
`PUSH_NOTIFICATION_CHANNEL_TITLE`, `PUSH_NOTIFICATION_ID`).

## InitializeBack4App (Parse)

Wraps Back4App/Parse setup behind a single callable seam:

```kotlin
class InitializeBack4App(private val applicationContext: Context) {
    operator fun invoke() { /* Parse.initialize(...) */ }
}
```

`invoke()` calls `Parse.initialize(...)` with the application id, client key, and server URL read
from string resources (`R.string.back4app_app_id`, `back4app_client_key`, `back4app_server_url`),
then sends a test `ParseObject` to confirm connectivity. Call it once from application startup.

## coreModule + platformCoreModule (Koin)

The DI entry point is two modules, always used together:

```kotlin
modules(coreModule, platformCoreModule())
```

- **`coreModule`** (commonMain) registers everything that needs nothing from the platform: the google
  data/status use cases (`GetGoogleDataUseCase`, `GetGoogleStatusUseCase`), the theme use cases
  (`GetThemeModeUseCase`, `SetThemeModeUseCase`) and `LaunchStability`, each taking a `:domain`
  interface resolved via `get()`.
- **`platformCoreModule()`** is `expect`/`actual`, one per target, and registers the capabilities only
  a platform can answer: `NotificationManager`, `LaunchAttemptStore`, `Clock`, `DeviceInfo`,
  `ContentSharer` and `AppLifecycle`. Android's also binds `InitializeBack4App` and reads every
  constructor argument from `androidContext()`; iOS's needs no context at all, omits
  `InitializeBack4App` (the Parse SDK is Android-only), and binds the domain's
  `NoOpNotificationManager` until a real `UNUserNotificationCenter` implementation exists.

That split is why `commonMain` depends on `koin-core` rather than `koin-android`: `androidContext()`
lives in the Android artifact, and only the Android actual ever calls it.
