package com.jj.templateproject.domain.app

interface AppInfoRepository {
    suspend fun installedFromValidSource(): Boolean
}

/**
 * A stand-in for a real install-source check (e.g. the Play Integrity API on Android, receipt
 * validation on iOS): both of Android's `DefaultAppInfoRepository` variants (debug/release) return
 * `true` unconditionally today, so iOS's `IosKoin` binds this rather than duplicating that same
 * placeholder a third time. Swap for a real per-platform implementation when one exists.
 */
object AlwaysInstalledFromValidSource : AppInfoRepository {
    override suspend fun installedFromValidSource() = true
}
