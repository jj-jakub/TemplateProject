package com.jj.templateproject.domain.experiment

/**
 * Where the opaque per-install identifier behind [ExperimentBucketing] is kept. Not a user
 * identity: it exists only to keep the same experiment variant assignment stable across app
 * launches for one install, and it is never sent anywhere except as an anonymous hash input — no
 * analytics event carries the id itself, only the variant index it resolves to.
 */
interface InstallIdStore {
    fun readInstallId(): String?
    fun writeInstallId(id: String)
}
