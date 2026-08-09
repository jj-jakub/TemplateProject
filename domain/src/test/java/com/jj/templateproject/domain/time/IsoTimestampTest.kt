package com.jj.templateproject.domain.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IsoTimestampTest {

    @Test
    fun `formats the unix epoch itself`() {
        assertEquals("1970-01-01T00:00:00Z", IsoTimestamp.format(0L))
    }

    @Test
    fun `formats an ordinary instant to the second`() {
        // 2026-08-07T14:03:22Z
        assertEquals("2026-08-07T14:03:22Z", IsoTimestamp.format(1_786_111_402_000L))
    }

    @Test
    fun `drops sub-second precision rather than rounding up`() {
        val onTheSecond = IsoTimestamp.format(1_786_111_402_000L)
        val almostTheNextSecond = IsoTimestamp.format(1_786_111_402_999L)

        assertEquals(onTheSecond, almostTheNextSecond)
    }

    @Test
    fun `formats a leap day, which the month arithmetic has to get right`() {
        // 2024-02-29T12:00:00Z
        assertEquals("2024-02-29T12:00:00Z", IsoTimestamp.format(1_709_208_000_000L))
    }

    @Test
    fun `formats a century year that is a leap year`() {
        // 2000-02-29T00:00:00Z — divisible by 100 but also by 400, so the leap day exists.
        assertEquals("2000-02-29T00:00:00Z", IsoTimestamp.format(951_782_400_000L))
    }

    @Test
    fun `formats an instant before the epoch, where the day and the time of day borrow differently`() {
        // 1969-12-31T23:59:59Z — one second before the epoch, the case a naive division gets wrong.
        assertEquals("1969-12-31T23:59:59Z", IsoTimestamp.format(-1_000L))
    }
}
