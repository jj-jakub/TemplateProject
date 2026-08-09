package com.jj.templateproject.core.data.device

import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * The locale tag is the one read on this platform with a decision in it, so it gets the tests.
 * Everything else [IosDeviceInfo] exposes is a single UIKit property, which has nothing of ours to
 * assert: a test for it would be pinning Apple's answer rather than our behaviour.
 */
class IosDeviceInfoTest {

    @Test
    fun `a language and a region join into a BCP 47 tag`() {
        assertEquals("en-GB", bcp47Tag(languageCode = "en", countryCode = "GB"))
    }

    @Test
    fun `a locale with no region is just the language`() {
        // Not "en-": a trailing separator is not a tag any parser accepts.
        assertEquals("en", bcp47Tag(languageCode = "en", countryCode = null))
    }

    @Test
    fun `an empty region is treated as no region at all`() {
        // Objective-C answers an unset value with an empty string as readily as with nil.
        assertEquals("pl", bcp47Tag(languageCode = "pl", countryCode = ""))
    }

    @Test
    fun `a locale with no language at all reports nothing`() {
        // Matching the Android side which reports an empty string rather than inventing a default.
        assertEquals("", bcp47Tag(languageCode = null, countryCode = "GB"))
    }
}
