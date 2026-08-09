package com.jj.templateproject.framework.navigation

import com.jj.templateproject.domain.push.PushDestination
import com.jj.templateproject.framework.navigation.model.Route
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PushRoutesTest {

    @Test
    fun `home opens the main screen`() {
        assertEquals(Route.MainScreen, PushRoutes.routeFor(PushDestination.Home))
    }

    @Test
    fun `settings opens the settings screen`() {
        assertEquals(Route.SettingsScreen, PushRoutes.routeFor(PushDestination.Settings))
    }

    @Test
    fun `an external destination has no route, because it never enters the app`() {
        // It is opened as a view intent straight from the notification: routing it through our own
        // UI would flash the app on the way, and since Android 12 a notification may not start an
        // activity indirectly anyway.
        assertNull(PushRoutes.routeFor(PushDestination.PlayStoreApp("com.example.app")))
    }
}
