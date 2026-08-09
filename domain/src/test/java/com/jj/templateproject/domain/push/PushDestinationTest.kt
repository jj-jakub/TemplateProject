package com.jj.templateproject.domain.push

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class PushDestinationTest {

    @Test
    fun `every destination survives a round trip through its token`() {
        val destinations = listOf(
            PushDestination.Home,
            PushDestination.Settings,
            PushDestination.PlayStoreApp("com.example.companion"),
        )

        destinations.forEach { destination ->
            assertEquals(destination, PushDestinations.parse(PushDestinations.token(destination)))
        }
    }

    @Test
    fun `tokens are read case-insensitively, since a campaign is composed by hand`() {
        assertEquals(PushDestination.Home, PushDestinations.parse("HOME"))
        assertEquals(PushDestination.Settings, PushDestinations.parse("Settings"))
        assertEquals(
            PushDestination.PlayStoreApp("com.example.app"),
            PushDestinations.parse("Play:com.example.app"),
        )
    }

    @Test
    fun `an unknown token parses to null rather than throwing`() {
        assertNull(PushDestinations.parse("checkout"))
        assertNull(PushDestinations.parse(""))
        assertNull(PushDestinations.parse(null))
    }

    @Test
    fun `a play target that is not shaped like a package is refused`() {
        // The value ends up inside a store URL, so anything that is not a package name is rejected
        // outright rather than handed on.
        assertNull(PushDestinations.parse("play:https://example.com"))
        assertNull(PushDestinations.parse("play:../../etc/passwd"))
        assertNull(PushDestinations.parse("play:single"))
        assertNull(PushDestinations.parse("play:"))
        assertNull(PushDestinations.parse("play:com..example"))
        assertNull(PushDestinations.parse("play:com.example app"))
    }

    @Test
    fun `a package segment may not start with a digit`() {
        assertNull(PushDestinations.parse("play:com.1example"))
        assertEquals(
            PushDestination.PlayStoreApp("com.example1"),
            PushDestinations.parse("play:com.example1"),
        )
    }
}
