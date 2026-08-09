package com.jj.templateproject.presentation

import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.core.di.platformCoreModule
import com.jj.templateproject.data.network.TemplateHttpClientFactory
import com.jj.templateproject.di.networkingModule
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.ad.AdUnitIds
import com.jj.templateproject.domain.ad.NoOpAdManager
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter
import com.jj.templateproject.domain.analytics.NoOpAnalyticsLogger
import com.jj.templateproject.domain.analytics.NoOpCrashReporter
import com.jj.templateproject.domain.app.AlwaysInstalledFromValidSource
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.domain.app.AppVersionInfo
import com.jj.templateproject.domain.coroutines.DefaultDispatcherProvider
import com.jj.templateproject.domain.coroutines.DispatcherProvider
import com.jj.templateproject.presentation.di.presentationModule
import io.ktor.client.HttpClient
import org.koin.core.context.startKoin
import org.koin.dsl.module

/**
 * The iOS counterpart of `:app`'s `KoinLauncher`. There is no separate Kotlin "iOS app" module —
 * Swift is the iOS app shell, and this is the last Kotlin layer before it — so the bindings only
 * `:app`'s `mainModule` supplies on Android (a real `AdUnitIds`/`AppVersionInfo`/`AdManager`, and
 * the `HttpClient`/`DispatcherProvider` pair `:networking`'s own module deliberately leaves for
 * the app layer to provide, since only it knows the base URL) live here instead, alongside the
 * same shared modules Android assembles.
 *
 * Deliberately narrower than Android's graph otherwise: no `ActivityProvider`/`RemoteFlags`
 * binding exists yet, because neither has a real iOS implementation (Firebase Remote Config stays
 * Android-only for now, and there is no iOS activity-scoped context to provide). Every other
 * Android-only binding below is a NoOp/always-true stand-in rather than an outright gap, so the
 * graph resolves the same shape it does on Android, just with weaker platform behavior until a
 * real iOS implementation replaces one.
 *
 * Called once from Swift, at app startup (`iOSApp.init()`), the same way `TemplateProjectApplication.onCreate()`
 * calls `KoinLauncher.startKoin` on Android.
 *
 * Named `bootstrapKoin`, not `initKoin`: Kotlin/Native's Objective-C export renames any top-level
 * function starting with `init` (it collides with Cocoa's initializer naming convention, so the
 * generated header prefixes it with `do` instead — `doInitKoin()`). Avoiding the `init` prefix
 * altogether keeps the Swift call site unsurprising.
 */
fun bootstrapKoin() {
    startKoin {
        modules(networkingModule, coreModule, platformCoreModule(), presentationModule, iosAppModule)
    }
}

/** Internal, not private: `IosKoinGraphTest` (iosTest) assembles the same module set directly. */
internal val iosAppModule = module {
    single<AdUnitIds> { IosAdUnitIds() }
    single<AppVersionInfo> { IosAppVersionInfo() }
    single<AppInfoRepository> { AlwaysInstalledFromValidSource }
    single<DispatcherProvider> { DefaultDispatcherProvider() }
    // The placeholder base URL both Android flavors ship (see app/build.gradle.kts's
    // ServerBaseUrl): this template makes no real network call, so there is no per-platform
    // config surface to build out yet. Body logging off, unlike Android's debug-only path — there
    // is no separate iOS debug/release distinction here to key it off yet.
    single<HttpClient> { TemplateHttpClientFactory.create(baseUrl = "https://www.google.com") }
    // No real ad SDK wired up on iOS yet (see ComposeAdView's iOS actual); NoOpAdManager is what
    // lets MainScreenViewModel resolve at all in the meantime.
    single<AdManager> { NoOpAdManager }
    // No Firebase (or other) analytics SDK wired up on iOS yet, mirroring AnalyticsFactory's own
    // no-op fallback on Android when reporting is off or unconfigured.
    single<AnalyticsLogger> { NoOpAnalyticsLogger() }
    single<CrashReporter> { NoOpCrashReporter() }
}
