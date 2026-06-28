package com.jj.templateproject.design

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
 * Base class for device-free Compose component tests.
 *
 * Registers [ComponentActivity] with Robolectric's shadow `PackageManager` so
 * `createAndroidComposeRule` can launch it without shipping a `ui-test-manifest` (the same
 * approach the `:app` module uses). Concrete tests annotate themselves with
 * `@RunWith(RobolectricTestRunner::class)` and `@Config(sdk = [30])`.
 */
abstract class ComponentUiTest {

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
