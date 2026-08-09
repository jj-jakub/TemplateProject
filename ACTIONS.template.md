# ACTIONS.md — <app name>

> Copy this file to `ACTIONS.md`, which is gitignored on purpose. It is a working list, not
> documentation: it changes every session, it is written to yourself, and it is finished when it is
> empty.

Things only **you** can do, because they live in a web console or need an account. Everything in the
code is done, verified and committed; nothing below is waiting on more engineering.

Order by what blocks a release today. Say what happens if it is skipped, not just what to click, so
that a stale entry can be recognised as stale.

---

## Release day, in the order I would do it

**Console work first, all of it reversible, none of it needing a build.** List the items here that
change what a release can do afterwards, especially anything that becomes a lever only once it exists:
a kill switch you have never created cannot be used in an emergency.

**Then the release:** bump `versionCode`, tag, roll out to a fraction rather than everyone.

**One caveat worth holding.** Note here what this particular release changed all at once, and whether
that argues for a staged rollout. "Verified" and "verified together, in production, for the first
time" are different claims.

---

## Blocking a release today

### 1. Bump `versionCode`

`app/build.gradle.kts`. CI's `check-version-code` action fails the release job if it was not bumped
past the previous tag, so this is caught rather than discovered at upload, but it still has to be done.

### 2. <the next thing that stops a release>

What to click, where, and what breaks if it is skipped.

---

## Blocking a feature, but not the release

### 3. <console configuration a shipped feature is waiting on>

For example: creating the Remote Config keys the build already reads, at their defaults. The code
falls back to its in-code defaults until they exist, so nothing is broken — but no lever exists either.

---

## Monitoring, before you press publish

Register the custom metrics, dashboards or alerts that this round's new events report into. An event
nobody registered reports into a void, and the gap is only visible weeks later when someone asks a
question the data cannot answer.

---

## Nice to have

Everything that would improve things and blocks nothing. Keep it short, and delete an entry rather
than letting it rot: an ageing wish list makes the blocking entries above harder to see.
