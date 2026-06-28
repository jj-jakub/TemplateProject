package com.jj.templateproject.framework.navigation.model

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RouteTest {

    private fun entryWithRoute(route: String?): NavBackStackEntry {
        val destination = mockk<NavDestination> { every { this@mockk.route } returns route }
        return mockk { every { this@mockk.destination } returns destination }
    }

    @Test
    fun `matchesCurrentEntry is true when the route contains the class name`() {
        val entry = entryWithRoute("com.jj.templateproject.framework.navigation.model.Route.SettingsScreen")

        assertTrue(Route.SettingsScreen.matchesCurrentEntry(entry))
    }

    @Test
    fun `matchesCurrentEntry is false for a different destination`() {
        val entry = entryWithRoute("com.jj.templateproject.framework.navigation.model.Route.MainScreen")

        assertFalse(Route.SettingsScreen.matchesCurrentEntry(entry))
    }

    @Test
    fun `matchesCurrentEntry is false when there is no current entry`() {
        assertFalse(Route.MainScreen.matchesCurrentEntry(null))
    }

    @Test
    fun `matchesCurrentEntry is false when the destination has no route`() {
        assertFalse(Route.MainScreen.matchesCurrentEntry(entryWithRoute(null)))
    }

    @Test
    fun `findSelectedIndex returns the index of the matching route`() {
        val items = listOf(Route.MainScreen, Route.SecondaryScreen(), Route.SettingsScreen)
        val entry = entryWithRoute("…model.Route.SettingsScreen")

        assertEquals(2, items.findSelectedIndex(entry))
    }

    @Test
    fun `findSelectedIndex defaults to zero when nothing matches`() {
        val items = listOf(Route.MainScreen, Route.SecondaryScreen(), Route.SettingsScreen)
        val entry = entryWithRoute("something.completely.unrelated")

        assertEquals(0, items.findSelectedIndex(entry))
    }
}
