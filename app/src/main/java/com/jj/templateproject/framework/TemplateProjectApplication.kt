package com.jj.templateproject.framework

import android.app.Application
import com.jj.templateproject.core.data.back4app.InitializeBack4App
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.di.koin.KoinLauncher
import com.jj.templateproject.domain.ad.AdManager
import org.koin.android.ext.android.inject

class TemplateProjectApplication : Application() {

    private val koinLauncher = KoinLauncher()

    private val activityProvider: ActivityProvider by inject()
    private val adManager: AdManager by inject()
    private val initializeBack4App: InitializeBack4App by inject()

    override fun onCreate() {
        super.onCreate()
        koinLauncher.startKoin(this)
        activityProvider.start()
        adManager.initAds()
        initializeBack4App()
    }
}
