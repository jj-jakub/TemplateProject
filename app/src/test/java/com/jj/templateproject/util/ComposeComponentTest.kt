package com.jj.templateproject.util

import android.app.Application
import android.content.pm.ActivityInfo
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.core.app.ApplicationProvider
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.robolectric.Shadows.shadowOf

/**
 * Base for device-free Compose tests that do NOT need Koin (unlike [com.jj.templateproject
 * .BaseInstrumentedKoinTest]). Registers [ComponentActivity] with Robolectric's shadow
 * `PackageManager` so `createAndroidComposeRule` can launch it without a `ui-test-manifest`.
 */
abstract class ComposeComponentTest {

    @get:Rule(order = 0)
    val addActivityToRobolectric = object : TestWatcher() {
        override fun starting(description: Description?) {
            val appContext: Application = ApplicationProvider.getApplicationContext()
            val activityInfo = ActivityInfo().apply {
                name = ComponentActivity::class.java.name
                packageName = appContext.packageName
            }
            shadowOf(appContext.packageManager).addOrUpdateActivity(activityInfo)
        }
    }

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()
}
