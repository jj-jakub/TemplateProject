package com.jj.templateproject.core.data.time

import com.jj.templateproject.domain.time.Clock
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import kotlin.time.TimeSource

/**
 * The platform's clocks.
 *
 * [nowMillis] converts `NSDate`'s seconds-since-epoch, which is a `Double`: the fractional part is
 * milliseconds and below, so truncating it is the conversion rather than a loss of anything the
 * caller asked for.
 *
 * [elapsedMillis] is measured from a mark taken when this clock is constructed, rather than from a
 * second `NSDate`, for the same reason the Android side avoids wall-clock time: a duration measured
 * with a clock the user or the network can correct comes back negative or wildly long. The origin
 * being "when the app built its clock" is fine, since the interface promises only that the reading
 * never moves backwards and is meaningless as an absolute.
 *
 * `TimeSource.Monotonic` rather than `kotlin.system.getTimeNanos`, which reads more directly but is
 * deprecated. The one promise it makes less firmly than Android's `elapsedRealtime` is behaviour
 * across device sleep, which Darwin's monotonic clocks do not all agree on; the interface's promise
 * holds either way.
 */
class IosSystemClock : Clock {

    private val origin = TimeSource.Monotonic.markNow()

    override fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * MILLIS_PER_SECOND).toLong()

    override fun elapsedMillis(): Long = origin.elapsedNow().inWholeMilliseconds

    private companion object {
        const val MILLIS_PER_SECOND = 1_000
    }
}
