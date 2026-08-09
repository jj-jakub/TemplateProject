package com.jj.templateproject.domain.review

/**
 * Where the banked satisfying-moment count and the "already asked" flag live. Deliberately a plain
 * synchronous seam, the same shape as `LaunchAttemptStore`: [ReviewController] is called from
 * wherever a satisfying moment happens, which should never have to await a persistence round-trip
 * to keep counting correctly.
 */
interface ReviewPromptStore {
    fun readSatisfyingMomentCount(): Int
    fun writeSatisfyingMomentCount(count: Int)
    fun readHasPrompted(): Boolean
    fun writeHasPrompted(hasPrompted: Boolean)
}

/**
 * Asks for an app-store review **at most once, ever**, and only after the player has hit enough
 * genuinely satisfying moments to make the ask welcome rather than intrusive — a completed level, a
 * high score beaten, a save that mattered, whatever a branching app defines as "this went well".
 * Call [recordSatisfyingMoment] from each of those moments; this decides when (and whether) to
 * actually prompt.
 *
 * The store is a raw count rather than a `Set` of moment kinds: this class does not care what made
 * a moment satisfying, only that [SATISFYING_MOMENT_THRESHOLD] of them have happened. A caller that
 * wants "N *distinct kinds* of satisfying moment" rather than "N satisfying moments total" should
 * dedupe before calling in.
 */
class ReviewController(
    private val store: ReviewPromptStore,
    private val reviewPrompter: ReviewPrompter,
) {

    fun recordSatisfyingMoment() {
        if (store.readHasPrompted()) return

        val count = (store.readSatisfyingMomentCount() + 1).coerceAtLeast(1)
        store.writeSatisfyingMomentCount(count)

        if (count >= SATISFYING_MOMENT_THRESHOLD) {
            // Written before the prompt fires, not after: the review flow can finish without ever
            // calling back (the app is backgrounded, the process dies mid-flow), and the very next
            // satisfying moment must not re-trigger it just because nothing confirmed completion.
            store.writeHasPrompted(true)
            reviewPrompter.requestReview()
        }
    }

    companion object {
        /** Satisfying moments banked before the review ask fires. */
        const val SATISFYING_MOMENT_THRESHOLD = 3
    }
}
