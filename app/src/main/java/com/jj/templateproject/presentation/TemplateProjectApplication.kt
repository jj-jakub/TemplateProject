package com.jj.templateproject.presentation

import android.app.Application
import com.jj.templateproject.di.koin.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class TemplateProjectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        launchKoin()
    }

    private fun launchKoin() {
        startKoin {
            androidContext(this@TemplateProjectApplication)
            modules(mainModule)
        }
    }
}