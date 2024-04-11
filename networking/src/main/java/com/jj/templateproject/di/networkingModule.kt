package com.jj.templateproject.di

import com.jj.templateproject.data.account.AccountService
import com.jj.templateproject.data.account.network.AccountNetwork
import com.jj.templateproject.data.account.network.MockAccountNetwork
import com.jj.templateproject.data.google.DefaultTemplateRepository
import com.jj.templateproject.data.google.network.TemplateNetwork
import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.login.LoginService
import com.jj.templateproject.data.login.network.LoginNetwork
import com.jj.templateproject.data.login.network.MockLoginNetwork
import com.jj.templateproject.domain.google.TemplateRepository
import org.koin.dsl.module
import retrofit2.Retrofit

val networkingModule = module {
    single { get<Retrofit>().create(AccountService::class.java) }
    single<AccountNetwork> { MockAccountNetwork() } // TODO replace with DefaultAccountNetwork(accountService = get()) }

    single { get<Retrofit>().create(LoginService::class.java) }
    single<LoginNetwork> { MockLoginNetwork() } // TODO replace with DefaultLoginNetwork(loginService = get()) }

    single { get<Retrofit>().create(TemplateService::class.java) }
    single { TemplateNetwork(templateService = get()) }
    single<TemplateRepository> { DefaultTemplateRepository(templateNetwork = get()) }
}