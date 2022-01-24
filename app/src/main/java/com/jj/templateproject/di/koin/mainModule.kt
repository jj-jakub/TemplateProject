package com.jj.templateproject.di.koin

import com.jj.templateproject.data.network.RetrofitFactory
import com.jj.templateproject.data.text.VersionTextProvider
import org.koin.dsl.module

val mainModule = module {

    single { RetrofitFactory() }
    single { VersionTextProvider() }
}