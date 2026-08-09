package com.jj.templateproject.core.data.device

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

/**
 * The device-class read is the part with a real decision in it, so it gets the tests: the qualifiers
 * below are the same ones the resource system uses, which is what makes `sw600dp` in code and
 * `res/values-sw600dp` agree.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [30])
class AndroidDeviceInfoTest {

    private val deviceInfo get() = AndroidDeviceInfo(RuntimeEnvironment.getApplication())

    @Test
    @Config(qualifiers = "sw400dp")
    fun `a phone-width device is not a tablet`() {
        assertFalse(deviceInfo.isTablet)
    }

    @Test
    @Config(qualifiers = "sw600dp")
    fun `a device at the sw600dp boundary is a tablet`() {
        assertTrue(deviceInfo.isTablet)
    }

    @Test
    @Config(qualifiers = "sw720dp")
    fun `a large tablet is a tablet`() {
        assertTrue(deviceInfo.isTablet)
    }

    @Test
    @Config(qualifiers = "sw400dp-land")
    fun `a phone in landscape is still not a tablet`() {
        // The point of reading smallestScreenWidthDp rather than the current window: rotating a
        // phone must not change the answer.
        assertFalse(deviceInfo.isTablet)
    }

    @Test
    @Config(qualifiers = "en-rGB")
    fun `the locale is reported as a BCP 47 tag`() {
        assertEquals("en-GB", deviceInfo.locale)
    }

    @Test
    fun `the OS version names both the release and the API level`() {
        assertTrue(deviceInfo.osVersion.contains("API 30"))
    }
}
