package com.jj.templateproject.framework

import android.app.Application
import com.jj.templateproject.core.data.back4app.InitializeBack4App
import com.jj.templateproject.core.data.notifications.AndroidNotificationManager
import com.jj.templateproject.data.firebase.PushRegistrar
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.di.koin.KoinLauncher
import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.analytics.CrashReporter
import com.jj.templateproject.domain.reliability.LaunchMode
import com.jj.templateproject.domain.reliability.LaunchStability
import org.koin.android.ext.android.inject

class TemplateProjectApplication : Application() {

    private val koinLauncher = KoinLauncher()

    private val activityProvider: ActivityProvider by inject()
    private val adManager: AdManager by inject()
    private val initializeBack4App: InitializeBack4App by inject()
    private val launchStability: LaunchStability by inject()
    private val crashReporter: CrashReporter by inject()

    override fun onCreate() {
        super.onCreate()
        koinLauncher.startKoin(this)
        // First, and before anything reads persisted state: two launches that never reached
        // stability make whatever is on disk a suspect, and this is the only chance to say so.
        if (launchStability.beginLaunch() == LaunchMode.SAFE) {
            crashReporter.log(
                "Starting in safe mode: the last ${LaunchStability.SAFE_MODE_THRESHOLD} launches " +
                    "never reached stability, so restored state is being held back.",
            )
        }
        activityProvider.start()
        adManager.initAds()
        initializeBack4App()
        // Created up front rather than only before the first post, so the channel the manifest names
        // as the SDK's fallback exists before the SDK could ever need it.
        AndroidNotificationManager.createChannel(this)
        PushRegistrar.register(this)
    }
}
