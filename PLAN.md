# Template modernization plan

Status of the modernization pass on `chore/template-modernization` (branched from `develop`).
Each item below was implemented and the project was rebuilt/retested after every change.

## Done

### 1. Networking correctness & safety
- `TemplateService` now declares a real `getGoogleStatus` endpoint; previously
  `TemplateNetwork.getGoogleStatus()` reused `getGoogleData()` on the service.
- Both calls map `Response` → `BaseResult` through one shared `Response.toResult { }` helper
  (removed the `body()`-presence check that was wrong for `Unit` responses).
- `HttpLoggingInterceptor` logs `BODY` only in debug; `NONE` in release (no payloads in
  production logs).

### 2. Dependency rot removed
- Dropped the unused `kotlin-kapt` plugin (no annotation processors remain).
- Removed the abandoned `retrofit2-kotlin-coroutines-adapter` / `CoroutineCallAdapterFactory`
  (Retrofit 2.6+/3.x supports `suspend` natively).
- Swapped Gson for the official `converter-kotlinx-serialization` (single serialization stack).

### 3. UI: edge-to-edge
- Replaced the end-of-life `accompanist-systemuicontroller` with `enableEdgeToEdge()` +
  `WindowCompat`. `MainRoot` now insets content under the status bar; the bottom
  `NavigationBar` handles the navigation-bar inset.

### 4. Gradle build hygiene
- Moved the SonarQube plugin into the version catalog and bumped `4.2.1.3168` → `7.3.1.8318`.
- Git short hash now read via `providers.exec` (configuration-cache safe; `exec {}` was not).
- Enabled `org.gradle.caching`, `org.gradle.parallel`, `org.gradle.configuration-cache`.
- Aligned `compileSdk` to 36 across all modules (was a 34/35 mix); `targetSdk` stays 35.

### 5. build-logic convention plugins
- Added an included `build-logic` build with four convention plugins
  (`templateproject.android.application[.compose]`, `templateproject.android.library[.compose]`)
  that centralise compileSdk, minSdk, Java 17, the `JvmTarget` `compilerOptions` (replacing the
  deprecated `kotlinOptions` DSL), JUnit5 and Compose. Each module's build file shrank to its
  namespace + dependencies.

### 6. Cleanup & docs
- Collapsed the pass-through `MainNavGraph` wrapper.
- Replaced the bare `// TODO` permission-result callbacks (MainScreen **and** SettingsScreen)
  with a documented template seam.
- Stopped tracking `app/google-services.json` (project keys); added
  `google-services.json.example` and git-ignored the real file.
- Rewrote the README to point at the version catalog instead of a hand-maintained table.

### 7. Test infrastructure (found during verification)
- Added `junit-platform-launcher` to the test runtime. The unit-test task crashed before
  running any test (`OutputDirectoryProvider not available … unaligned launcher`) because
  junit-jupiter 5.13 needs a matching launcher. This was pre-existing on `develop`.
- Review follow-ups: added an explicit `androidx.core` dependency to `:design` (was relying on
  `WindowCompat` resolving transitively) and made the theme's Activity cast defensive.

## Verification
- `./gradlew :app:compileFlavor1DebugKotlin` — green after every commit.
- `./gradlew testFlavor1DebugUnitTest` — all unit tests + Konsist architecture checks pass.
- `./gradlew assembleFlavor1Debug` — APK builds.
- Configuration cache **stores and is reused** with no reported problems.
- An adversarial multi-agent review of the diff found no blockers/highs; its actionable
  low/nit findings were applied (items in §7).

## Follow-ups / out of scope (intentionally not changed)
- **Rotate the leaked Firebase API key.** The real `google-services.json` is now untracked,
  but the previous value remains in git history; rotate it in the Firebase console.
- **CI must have Android SDK Platform 36** installed for `compileSdk 36`.
- **`./gradlew sonar` + configuration cache:** sonar 7.3.1 supports it, but if a sonar run hits
  a cache problem, use `--no-configuration-cache` for that invocation.
- `toResult` maps HTTP errors but lets IO/network exceptions propagate (pre-existing). Consider
  wrapping calls in a `runCatching`-style boundary if you want network failures as `BaseResult`.
- `local.properties`’ `ciBuildNumber` is read with `java.io` at configuration time; fine today,
  but could be migrated to a `providers` API if it ever needs to invalidate the config cache.
- The Konsist test uses `hasValModifier`, deprecated in konsist 0.19.0 — update when bumping.
- Stale remote branches (`master`, `login_screen`, `lvl_library_integration`) could be pruned;
  not done here since it touches the remote.
- `targetSdk 36` deferred — do it in a dedicated pass (predictive back, large-screen layouts).
