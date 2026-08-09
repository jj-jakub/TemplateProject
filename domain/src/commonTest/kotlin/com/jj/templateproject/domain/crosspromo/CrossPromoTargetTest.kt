package com.jj.templateproject.domain.crosspromo

import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class CrossPromoTargetTest {

    @Test
    fun `a full Play Store URL resolves unchanged`() {
        val url = "https://play.google.com/store/apps/details?id=com.example.other"

        assertEquals(url, CrossPromoTarget.resolveUrl(url))
    }

    @Test
    fun `a full App Store URL resolves unchanged`() {
        val url = "https://apps.apple.com/us/app/other-app/id123456789"

        assertEquals(url, CrossPromoTarget.resolveUrl(url))
    }

    @Test
    fun `a bare package name resolves to its store listing`() {
        assertEquals(
            "https://play.google.com/store/apps/details?id=com.example.other",
            CrossPromoTarget.resolveUrl("com.example.other"),
        )
    }

    @Test
    fun `a single-segment string is not a valid package name`() {
        assertNull(CrossPromoTarget.resolveUrl("notapackage"))
    }

    @Test
    fun `an arbitrary URL to a different host is rejected`() {
        assertNull(CrossPromoTarget.resolveUrl("https://evil.example.com/phish"))
    }

    @Test
    fun `an empty string is rejected`() {
        assertNull(CrossPromoTarget.resolveUrl(""))
    }
}
