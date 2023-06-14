package com.jj.templateproject.di

import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.google.TemplateRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val networkingModule = module {
    single { get<Retrofit>().create(TemplateService::class.java) }

    single { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> { DefaultTemplateRepository(templateNetwork = get()) }
}