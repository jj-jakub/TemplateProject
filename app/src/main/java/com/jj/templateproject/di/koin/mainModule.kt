package com.jj.templateproject.di.koin

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.config.VersionTextProvider
import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
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
        MainScreenViewModel(
            versionTextProvider = get(),
            getGoogleStatusUseCase = get(),
            getGoogleDataUseCase = get(),
        )
    }
}