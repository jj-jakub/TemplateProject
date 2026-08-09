package com.jj.templateproject.core.data.lifecycle

import com.jj.templateproject.domain.lifecycle.AppLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSNotificationName
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidEnterBackgroundNotification
import platform.UIKit.UIApplicationState
import platform.UIKit.UIApplicationWillEnterForegroundNotification

/**
 * Tracks the foreground state of the whole app, via `NSNotificationCenter`.
 *
 * **Which pair of notifications, and why.** UIKit offers two candidate pairings, and they mean
 * different things:
 *
 * - `didBecomeActive` / `willResignActive` is the *active* state. It flips for anything that takes
 *   event delivery away for a moment: an incoming call, Control Centre pulled down, the app
 *   switcher opened and dismissed. That is the analogue of Android's `onResume`/`onPause`.
 * - `willEnterForeground` / `didEnterBackground` is the *foreground* state, and flips only when the
 *   app actually leaves or returns to the screen.
 *
 * The second pair is what this observes, because it is what `ProcessLifecycleOwner`'s
 * `onStart`/`onStop` mean on Android: pulling down the notification shade there does not stop an
 * activity either, so the two platforms answer the same question the same way.
 *
 * The initial value has to be read rather than assumed, because `willEnterForeground` does **not**
 * fire on a cold launch, only when returning from the background: seeded from `applicationState`,
 * this reports an app that is plainly on screen as being on screen, which is the same property the
 * Android side gets from `ProcessLifecycleOwner` replaying its current state on registration.
 *
 * Reading `UIApplication.sharedApplication` is a main-thread call, so like the Android
 * implementation this must be constructed during startup rather than lazily from a screen.
 *
 * The observers are never removed: this is a process-lifetime singleton, so there is no point at
 * which unregistering would be correct, and the notification centre holds them until the process
 * ends.
 */
class NotificationCenterAppLifecycle : AppLifecycle {

    private val state = MutableStateFlow(
        UIApplication.sharedApplication.applicationState !=
            UIApplicationState.UIApplicationStateBackground,
    )

    override val isInForeground: StateFlow<Boolean> = state.asStateFlow()

    init {
        observe(UIApplicationWillEnterForegroundNotification, inForeground = true)
        observe(UIApplicationDidEnterBackgroundNotification, inForeground = false)
    }

    // Delivered on the main queue, so the state a UI collector observes changes on the thread that
    // collector is already on.
    private fun observe(notification: NSNotificationName, inForeground: Boolean) {
        NSNotificationCenter.defaultCenter.addObserverForName(
            name = notification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { state.value = inForeground }
    }
}
