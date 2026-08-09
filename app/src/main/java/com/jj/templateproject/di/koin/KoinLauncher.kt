package com.jj.templateproject.di.koin

import android.content.Context
import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.core.di.platformCoreModule
import com.jj.templateproject.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinLauncher {

    /**
     * `:core` contributes two modules rather than one: [coreModule] holds the bindings every
     * platform shares, and [platformCoreModule] the ones only this platform can answer. They are
     * always used together.
     */
    fun startKoin(applicationContext: Context) {
        startKoin {
            androidContext(applicationContext)
            modules(mainModule, networkingModule, coreModule, platformCoreModule())
        }
    }
}
