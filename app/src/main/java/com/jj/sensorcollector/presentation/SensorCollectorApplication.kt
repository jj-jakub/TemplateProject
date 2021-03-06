package com.jj.sensorcollector.presentation

import android.app.Application
import com.jj.sensorcollector.di.KoinLauncher
import com.jj.sensorcollector.domain.initializers.AppInitializer
import org.koin.android.ext.android.inject

class SensorCollectorApplication : Application() {

    private val koinLauncher: KoinLauncher = KoinLauncher()

    private val appInitializer: AppInitializer by inject()

    override fun onCreate() {
        super.onCreate()
        koinLauncher.startKoin(this)
        appInitializer.initialize()
    }
}