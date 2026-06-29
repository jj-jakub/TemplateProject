package com.jj.templateproject

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

/**
 * Instrumentation runner that swaps in [HermeticTestApplication] for the production
 * `TemplateProjectApplication`, so UI tests run against a deterministic Koin graph (faked network,
 * no AdMob interstitials, no Parse) on a real device/emulator.
 *
 * Wired via `testInstrumentationRunner` in app/build.gradle.kts.
 */
class HermeticTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, HermeticTestApplication::class.java.name, context)
    }
}
