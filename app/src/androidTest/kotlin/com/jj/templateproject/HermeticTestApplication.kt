package com.jj.templateproject

import android.app.Application
import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.core.di.platformCoreModule
import com.jj.templateproject.di.ActivityProvider
import com.jj.templateproject.di.koin.mainModule
import com.jj.templateproject.di.networkingModule
import com.jj.templateproject.testsupport.testOverrideModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Test `Application` for instrumented UI tests. Starts the real Koin graph plus
 * [testOverrideModule] (faked network repository + no-op ads), and intentionally skips AdMob and
 * Back4App/Parse initialization so flows are deterministic and offline-safe. Tests render
 * `MainNavGraph` directly (rather than `MainActivity`) so the real AdMob banner — whose animating
 * WebView would block Compose's idle synchronization — is never created.
 */
class HermeticTestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val koin = startKoin {
            allowOverride(true)
            androidContext(this@HermeticTestApplication)
            modules(mainModule, networkingModule, coreModule, platformCoreModule(), testOverrideModule)
        }.koin

        koin.get<ActivityProvider>().start()
    }
}
