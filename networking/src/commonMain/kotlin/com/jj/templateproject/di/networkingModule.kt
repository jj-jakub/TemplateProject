package com.jj.templateproject.di

import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.network.TemplateNetworkApi
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.domain.google.TemplateRepository
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 * The `HttpClient` singleton is bound in the app's own DI module (`mainModule`), not here: only
 * the app layer knows the real base URL and header provider (`BuildConfig`, an auth token seam),
 * so this module receives it via `get()` the same way it used to receive Retrofit.
 */
val networkingModule = module {
    single { TemplateService(client = get<HttpClient>()) }

    single<TemplateNetworkApi> { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> {
        DefaultTemplateRepository(templateNetwork = get(), dispatcherProvider = get())
    }
}
