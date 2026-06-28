package com.jj.templateproject

import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class KoinTestRule(
    // A provider (not a List) so the modules are resolved when the test starts, after the
    // subclass is fully constructed. Resolving them during field init would read not-yet-set
    // subclass fields as null and feed a null module to Koin.
    private val modulesProvider: () -> List<Module>,
) : TestWatcher() {

    override fun starting(description: Description) {
        val isKoinRunning = GlobalContext.getOrNull() != null
        if (isKoinRunning) return
        startKoin {
            androidContext(InstrumentationRegistry.getInstrumentation().targetContext.applicationContext)
            modules(modulesProvider())
        }
    }
}