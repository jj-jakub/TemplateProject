package com.jj.templateproject.presentation

import android.app.Application
import com.jj.templateproject.di.koin.KoinLauncher

class TemplateProjectApplication : Application() {

    private val koinLauncher = KoinLauncher()

    override fun onCreate() {
        super.onCreate()
        koinLauncher.startKoin(this)
    }
}