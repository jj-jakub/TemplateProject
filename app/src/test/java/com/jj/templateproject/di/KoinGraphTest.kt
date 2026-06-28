package com.jj.templateproject.di

import androidx.test.core.app.ApplicationProvider
import com.jj.templateproject.data.ad.GetInterstitialAdUnitId
import com.jj.templateproject.data.ad.GetMainAdUnitId
import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.di.koin.mainModule
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.presentation.MainRootViewModel
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
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
import retrofit2.Retrofit

/**
 * Integration test: starts the full Koin graph (main + networking + core modules) against a
 * Robolectric application context and asserts that the real wiring resolves. Catches missing or
 * mis-typed DI bindings that unit tests with hand-built mocks would miss.
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
            modules(mainModule, networkingModule, coreModule)
        }
    }

    @After
    fun tearDown() {
        if (GlobalContext.getOrNull() != null) stopKoin()
    }

    @Test
    fun `networking and domain singletons resolve`() {
        assertNotNull(get<AppConfiguration>())
        assertNotNull(get<Retrofit>())
        assertNotNull(get<TemplateService>())
        assertNotNull(get<TemplateNetwork>())
        assertNotNull(get<TemplateRepository>())
        assertNotNull(get<GetGoogleDataUseCase>())
        assertNotNull(get<GetGoogleStatusUseCase>())
    }

    @Test
    fun `app singletons resolve`() {
        assertNotNull(get<AdManager>())
        assertNotNull(get<ActivityProvider>())
        assertNotNull(get<GetMainAdUnitId>())
        assertNotNull(get<GetInterstitialAdUnitId>())
        assertNotNull(get<AppInfoRepository>())
        assertNotNull(get<GetIsInstalledFromValidSource>())
        assertNotNull(get<VersionTextProvider>())
        assertNotNull(get<NotificationManager>())
    }

    @Test
    fun `view models resolve from the graph`() {
        assertNotNull(get<MainScreenViewModel>())
        assertNotNull(get<MainRootViewModel>())
        assertNotNull(get<SettingsScreenViewModel>())
    }
}
