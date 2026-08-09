package com.jj.templateproject.core.data.device

import android.content.Context
import android.os.Build
import com.jj.templateproject.domain.device.DeviceInfo

class AndroidDeviceInfo(
    private val context: Context,
) : DeviceInfo {

    override val model: String get() = "${Build.MANUFACTURER} ${Build.MODEL}".trim()

    override val osVersion: String get() = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    override val locale: String
        get() = context.resources.configuration.locales
            .takeIf { !it.isEmpty }
            ?.get(0)
            ?.toLanguageTag()
            .orEmpty()

    /**
     * Read from `smallestScreenWidthDp`, which is the shorter of the two screen dimensions and so
     * does not change when the device is rotated. That is the whole reason to use it: deriving this
     * from the current window size makes a phone in landscape report as a tablet, and makes a tablet
     * in split-screen report as a phone.
     */
    override val isTablet: Boolean
        get() = context.resources.configuration.smallestScreenWidthDp >= TABLET_SMALLEST_WIDTH_DP

    private companion object {
        /** The sw600dp resource qualifier, in code, so both agree on where the boundary is. */
        const val TABLET_SMALLEST_WIDTH_DP = 600
    }
}
