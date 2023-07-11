package com.jj.templateproject.core.di

import com.jj.templateproject.core.data.google.GetGoogleDataUseCase
import com.jj.templateproject.core.data.google.GetGoogleStatusUseCase
import com.jj.templateproject.core.data.notifications.AndroidNotificationManager
import com.jj.templateproject.domain.notifications.NotificationManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
    single<NotificationManager> { AndroidNotificationManager(context = androidContext()) }
}