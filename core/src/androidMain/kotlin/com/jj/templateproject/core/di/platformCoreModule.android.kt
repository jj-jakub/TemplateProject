package com.jj.templateproject.core.di

import com.jj.templateproject.core.data.back4app.InitializeBack4App
import com.jj.templateproject.core.data.device.AndroidDeviceInfo
import com.jj.templateproject.core.data.lifecycle.ProcessAppLifecycle
import com.jj.templateproject.core.data.notifications.AndroidNotificationManager
import com.jj.templateproject.core.data.reliability.SharedPreferencesLaunchAttemptStore
import com.jj.templateproject.core.data.review.SharedPreferencesReviewPromptStore
import com.jj.templateproject.core.data.sharing.AndroidContentSharer
import com.jj.templateproject.core.data.time.SystemClock
import com.jj.templateproject.domain.device.DeviceInfo
import com.jj.templateproject.domain.lifecycle.AppLifecycle
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.reliability.LaunchAttemptStore
import com.jj.templateproject.domain.review.ReviewPromptStore
import com.jj.templateproject.domain.sharing.ContentSharer
import com.jj.templateproject.domain.time.Clock
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Every implementation here takes a `Context`, which is the whole reason this module is per-platform:
 * `androidContext()` comes from the Android-only Koin artifact and does not exist on iOS.
 */
actual fun platformCoreModule(): Module = module {
    single<NotificationManager> { AndroidNotificationManager(context = androidContext()) }
    single<InitializeBack4App> { InitializeBack4App(applicationContext = androidContext()) }
    single<LaunchAttemptStore> { SharedPreferencesLaunchAttemptStore(context = androidContext()) }
    single<ReviewPromptStore> { SharedPreferencesReviewPromptStore(context = androidContext()) }

    // Platform capabilities, each behind a domain interface with a test double beside it, so nothing
    // above this layer has to know which SDK answers the question.
    single<Clock> { SystemClock() }
    single<DeviceInfo> { AndroidDeviceInfo(context = androidContext()) }
    single<ContentSharer> { AndroidContentSharer(context = androidContext()) }
    // createdAtStart because it registers a lifecycle observer, which has to happen on the main
    // thread: built during startKoin it always does, built lazily it depends on who injects it first.
    single<AppLifecycle>(createdAtStart = true) { ProcessAppLifecycle() }
}
