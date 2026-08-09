package com.jj.templateproject.di

import androidx.test.core.app.ApplicationProvider
import com.jj.templateproject.core.data.back4app.InitializeBack4App
import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.core.di.platformCoreModule
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.google.network.TemplateNetworkApi
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.di.koin.mainModule
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.ad.AdUnitIds
import com.jj.templateproject.domain.ad.GetInterstitialAdUnitId
import com.jj.templateproject.domain.ad.GetMainAdUnitId
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.domain.app.AppVersionInfo
import com.jj.templateproject.domain.app.GetIsInstalledFromValidSource
import com.jj.templateproject.domain.config.RemoteFlags
import com.jj.templateproject.domain.crosspromo.GetCrossPromoConfigUseCase
import com.jj.templateproject.domain.crosspromo.UrlOpener
import com.jj.templateproject.domain.device.DeviceInfo
import com.jj.templateproject.domain.experiment.ExperimentBucketing
import com.jj.templateproject.domain.experiment.GetExperimentVariantUseCase
import com.jj.templateproject.domain.experiment.InstallIdStore
import com.jj.templateproject.domain.game.GameStateStorage
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.lifecycle.AppLifecycle
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.domain.reliability.LaunchAttemptStore
import com.jj.templateproject.domain.reliability.LaunchStability
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.review.ReviewPrompter
import com.jj.templateproject.domain.sharing.ContentSharer
import com.jj.templateproject.domain.time.Clock
import com.jj.templateproject.presentation.MainRootViewModel
import com.jj.templateproject.presentation.di.presentationModule
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import com.jj.templateproject.presentation.ui.settings.VersionTextProvider
import io.ktor.client.HttpClient
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.get
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Integration test: starts the full Koin graph (main + networking + core + presentation modules)
 * against a Robolectric application context and asserts that the real wiring resolves. Catches
 * missing or mis-typed DI bindings that unit tests with hand-built fakes would miss.
 *
 * `:core` contributes a shared module and a per-platform one, and this asserts the Android pairing
 * specifically: it runs on the JVM, so `platformCoreModule()` here is the Android actual. See
 * `:presentation`'s `IosKoinGraphTest` for the iOS pairing.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KoinGraphTest : KoinTest {

    @Before
    fun setup() {
        // Another test (e.g. the Robolectric UI tests via KoinTestRule) may have left Koin running.
        if (GlobalContext.getOrNull() != null) stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(mainModule, networkingModule, coreModule, platformCoreModule(), presentationModule)
        }
    }

    @After
    fun tearDown() {
        if (GlobalContext.getOrNull() != null) stopKoin()
    }

    @Test
    fun `networking and domain singletons resolve`() {
        assertNotNull(get<AppConfiguration>())
        assertNotNull(get<HttpClient>())
        assertNotNull(get<TemplateService>())
        assertNotNull(get<TemplateNetworkApi>())
        assertNotNull(get<TemplateRepository>())
        assertNotNull(get<GetGoogleDataUseCase>())
        assertNotNull(get<GetGoogleStatusUseCase>())
    }

    @Test
    fun `app singletons resolve`() {
        assertNotNull(get<AdManager>())
        assertNotNull(get<AdUnitIds>())
        assertNotNull(get<AppVersionInfo>())
        assertNotNull(get<ActivityProvider>())
        assertNotNull(get<GetMainAdUnitId>())
        assertNotNull(get<GetInterstitialAdUnitId>())
        assertNotNull(get<AppInfoRepository>())
        assertNotNull(get<GetIsInstalledFromValidSource>())
        assertNotNull(get<VersionTextProvider>())
        assertNotNull(get<NotificationManager>())
        assertNotNull(get<AppPreferencesRepository>())
        assertNotNull(get<AnalyticsLogger>())
        assertNotNull(get<CrashReporter>())
        assertNotNull(get<RemoteFlags>())
        assertNotNull(get<LaunchStability>())
        assertNotNull(get<ReviewPrompter>())
        assertNotNull(get<ReviewController>())
        assertNotNull(get<GameStateStorage>())
        assertNotNull(get<ExperimentBucketing>())
        assertNotNull(get<GetExperimentVariantUseCase>())
        assertNotNull(get<GetCrossPromoConfigUseCase>())
    }

    @Test
    fun `platform seams resolve`() {
        // Each of these is an interface in :domain with an Android implementation in :core, so a
        // binding that goes missing is only ever caught here.
        assertNotNull(get<Clock>())
        assertNotNull(get<DeviceInfo>())
        assertNotNull(get<ContentSharer>())
        assertNotNull(get<AppLifecycle>())
        assertNotNull(get<LaunchAttemptStore>())
        assertNotNull(get<InstallIdStore>())
        assertNotNull(get<UrlOpener>())
    }

    @Test
    fun `the Android-only Parse initializer resolves`() {
        // Bound by the Android platformCoreModule and by no other: the iOS one omits it, since the
        // SDK it wraps has no iOS counterpart. Asserted separately from the seams above to keep
        // that asymmetry visible rather than buried in a list.
        assertNotNull(get<InitializeBack4App>())
    }

    @Test
    fun `view models resolve from the graph`() {
        assertNotNull(get<MainScreenViewModel>())
        assertNotNull(get<MainRootViewModel>())
        assertNotNull(get<SettingsScreenViewModel>())
    }
}
