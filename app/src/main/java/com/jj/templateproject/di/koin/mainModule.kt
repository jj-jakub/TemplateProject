package com.jj.templateproject.di.koin

import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.data.text.VersionTextProvider
import com.jj.templateproject.presentation.ui.main.MainScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainModule = module {

    single { RetrofitFactory() }
    single { VersionTextProvider() }

    viewModel { MainScreenViewModel(versionTextProvider = get()) }
}