# :core

Cross-cutting **platform glue** for TemplateProject: the Android implementations of platform-facing
domain interfaces, third-party SDK initialization, and the Koin module that wires it all together.

Depends on `:domain` (and nothing in the presentation layer), so these implementations can be reused
by any app branched from the template.

## What's here

| Piece | Role |
| --- | --- |
| `AndroidNotificationManager` | Android implementation of the domain `NotificationManager` |
| `InitializeBack4App` | One-shot Back4App/Parse SDK initialization |
| `coreModule` | Koin module registering the use cases + platform managers |

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

## coreModule (Koin)

`coreModule` is the DI entry point for this module. It registers the cross-cutting use cases and the
platform managers as singletons:

```kotlin
val coreModule = module {
    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
    single { GetThemeModeUseCase(appPreferencesRepository = get()) }
    single { SetThemeModeUseCase(appPreferencesRepository = get()) }
    single<NotificationManager> { AndroidNotificationManager(context = androidContext()) }
    single<InitializeBack4App> { InitializeBack4App(applicationContext = androidContext()) }
}
```

- **Use cases** — the google data/status use cases (`GetGoogleDataUseCase`, `GetGoogleStatusUseCase`)
  and the theme use cases (`GetThemeModeUseCase`, `SetThemeModeUseCase`), each depending on a
  repository interface from `:domain` resolved via `get()`.
- **Platform managers** — `AndroidNotificationManager` bound to the `NotificationManager` interface,
  and `InitializeBack4App`, both constructed with `androidContext()`.

Include `coreModule` in your Koin setup so these dependencies are available app-wide.
