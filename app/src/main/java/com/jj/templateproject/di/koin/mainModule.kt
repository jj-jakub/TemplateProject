package com.jj.templateproject.di.koin

import com.jj.templateproject.BuildConfig
import com.jj.templateproject.data.config.AppConfiguration
import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.GetGoogleDataUseCase
import com.jj.templateproject.data.google.GetGoogleStatusUseCase
import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.data.network.network.TemplateNetwork
import com.jj.templateproject.data.network.service.TemplateService
import com.jj.templateproject.data.text.VersionTextProvider
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit

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

    single { get<Retrofit>().create(TemplateService::class.java) }

    single { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> { DefaultTemplateRepository(templateNetwork = get()) }

    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
}