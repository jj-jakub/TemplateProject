# Push notifications (Firebase Cloud Messaging)

Campaigns are sent to a **topic**, arrive as **data-only** messages, and are drawn by the app itself
so they can carry up to three action buttons. Each button goes to one of the destinations the app
declares.

The parsing and routing layers are platform-free (`domain/.../push`) and unit-tested; the Android
half is `core/.../data/notifications` (building the notification and its pending intents) plus
`app/.../data/firebase` (receiving the message, joining the topic).

## The payload

Every field is a string, in the message's `data` map. Anything missing, unknown or malformed is
dropped rather than failing the message; a message without both `title` and `body` is not shown at
all.

| Key | Required | Meaning |
| --- | --- | --- |
| `title` | yes | Notification title. |
| `body` | yes | Notification text (expands to multiple lines). |
| `tap` | no | Where tapping the notification body goes. Defaults to `home`. |
| `action1_label` … `action3_label` | no | Button text. |
| `action1_target` … `action3_target` | no | Where that button goes. |
| `campaign` | no | Names the campaign, so an event logged for this notification can carry it. |
| `slot` | no | `0`–`99`, default `0`. Two campaigns with different slots sit side by side in the shade; re-sending the same slot replaces the earlier notification. |

A button needs **both** its label and a valid target, or it is dropped. Actions past the third are
ignored: Android renders at most three.

### Destinations

| Token | Goes to |
| --- | --- |
| `home` | The main screen. |
| `settings` | The settings screen. |
| `play:<package>` | That package's Play Store listing, e.g. `play:com.example.companion`. |

`play:` targets are validated as package names before they are used, since the value ends up inside a
URL. Add a destination in `domain/.../push/PushDestination.kt` and name its screen in
`app/.../framework/navigation/PushRoutes.kt`; the mapping is exhaustive, so a new destination will
not compile until a screen is chosen for it.

## Sending

Data-only messages are **not** sendable from the Firebase console's Notifications composer: it always
attaches a `notification` block, which the SDK itself draws when the app is backgrounded, without our
action buttons and without ever calling our code. Use the HTTP v1 API:

```bash
ACCESS_TOKEN=$(gcloud auth print-access-token)   # or a service-account token

curl -X POST \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  https://fcm.googleapis.com/v1/projects/<firebase-project-id>/messages:send \
  -d '{
    "message": {
      "topic": "all",
      "android": { "priority": "high" },
      "data": {
        "title": "Version 2 is out",
        "body": "Faster sync, and the export bug is fixed.",
        "tap": "settings",
        "campaign": "v2_launch",
        "slot": "1",
        "action1_label": "Open settings",
        "action1_target": "settings",
        "action2_label": "Not now",
        "action2_target": "home"
      }
    }
  }'
```

`"priority": "high"` matters for data-only messages: without it, delivery to a dozing device is
deferred, sometimes for hours.

To aim a message at one device while testing, swap `"topic": "all"` for `"token": "<device token>"`.
A debug build prints its own token on every launch:

```bash
adb logcat -s push
```

## In-app messages

An in-app message composed in a console has no send API, and its custom data is a single set for the
whole message, so it could name one destination at most. Each button carries **its own action URL**,
which is the only per-button channel it has, so the destination travels as a link:

| Button should open | Action URL |
| --- | --- |
| The main screen | `templateproject://home` |
| Settings | `templateproject://settings` |
| Another app's listing | `templateproject://play/com.example.companion` |

`MainActivity` claims that scheme and routes the link through the same destinations a notification
tap uses. The scheme is browsable, so any web page can send one: every link is parsed as untrusted
input and anything unrecognised routes nowhere.

## Audience

Every install subscribes to the `all` topic at launch (`PushRegistrar.register`, idempotent, so an
install predating a topic still joins it). Debug builds subscribe too, which is what makes a test send
reach your own device. Note that this is unlike analytics, which only a build a real user could be
running reports at all (see `BuildProfile`).

## Permission and the channel

Android 13+ denies notifications until the user grants them; the app requests `POST_NOTIFICATIONS`
through the permission flow on the main screen. Worth knowing when you tune that: the OS shows the
dialog exactly once ever, so asking on the very first launch spends it on someone with no reason yet
to say yes.

Notifications use the `announcements` channel, created **IMPORTANCE_HIGH** with an explicit sound and
vibration, which is what makes one appear as a heads-up banner rather than sliding silently into the
shade.

A channel's importance is fixed when it is created: `createNotificationChannel` cannot raise an
existing one, only the user can change it (downward) in system settings. So changing it means a new
channel id and deleting the old one. Verify with:

```bash
adb shell dumpsys notification --noredact | grep -a "mId='announcements'"   # expect mImportance=4
```

## Where the code lives

| Piece | File |
| --- | --- |
| Destinations, payload parsing, deep links | `domain/.../push/` (pure, unit-tested) |
| Notification building | `core/.../data/notifications/AndroidNotificationManager.kt` |
| Pending intents | `core/.../data/notifications/PushIntents.kt` |
| Message receipt | `app/.../data/firebase/DefaultFirebaseMessagingService.kt` |
| Topic + token | `app/.../data/firebase/PushRegistrar.kt` |
| Navigation on tap | `app/.../framework/navigation/PushRoutes.kt`, `MainActivity`, `MainRoot` |
