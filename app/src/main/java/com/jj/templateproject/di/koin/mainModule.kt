package com.jj.templateproject.di.koin

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.ad.DefaultAdManager
import com.jj.templateproject.data.ad.GetInterstitialAdUnitId
import com.jj.templateproject.data.ad.GetMainAdUnitId
import com.jj.templateproject.data.analytics.AnalyticsFactory
import com.jj.templateproject.data.app.DefaultAppInfoRepository
import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.data.preferences.DataStoreAppPreferencesRepository
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.analytics.AnalyticsLogger
import com.jj.templateproject.domain.analytics.CrashReporter
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.domain.coroutines.DefaultDispatcherProvider
import com.jj.templateproject.domain.coroutines.DispatcherProvider
import com.jj.templateproject.domain.preferences.AppPreferencesRepository
import com.jj.templateproject.presentation.MainRootViewModel
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreenViewModel
import com.jj.templateproject.presentation.ui.settings.SettingsScreenViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single {
        AppConfiguration(
            baseUrl = BuildConfig.ServerBaseUrl,
        )
    }
    single { RetrofitFactory() }
    single {
        get<RetrofitFactory>().retrofit(
            baseUrl = get<AppConfiguration>().baseUrl,
        )
    }
    single { VersionTextProvider() }
    single<DispatcherProvider> { DefaultDispatcherProvider() }

    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.create(
            produceFile = { androidContext().preferencesDataStoreFile("app_preferences") },
        )
    }
    single<AppPreferencesRepository> { DataStoreAppPreferencesRepository(dataStore = get()) }

    // Firebase-backed once a google-services.json is present AND this is a build a real user could
    // be running; the no-op pair otherwise, so a fresh clone runs and a development session never
    // reports into the same dashboards as a shipped build. See AnalyticsFactory / BuildProfile.
    single<AnalyticsLogger> { AnalyticsFactory.analyticsLogger(androidContext()) }
    single<CrashReporter> { AnalyticsFactory.crashReporter(androidContext()) }

    viewModel {
        MainScreenViewModel(
            adManager = get(),
        )
    }
    viewModel {
        SettingsScreenViewModel(
            versionTextProvider = get(),
            getGoogleStatusUseCase = get(),
            getGoogleDataUseCase = get(),
            getIsInstalledFromValidSource = get(),
            getThemeModeUseCase = get(),
            setThemeModeUseCase = get(),
        )
    }
    viewModel {
        MainRootViewModel(
            getMainAdUnitId = get(),
            getThemeModeUseCase = get(),
            analyticsLogger = get(),
        )
    }
    viewModel {
        SecondaryScreenViewModel(savedStateHandle = get())
    }
    single<AdManager> {
        DefaultAdManager(
            context = androidContext(),
            activityProvider = get(),
            getInterstitialAdUnitId = get(),
        )
    }
    single { ActivityProvider(application = androidApplication()) }
    single { GetMainAdUnitId() }
    single { GetInterstitialAdUnitId() }
    single<AppInfoRepository> { DefaultAppInfoRepository() }
    single { GetIsInstalledFromValidSource(appInfoRepository = get()) }
}
