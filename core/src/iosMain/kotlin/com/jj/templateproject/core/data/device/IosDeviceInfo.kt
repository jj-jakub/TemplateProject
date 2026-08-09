package com.jj.templateproject.core.data.device

import com.jj.templateproject.domain.device.DeviceInfo
import platform.Foundation.NSLocale
import platform.Foundation.countryCode
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.UIKit.UIDevice
import platform.UIKit.UIUserInterfaceIdiomPad

/**
 * The iOS half of [DeviceInfo], read straight from UIKit and Foundation.
 *
 * [model] is the device *class* (`iPhone`, `iPad`), not the marketing name its Android counterpart
 * reports. The identifier that would name the hardware exactly comes from `uname`/`sysctl` and looks
 * like `iPhone15,2`, which needs a lookup table to become anything a human recognises and goes stale
 * with every release. The coarse answer is honest without that maintenance.
 */
class IosDeviceInfo : DeviceInfo {

    override val model: String get() = UIDevice.currentDevice.model

    /**
     * `iOS 17.4`, built from the two `UIDevice` reads rather than
     * `NSProcessInfo.operatingSystemVersionString`: that one answers with `Version 17.4 (Build
     * 21E219)`, a string Apple documents as being for display only and not to be parsed, and whose
     * shape reads nothing like the Android side's.
     *
     * There is no equivalent of the Android API level in the parentheses, because iOS has no
     * separate SDK number to report: the version *is* the compatibility answer.
     */
    override val osVersion: String
        get() = with(UIDevice.currentDevice) { "$systemName $systemVersion" }

    /**
     * The locale as a BCP 47 tag, so the two platforms report the same shape of string.
     *
     * Composed from the language and region rather than taken from `localeIdentifier`, which is an
     * ICU identifier (`en_GB`, and `zh-Hans_HK` for a locale with a script): underscore-separated,
     * and carrying keyword suffixes such as `@calendar=` that no consumer of this field wants.
     */
    override val locale: String
        get() = with(NSLocale.currentLocale) { bcp47Tag(languageCode, countryCode) }

    override val isTablet: Boolean
        get() = UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad
}

/**
 * Joins a language and an optional region into a BCP 47 tag.
 *
 * Both halves are declared nullable by the Objective-C API and are genuinely empty for a locale
 * with no region (`en`, as against `en-GB`), so a naive join produces the trailing separator
 * `en-` that a tag parser rejects.
 */
internal fun bcp47Tag(languageCode: String?, countryCode: String?): String {
    val language = languageCode.orEmpty()
    val region = countryCode.orEmpty()
    return when {
        language.isEmpty() -> ""
        region.isEmpty() -> language
        else -> "$language-$region"
    }
}
