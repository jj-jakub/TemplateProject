package com.jj.server.di

import com.jj.server.domain.managers.VibrationManager
import com.jj.server.data.server.DefaultRequestDispatcher
import com.jj.server.data.server.network.RetrofitFactory
import com.jj.server.domain.server.IPProvider
import com.jj.server.domain.server.ServerStarter
import com.jj.server.domain.server.requests.RequestDispatcher
import com.jj.server.domain.server.requests.RequestReceiver
import com.jj.server.framework.managers.AndroidVibrationManager
import com.jj.server.framework.server.AndroidIPProvider
import com.jj.server.framework.server.KtorServerStarter
import com.jj.server.framework.server.requests.KtorRequestReceiver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val serverModule = module {

    single<IPProvider> { AndroidIPProvider(androidContext()) }
    single<RequestDispatcher> { DefaultRequestDispatcher(get()) }
    single<RequestReceiver> { KtorRequestReceiver(get()) }
    single<ServerStarter> { KtorServerStarter(get(), get()) }

    single { RetrofitFactory() }
    single<VibrationManager> { AndroidVibrationManager(get()) }

}