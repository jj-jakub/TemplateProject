package com.jj.templateproject.core.di

import com.jj.templateproject.core.data.device.IosDeviceInfo
import com.jj.templateproject.core.data.lifecycle.NotificationCenterAppLifecycle
import com.jj.templateproject.core.data.preferences.UserDefaultsAppPreferencesRepository
import com.jj.templateproject.core.data.reliability.UserDefaultsLaunchAttemptStore
import com.jj.templateproject.core.data.review.UserDefaultsReviewPromptStore
import com.jj.templateproject.core.data.sharing.IosContentSharer
import com.jj.templateproject.core.data.time.IosSystemClock
import com.jj.templateproject.domain.device.DeviceInfo
import com.jj.templateproject.domain.lifecycle.AppLifecycle
import com.jj.templateproject.domain.notifications.NoOpNotificationManager
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.reliability.LaunchAttemptStore
import com.jj.templateproject.domain.review.ReviewPromptStore
import com.jj.templateproject.domain.sharing.ContentSharer
import com.jj.templateproject.domain.time.Clock
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Nothing here takes a context: UIKit and Foundation are reached through their own singletons, which
 * is why iOS needs no equivalent of `androidContext()`.
 *
 * Two bindings differ from the Android module rather than merely being implemented differently:
 *
 * - **No `InitializeBack4App`.** The Parse SDK this app initializes is an Android library with no
 *   multiplatform counterpart, so there is no iOS implementation to bind and nothing on iOS should
 *   be asking for one. A resolution failure is the correct outcome, and a louder one than a no-op.
 * - **`NotificationManager` is the domain no-op.** Registering notification categories and their
 *   action buttons through `UNUserNotificationCenter` is entitlement-sensitive work that cannot be
 *   verified without a provisioned device, so it is deliberately deferred rather than half-written.
 *   The seam ships its no-op default, the same way every other unimplemented platform capability in
 *   this codebase does, so the graph resolves and a push simply shows nothing until it is built.
 */
actual fun platformCoreModule(): Module = module {
    single<NotificationManager> { NoOpNotificationManager() }
    single<LaunchAttemptStore> { UserDefaultsLaunchAttemptStore() }

    single<Clock> { IosSystemClock() }
    single<DeviceInfo> { IosDeviceInfo() }
    single<ContentSharer> { IosContentSharer() }
    single<AppPreferencesRepository> { UserDefaultsAppPreferencesRepository() }
    single<ReviewPromptStore> { UserDefaultsReviewPromptStore() }
    // createdAtStart for the same reason as Android's: it reads UIApplication's state and registers
    // notification observers, which belongs on the main thread during startup.
    single<AppLifecycle>(createdAtStart = true) { NotificationCenterAppLifecycle() }
}
