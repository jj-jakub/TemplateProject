package com.jj.templateproject.core.di

import com.jj.templateproject.core.data.back4app.InitializeBack4App
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.core.data.notifications.AndroidNotificationManager
import com.jj.templateproject.domain.notifications.NotificationManager
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
    single { GetThemeModeUseCase(appPreferencesRepository = get()) }
    single { SetThemeModeUseCase(appPreferencesRepository = get()) }
    single<NotificationManager> { AndroidNotificationManager(context = androidContext()) }
    single<InitializeBack4App> { InitializeBack4App(applicationContext = androidContext()) }
}
