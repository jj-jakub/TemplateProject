package com.jj.templateproject.domain.push

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

/**
 * The scheme is browsable, so any web page can send one of these. Everything here is therefore about
 * what a link may NOT make the app do.
 */
class PushDeepLinkTest {

    @Test
    fun `every destination survives a round trip through its link`() {
        val destinations = listOf(
            PushDestination.Home,
            PushDestination.Settings,
            PushDestination.PlayStoreApp("com.example.companion"),
        )

        destinations.forEach { destination ->
            assertEquals(destination, PushDeepLink.parse(PushDeepLink.format(destination)))
        }
    }

    @Test
    fun `a trailing slash is tolerated since links are written by hand`() {
        assertEquals(PushDestination.Home, PushDeepLink.parse("templateproject://home/"))
    }

    @Test
    fun `the scheme is matched case-insensitively`() {
        assertEquals(PushDestination.Settings, PushDeepLink.parse("TemplateProject://SETTINGS"))
    }

    @Test
    fun `a link for another scheme routes nowhere`() {
        assertNull(PushDeepLink.parse("https://example.com/home"))
        assertNull(PushDeepLink.parse("otherapp://home"))
    }

    @Test
    fun `an unknown host routes nowhere`() {
        assertNull(PushDeepLink.parse("templateproject://admin"))
        assertNull(PushDeepLink.parse("templateproject://"))
    }

    @Test
    fun `a play link with no usable package routes nowhere`() {
        assertNull(PushDeepLink.parse("templateproject://play"))
        assertNull(PushDeepLink.parse("templateproject://play/"))
        assertNull(PushDeepLink.parse("templateproject://play/https://example.com"))
    }

    @Test
    fun `null and blank input route nowhere`() {
        assertNull(PushDeepLink.parse(null))
        assertNull(PushDeepLink.parse(""))
        assertNull(PushDeepLink.parse("   "))
    }
}
