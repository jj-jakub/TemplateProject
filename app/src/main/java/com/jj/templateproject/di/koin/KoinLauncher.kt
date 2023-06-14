package com.jj.templateproject.di.koin

import android.content.Context
import com.jj.templateproject.core.di.coreModule
import com.jj.templateproject.di.networkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinLauncher {

    fun startKoin(applicationContext: Context) {
        startKoin {
            androidContext(applicationContext)
            modules(mainModule, networkingModule, coreModule)
        }
    }
}
