package com.jj.templateproject.domain.crosspromo

/** Opens a URL in whatever the platform's own browser/store-app handoff is. */
interface UrlOpener {
    fun open(url: String)
}

/** Does nothing, for a build or a test with no platform surface to hand a URL to. */
object NoOpUrlOpener : UrlOpener {
    override fun open(url: String) = Unit
}
