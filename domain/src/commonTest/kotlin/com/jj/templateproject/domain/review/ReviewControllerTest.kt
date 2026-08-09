package com.jj.templateproject.domain.review

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.Test

class ReviewControllerTest {

    private class FakeStore(
        var count: Int = 0,
        var hasPrompted: Boolean = false,
    ) : ReviewPromptStore {
        override fun readSatisfyingMomentCount() = count
        override fun writeSatisfyingMomentCount(count: Int) {
            this.count = count
        }

        override fun readHasPrompted() = hasPrompted
        override fun writeHasPrompted(hasPrompted: Boolean) {
            this.hasPrompted = hasPrompted
        }
    }

    private class RecordingPrompter : ReviewPrompter {
        var requestCount = 0
            private set

        override fun requestReview() {
            requestCount++
        }
    }

    @Test
    fun `fewer than the threshold does not prompt`() {
        val store = FakeStore()
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        repeat(ReviewController.SATISFYING_MOMENT_THRESHOLD - 1) { controller.recordSatisfyingMoment() }

        assertEquals(0, prompter.requestCount)
    }

    @Test
    fun `the threshold moment prompts`() {
        val store = FakeStore()
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        repeat(ReviewController.SATISFYING_MOMENT_THRESHOLD) { controller.recordSatisfyingMoment() }

        assertEquals(1, prompter.requestCount)
    }

    @Test
    fun `the flag is written before the prompt fires`() {
        val store = FakeStore()
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        repeat(ReviewController.SATISFYING_MOMENT_THRESHOLD) { controller.recordSatisfyingMoment() }

        assertTrue(store.hasPrompted)
    }

    @Test
    fun `moments past the threshold never prompt again`() {
        val store = FakeStore()
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        repeat(ReviewController.SATISFYING_MOMENT_THRESHOLD + 5) { controller.recordSatisfyingMoment() }

        assertEquals(1, prompter.requestCount)
    }

    @Test
    fun `a fresh session that already prompted does not count moments or prompt again`() {
        val store = FakeStore(count = 0, hasPrompted = true)
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        controller.recordSatisfyingMoment()

        assertEquals(0, store.count)
        assertEquals(0, prompter.requestCount)
    }

    @Test
    fun `nonsense in the store reads as zero rather than blocking the count`() {
        val store = FakeStore(count = -4)
        val prompter = RecordingPrompter()
        val controller = ReviewController(store, prompter)

        controller.recordSatisfyingMoment()

        assertEquals(1, store.count)
        assertFalse(store.hasPrompted)
    }
}
