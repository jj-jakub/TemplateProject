package com.jj.templateproject.core.data.experiment

import com.jj.templateproject.domain.experiment.InstallIdStore
import platform.Foundation.NSUserDefaults

/** The generated install id, in `NSUserDefaults` — same shape as `UserDefaultsLaunchAttemptStore`. */
class UserDefaultsInstallIdStore(
    private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults,
) : InstallIdStore {

    override fun readInstallId(): String? = defaults.stringForKey(KEY_INSTALL_ID)

    override fun writeInstallId(id: String) {
        defaults.setObject(id, KEY_INSTALL_ID)
    }

    private companion object {
        const val KEY_INSTALL_ID = "experiment_bucketing.install_id"
    }
}
