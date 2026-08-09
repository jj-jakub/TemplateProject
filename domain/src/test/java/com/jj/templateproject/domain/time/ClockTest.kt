package com.jj.templateproject.domain.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * The seam exists so a duration can be measured in a test without waiting, and so the wall-clock and
 * monotonic readings stay distinguishable. These pin the double every other module's tests use.
 */
class ClockTest {

    @Test
    fun `advancing moves both readings together, as an undisturbed clock does`() {
        val clock = FixedClock(now = 1_000L, elapsed = 500L)

        clock.advance(250L)

        assertEquals(1_250L, clock.nowMillis())
        assertEquals(750L, clock.elapsedMillis())
    }

    @Test
    fun `a wall-clock correction leaves the monotonic reading alone`() {
        // The reason durations must be measured on elapsedMillis: a clock correction here would
        // otherwise turn an in-flight measurement into a jump, negative ones included.
        val clock = FixedClock(now = 1_000L, elapsed = 500L)

        clock.setWallClock(9_999_999L)

        assertEquals(9_999_999L, clock.nowMillis())
        assertEquals(500L, clock.elapsedMillis())
    }

    @Test
    fun `a duration measured across a wall-clock jump is still correct`() {
        val clock = FixedClock(now = 1_000L, elapsed = 500L)
        val startedAt = clock.elapsedMillis()

        clock.advance(200L)
        clock.setWallClock(0L)

        assertEquals(200L, clock.elapsedMillis() - startedAt)
    }
}
