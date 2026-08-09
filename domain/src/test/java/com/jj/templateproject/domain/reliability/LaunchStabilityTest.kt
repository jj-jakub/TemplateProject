package com.jj.templateproject.domain.reliability

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LaunchStabilityTest {

    private class FakeStore(var value: Int = 0) : LaunchAttemptStore {
        override fun readFailedLaunches() = value
        override fun writeFailedLaunches(count: Int) {
            value = count
        }
    }

    @Test
    fun `a first launch is normal, and is not treated as a failure`() {
        val store = FakeStore()

        assertEquals(LaunchMode.NORMAL, LaunchStability(store).beginLaunch())
    }

    @Test
    fun `a launch that survives clears the history`() {
        val store = FakeStore()
        val stability = LaunchStability(store)

        stability.beginLaunch()
        stability.markStable()

        assertEquals(0, store.value)
    }

    @Test
    fun `one failed launch is not enough to hold state back`() {
        val store = FakeStore()

        LaunchStability(store).beginLaunch() // crashes before markStable

        assertEquals(LaunchMode.NORMAL, LaunchStability(store).beginLaunch())
    }

    @Test
    fun `the launch after the threshold starts in safe mode`() {
        val store = FakeStore()

        repeat(LaunchStability.SAFE_MODE_THRESHOLD) { LaunchStability(store).beginLaunch() }

        assertEquals(LaunchMode.SAFE, LaunchStability(store).beginLaunch())
    }

    @Test
    fun `one good launch after repeated failures returns the next one to normal`() {
        val store = FakeStore()
        repeat(LaunchStability.SAFE_MODE_THRESHOLD) { LaunchStability(store).beginLaunch() }

        LaunchStability(store).apply {
            beginLaunch()
            markStable()
        }

        assertEquals(LaunchMode.NORMAL, LaunchStability(store).beginLaunch())
    }

    @Test
    fun `the attempt is banked before the mode is reported, so a crash mid-launch still counts`() {
        val store = FakeStore()

        LaunchStability(store).beginLaunch()

        assertEquals(1, store.value)
    }

    @Test
    fun `nonsense in the store reads as a clean history rather than as safe mode`() {
        val store = FakeStore(value = -7)

        assertEquals(LaunchMode.NORMAL, LaunchStability(store).beginLaunch())
        assertEquals(1, store.value)
    }

    @Test
    fun `the resolved mode stays readable after the launch decision`() {
        val store = FakeStore()
        repeat(LaunchStability.SAFE_MODE_THRESHOLD) { LaunchStability(store).beginLaunch() }
        val stability = LaunchStability(store)

        stability.beginLaunch()

        assertEquals(LaunchMode.SAFE, stability.mode)
    }
}
