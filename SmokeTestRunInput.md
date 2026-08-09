# SmokeTestRunInput

Input for an agent-driven release smoke test. Walk every screen on a real device, capture a named
screenshot of each, exercise the wiring that only breaks at runtime (push, deep links, the analytics
gate), and write a pass/fail report.

**How to start a run:** fill in RUN CONFIG below, then say
*"Run the release smoke test from SmokeTestRunInput.md"*. Everything else in this file is instructions
to the agent, not to you.

Unit tests cover the pure layers and Konsist covers the module graph. This covers what neither can:
that the app installs, opens, draws, and that the parts wired to a platform service actually reach it.

---

## RUN CONFIG

The only block you edit. Values marked `auto` are resolved during preflight.

```yaml
variant:      flavor1Debug       # flavor1Debug | flavor2Debug | flavor1Release
package:      auto               # resolved from the variant (com.jj.templateproject.fl1[.debug])
device:       auto               # adb serial; auto = the only connected device, else ask
legs:         [screens, logscan, deeplinks]
artifacts:    ~/Desktop/smoke/TemplateProject/<versionName>_<versionCode>
baseline:     auto               # previous run's directory to diff against; none = skip diffing
fcm_key:                         # path to a Firebase service-account json, outside this repo
fcm_project:                     # Firebase project id, for the fcm leg
allow_data_wipe: false           # if false, never `pm clear`; the run adapts instead
```

### Why a debug variant is the default

- `BuildConfig.DEBUG` holds, so the app prints its FCM token on every launch. A release build never
  prints one, so the `fcm` leg cannot run there.
- Analytics and Crashlytics collection are off in a debug build (`BuildProfile.isReportingBuild` plus
  the manifest flags in `src/debug`), so the run's own events never reach the dashboards that describe
  shipped builds.
- It installs as `…​.debug`, side by side with anything else on the device.

The cost: it certifies nothing about R8. Anything that only breaks after shrinking needs a release
variant, which is what the `release` leg below is for.

### Legs

| Leg | What it does | Needs |
| --- | --- | --- |
| `screens` | The screen walk plus screenshots. The core of the run. | device |
| `logscan` | Crash, ANR and `FATAL` scan across the run; checks the analytics gate held. | device |
| `deeplinks` | Every `templateproject://` destination, plus a rejected one. | device |
| `fcm` | Real token-targeted push sends, action buttons, tap routing. | debug variant + `fcm_key` |
| `release` | Repeats the walk on a release build, which is the only R8 evidence there is. | release variant |
| `safemode` | Two killed launches, then confirm the third reports safe mode. | device |
| `diff` | Pixel-diffs every shot against `baseline` and flags what changed. | a previous run |

---

## Naming

```
<app>_<screen>_<versionName>_<versionCode>.png
templateproject_settings_0.1_1.png
```

`<screen>` is the lowercased route name (`main`, `secondary`, `settings`). Sub-states take a suffix:
`settings_scrolled`, `main_error`. A dialog that belongs to no route is named for itself
(`permission_prompt`).

Files land in `artifacts`. Never commit them.

---

## Preflight

1. `adb devices -l`. One device: use it. Several: ask which. None: stop and say so.
2. Build and install: `./gradlew :app:install<Variant>`.
3. Resolve the identity from the **installed package**, never from `build.gradle.kts`:
   ```bash
   adb shell dumpsys package <pkg> | grep -E "versionName|versionCode=" | head -2
   ```
   A run that reports the version it meant to install rather than the one it did is worthless.
4. `adb shell pm grant <pkg> android.permission.POST_NOTIFICATIONS`.
5. **Force-stop the sibling builds now**, not later:
   ```bash
   adb shell am force-stop com.jj.templateproject.fl1.debug
   adb shell am force-stop com.jj.templateproject.fl2.debug
   adb shell service call statusbar 2   # collapse the shade
   ```
   The two flavors render an identical UI, so a screenshot from the wrong one cannot be spotted by eye.
6. Create the artifacts directory. If it already holds shots for this exact versionName+versionCode,
   warn: either the version was not bumped or this is a re-run.
7. Start a **filtered, continuous** log capture in the background:
   ```bash
   adb logcat -c
   adb logcat -v time AndroidRuntime:E ActivityManager:W push:D FA:V "*:S" > smoke_logcat.txt
   ```
   An unfiltered read is useless: Compose logs several lines per frame, so at 120Hz the ring buffer
   evicts everything older than seconds.
8. **Guard every tap and capture on the foreground package**: assert that
   `adb shell dumpsys activity activities | grep topResumedActivity` names `<pkg>`, and abort loudly if
   not. This is what stops another app's screen from being filed as release evidence.

---

## How to tap

The walk taps the app the way a user would. Reaching a screen through its own navigation is itself the
thing under test.

1. **Never tap coordinates read off a screenshot.** Dump the tree first, every time:
   `adb shell uiautomator dump /sdcard/ui.xml && adb shell cat /sdcard/ui.xml`. Find the node by its
   `text`, tap the centre of its `bounds`.
2. **Only text is reliably addressable, and `clickable` is a lie.** Compose exposes no resource ids and
   reports most nodes as `clickable="false"`, so an icon-only control cannot be found by attribute.
   Reach such controls by their content description if the app sets one, and record what could not be
   reached rather than guessing at coordinates.
