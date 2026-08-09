package com.jj.templateproject.presentation

import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.core.di.platformCoreModule
import com.jj.templateproject.di.networkingModule
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.ad.AdUnitIds
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.domain.app.AppVersionInfo
import com.jj.templateproject.domain.config.RemoteFlags
import com.jj.templateproject.domain.crosspromo.GetCrossPromoConfigUseCase
import com.jj.templateproject.domain.crosspromo.UrlOpener
import com.jj.templateproject.domain.device.DeviceInfo
import com.jj.templateproject.domain.experiment.ExperimentBucketing
import com.jj.templateproject.domain.experiment.GetExperimentVariantUseCase
import com.jj.templateproject.domain.experiment.InstallIdStore
import com.jj.templateproject.domain.game.GameStateStorage
import com.jj.templateproject.domain.lifecycle.AppLifecycle
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.reliability.LaunchAttemptStore
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.review.ReviewPrompter
import com.jj.templateproject.domain.sharing.ContentSharer
import com.jj.templateproject.domain.time.Clock
import com.jj.templateproject.presentation.di.presentationModule
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import io.ktor.client.HttpClient
import org.koin.dsl.koinApplication
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * The iOS counterpart of `:app`'s `KoinGraphTest`: starts the real graph (shared modules +
 * `platformCoreModule()`'s iOS actual + `IosKoin`'s `iosAppModule`) and asserts it resolves.
 *
 * Exists because this exact gap once shipped silently: `AppPreferencesRepository` had no iOS
 * binding at all, so the app built and linked cleanly but crashed at first launch resolving
 * `MainRootViewModel` (see `UserDefaultsAppPreferencesRepository`'s introduction). A build/link
 * success proves the Kotlin/Swift boundary compiles; it says nothing about whether the graph
 * behind it actually resolves. This closes that gap the way `KoinGraphTest` already does for
 * Android, using a standalone `koinApplication` (not `bootstrapKoin()`, which installs into the
 * global `GlobalContext` and has no per-test isolation) so each test run is independent.
 */
class IosKoinGraphTest {

    private fun buildKoin() = koinApplication {
        modules(networkingModule, coreModule, platformCoreModule(), presentationModule, iosAppModule)
    }.koin

    @Test
    fun networkingAndDomainSingletonsResolve() {
        val koin = buildKoin()

        assertNotNull(koin.get<HttpClient>())
        assertNotNull(koin.get<AppPreferencesRepository>())
    }

    @Test
    fun iosPlatformBindingsResolve() {
        val koin = buildKoin()

        assertNotNull(koin.get<AdManager>())
        assertNotNull(koin.get<AdUnitIds>())
        assertNotNull(koin.get<AppVersionInfo>())
        assertNotNull(koin.get<AppInfoRepository>())
        assertNotNull(koin.get<AnalyticsLogger>())
        assertNotNull(koin.get<CrashReporter>())
        assertNotNull(koin.get<NotificationManager>())
        assertNotNull(koin.get<Clock>())
        assertNotNull(koin.get<DeviceInfo>())
        assertNotNull(koin.get<ContentSharer>())
        assertNotNull(koin.get<AppLifecycle>())
        assertNotNull(koin.get<LaunchAttemptStore>())
        assertNotNull(koin.get<ReviewPrompter>())
        assertNotNull(koin.get<ReviewController>())
        assertNotNull(koin.get<GameStateStorage>())
        assertNotNull(koin.get<InstallIdStore>())
        assertNotNull(koin.get<ExperimentBucketing>())
        assertNotNull(koin.get<GetExperimentVariantUseCase>())
        assertNotNull(koin.get<UrlOpener>())
        assertNotNull(koin.get<RemoteFlags>())
        assertNotNull(koin.get<GetCrossPromoConfigUseCase>())
    }

    @Test
    fun viewModelsResolveFromTheGraph() {
        val koin = buildKoin()

        assertNotNull(koin.get<MainScreenViewModel>())
        assertNotNull(koin.get<MainRootViewModel>())
        assertNotNull(koin.get<SettingsScreenViewModel>())
    }
}
