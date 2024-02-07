package com.jj.templateproject.di.koin

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.ad.DefaultAdManager
import com.jj.templateproject.data.ad.GetInterstitialAdUnitId
import com.jj.templateproject.data.ad.GetMainAdUnitId
import com.jj.templateproject.data.app.DefaultAppInfoRepository
import com.jj.templateproject.data.app.GetIsInstalledFromValidSource
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.app.AppInfoRepository
import com.jj.templateproject.presentation.MainRootViewModel
import com.jj.templateproject.presentation.ui.login.LoginScreenViewModel
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import com.jj.templateproject.presentation.ui.secondary.SecondaryScreenViewModel
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

    viewModel {
        LoginScreenViewModel()
    }

    viewModel {
        MainScreenViewModel(
            versionTextProvider = get(),
            adManager = get(),
            getGoogleStatusUseCase = get(),
            getGoogleDataUseCase = get(),
            getIsInstalledFromValidSource = get(),
        )
    }
    viewModel {
        MainRootViewModel(
            getMainAdUnitId = get(),
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
    single<AppInfoRepository> { DefaultAppInfoRepository(context = androidContext()) }
    single { GetIsInstalledFromValidSource(appInfoRepository = get()) }
}