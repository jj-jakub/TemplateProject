package com.jj.templateproject.core.data.crosspromo

import com.jj.templateproject.domain.crosspromo.UrlOpener
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

/**
 * `UIApplication.openURL` at an `https://apps.apple.com/…` URL — the App Store app itself
 * intercepts its own listing links the same way Play intercepts `play.google.com` ones. See
 * `CrossPromoTarget`'s doc comment for which raw target strings resolve to which store's URL.
 */
class IosUrlOpener : UrlOpener {

    override fun open(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}
