package com.jj.templateproject

import android.app.Application

/**
 * Minimal [Application] used for Robolectric tests (configured in `robolectric.properties`).
 *
 * The production [com.jj.templateproject.framework.TemplateProjectApplication] starts Koin and
 * initializes Ads/Parse in `onCreate`, which is both heavyweight and conflicts across tests in a
 * shared JVM (Koin's GlobalContext is static). Tests manage their own Koin via `KoinTestRule` /
 * the integration test, so the test Application intentionally does nothing.
 */
class TestApplication : Application()
