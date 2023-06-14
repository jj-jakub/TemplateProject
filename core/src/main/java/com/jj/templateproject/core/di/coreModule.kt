package com.jj.templateproject.core.di

import com.jj.templateproject.core.data.google.GetGoogleDataUseCase
import com.jj.templateproject.core.data.google.GetGoogleStatusUseCase
import org.koin.dsl.module

val coreModule = module {
    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
}