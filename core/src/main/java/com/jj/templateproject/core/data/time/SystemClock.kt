package com.jj.templateproject.core.data.time

import com.jj.templateproject.domain.time.Clock
import android.os.SystemClock as AndroidSystemClock

/**
 * The platform's clocks.
 *
 * [elapsedMillis] uses `elapsedRealtime`, not `uptimeMillis`: the latter stops while the device is in
 * deep sleep, so a duration measured across a screen-off period comes back far too short.
 */
class SystemClock : Clock {
    override fun nowMillis(): Long = System.currentTimeMillis()
    override fun elapsedMillis(): Long = AndroidSystemClock.elapsedRealtime()
}
