package com.jj.templateproject

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.koin.core.module.Module
import org.koin.test.KoinTest
import org.robolectric.Shadows.shadowOf

abstract class BaseInstrumentedKoinTest<T : ComponentActivity> : KoinTest {

    @get:Rule
    val koinTestRule = KoinTestRule(
        modulesProvider = { getInstrumentedTestModules() }
    )

    abstract fun getInstrumentedTestModules(): List<Module>


    @get:Rule(order = 1)
    val addActivityToRobolectricRule = object : TestWatcher() {
        override fun starting(description: Description?) {
            super.starting(description)
            val appContext: Application = ApplicationProvider.getApplicationContext()
            val activityInfo = ActivityInfo().apply {
                name = ComponentActivity::class.java.name
                packageName = appContext.packageName
            }
            shadowOf(appContext.packageManager).addOrUpdateActivity(activityInfo)
        }
    }

    @get:Rule(order = 2)
    abstract val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<T>, T>
}