package com.jj.templateproject.domain.time

/**
 * Time, behind a seam, so that anything which depends on "now" can be tested without waiting and
 * without a device.
 *
 * The two readings are not interchangeable, and picking the wrong one is a real bug rather than a
 * style preference:
 *
 * - [nowMillis] is wall-clock time. Use it for a value that has to mean something outside this
 *   process (a timestamp on a record, a date shown to a user). It can jump, backwards included, when
 *   the user or the network corrects the clock.
 * - [elapsedMillis] is monotonic. Use it for **every** duration: a stopwatch built on wall-clock
 *   time reports a negative or wildly long interval the moment the clock is corrected mid-measure.
 */
interface Clock {

    /** Milliseconds since the Unix epoch, UTC. */
    fun nowMillis(): Long

    /** Milliseconds from an arbitrary origin that only ever increases. Meaningless as an absolute. */
    fun elapsedMillis(): Long
}

/**
 * A clock that does not move unless a test moves it. Lives in main rather than a test source set so
 * that every module's tests can share one, the same way the no-op reporters are shared.
 */
class FixedClock(
    private var now: Long = 0L,
    private var elapsed: Long = 0L,
) : Clock {

    override fun nowMillis(): Long = now

    override fun elapsedMillis(): Long = elapsed

    /** Moves both readings forward together, which is what an undisturbed clock does. */
    fun advance(millis: Long) {
        now += millis
        elapsed += millis
    }

    /** Moves wall-clock time only, for the case a monotonic reading exists to survive. */
    fun setWallClock(millis: Long) {
        now = millis
    }
}