3. **Match text exactly before falling back to a substring.** Compose sometimes exposes a merged parent
   whose text concatenates its children's, and it comes first in the tree, so a substring search can
   tap the middle of a whole row.
4. **Wait for the transition before capturing**, about 1.5 s. A shot taken mid-transition catches two
   screens blended together and is worthless as evidence.
5. **A label that repeats must be disambiguated**, either by its container (find the card first, then
   the control inside its bounds) or by index.
6. **Use the system back key to leave a screen**, but **never at the root**: it finishes the activity
   and reveals whatever is behind ours. To return to a known state, force-stop and relaunch.

---

## The walk

One pass. Reach the screen, wait, capture, move on.

| # | Reach it by | Shot |
| --- | --- | --- |
| 1 | Cold start. Capture whatever the first launch shows, permission prompt included, before dismissing it. | `permission_prompt` (if shown), `main` |
| 2 | The bottom bar's second tab. | `secondary` |
| 3 | The bottom bar's settings tab, then scroll to the end. | `settings`, `settings_scrolled` |
| 4 | In Settings, switch the theme to Dark, then return to the first tab. | `main_dark` |
| 5 | Switch the theme back to System. | — |
| 6 | Whatever the app's primary action is (the network call on the main screen). Capture success, then enable airplane mode and repeat to capture the error state. | `main_loaded`, `main_error` |
| 7 | Turn airplane mode off, retry, and confirm it recovers. | `main_recovered` |

### What counts as a failure

Per screen, only the obvious: it did not open, it drew blank or half-drawn, a control the screen is
supposed to have is missing from the dump, or the log gained a crash while it was up. Beyond that:

- `settings` shows the version matching the installed package.
- `main_error` shows the error state with a retry, not a blank screen or a crash.
- The theme switch actually recolors: `main` and `main_dark` must differ.

---

## The deeplinks leg

Every destination the app declares, plus one it must refuse. Nothing here should ever crash: an
unrecognised link routes nowhere by design.

```bash
adb shell am start -a android.intent.action.VIEW -d "templateproject://home"                    <pkg>
adb shell am start -a android.intent.action.VIEW -d "templateproject://settings"                <pkg>
adb shell am start -a android.intent.action.VIEW -d "templateproject://play/com.example.app"    <pkg>
# Must do nothing but open the app normally:
adb shell am start -a android.intent.action.VIEW -d "templateproject://admin"                   <pkg>
adb shell am start -a android.intent.action.VIEW -d "templateproject://play/https://example.com" <pkg>
```

Send each twice: once cold (app killed) and once while it is already open, which is the `onNewIntent`
path and the one that regresses silently.

---

## The fcm leg

Needs `fcm_key` and `fcm_project` in RUN CONFIG, and a service-account json that lives **outside this
repo**. See PUSH.md for the payload and a working send.

1. Read the device token from the log capture: `grep "device token" smoke_logcat.txt`. A debug build
   prints one on every launch.
2. Send a campaign with three action buttons, aimed at that token rather than at the topic.
3. Capture the notification in the shade. Assert: title and body both present, the body expanded to
   more than one line, three buttons, a status-bar icon that is a recognisable glyph rather than a
   solid blob.
4. Tap each button in turn, cold and warm, and record where it landed. **This is the leg's real
   subject**: pending intents that share a request code all open the first destination, and that
   failure is invisible in a screenshot of the notification itself.
5. Send a second campaign with the same `slot`, then a third with a different one. The same slot must
   replace, a different slot must sit alongside.
6. Send a deliberately broken payload (no `body`, an unknown `tap`, an `action1_label` with no target).
   Nothing must be shown for the first, and the others must degrade rather than fail.

---

## The safemode leg

Confirms the launch-stability counter does what it claims, which nothing else can observe.

1. Launch, then `am force-stop` before the app reaches its first frame. Repeat, so two launches in a
   row never reached stability.
2. Launch normally. `LaunchStability` must report SAFE for that launch, and the Crashlytics breadcrumb
   must say so.
3. Let it settle, then launch once more: it must be back to NORMAL.

---

## The logscan leg

Read `smoke_logcat.txt` at the end and report:

- Any `FATAL EXCEPTION`, `ANR in <pkg>`, or `AndroidRuntime: E` naming our package. Each one fails the
  run outright.
- Any `ActivityManager: W` about our package (a slow start, a killed service).
- **The analytics gate.** On a debug variant there must be **no** Firebase Analytics upload traffic at
  all. `FA:V` lines describing an upload mean the gate leaked, which is a failure even though nothing
  visibly broke.

---

## The report

Write `report.md` beside the screenshots. It is the deliverable, not the screenshots.

1. **Verdict first.** Pass, pass-with-findings, or fail, in one line.
2. **The identity.** Package, versionName, versionCode, variant, device, Android version, run date.
   Read from the device, not from the build files.
3. **Per leg**, a table: what was attempted, what happened, pass or fail.
4. **Findings**, most severe first. Each one: what was seen, the exact reproduction, and the artifact
   that shows it.
5. **What was not covered**, explicitly. A leg that did not run, a screen that could not be reached, a
   control that could not be addressed. An unstated gap reads as a pass.

Never soften a finding to make a run look clean, and never file a screenshot from another app or
another build as evidence.
