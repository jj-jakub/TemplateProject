package com.jj.templateproject.di.koin

import com.jj.templateproject.data.network.RetrofitFactory
import org.koin.dsl.module

val mainModule = module {

    single { RetrofitFactory() }
}