package com.jj.templateproject.core.data.experiment

import android.content.Context
import com.jj.templateproject.domain.experiment.InstallIdStore

/**
 * The generated install id, in its own SharedPreferences file — same rationale as
 * `SharedPreferencesLaunchAttemptStore`'s own file: a restored backup should not carry one
 * install's experiment bucketing onto a device that never generated its own id (see
 * `backup_rules.xml`).
 */
class SharedPreferencesInstallIdStore(
    context: Context,
) : InstallIdStore {

    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    override fun readInstallId(): String? = preferences.getString(KEY_INSTALL_ID, null)

    override fun writeInstallId(id: String) {
        preferences.edit().putString(KEY_INSTALL_ID, id).apply()
    }

    companion object {
        /** Named in backup_rules.xml and data_extraction_rules.xml; keep the three in step. */
        const val FILE_NAME = "experiment_bucketing"
        private const val KEY_INSTALL_ID = "install_id"
    }
}
