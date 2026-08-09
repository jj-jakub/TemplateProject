package com.jj.templateproject.domain.device

/**
 * What the app is running on. Behind a seam because every one of these is a platform read that would
 * otherwise pull `android.os.Build` into whatever wants to log or branch on it.
 */
interface DeviceInfo {

    /** Marketing-ish device name, e.g. `Pixel 8`. For crash breadcrumbs and support reports. */
    val model: String

    /** Human-readable platform version, e.g. `Android 14 (API 34)`. */
    val osVersion: String

    /** The device's primary locale as a BCP 47 tag, e.g. `en-GB`. */
    val locale: String

    /**
     * Whether this is a tablet-class device.
     *
     * A **device** question, deliberately not derived from the window's current size. Window
     * constraints shift with orientation, split-screen and free-form windowing, so a layout that
     * asks them gets a different answer for the same device from one moment to the next. Ask this
     * when the answer should be stable for the hardware, and ask the window when the answer should
     * follow the space actually available.
     */
    val isTablet: Boolean
}

/** Fixed answers for tests, so nothing has to stand up a device to read one field. */
data class FixedDeviceInfo(
    override val model: String = "Test Device",
    override val osVersion: String = "Android 14 (API 34)",
    override val locale: String = "en-US",
    override val isTablet: Boolean = false,
) : DeviceInfo
