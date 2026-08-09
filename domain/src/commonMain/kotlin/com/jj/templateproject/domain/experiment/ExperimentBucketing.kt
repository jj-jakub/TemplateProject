package com.jj.templateproject.domain.experiment

import kotlin.random.Random

/**
 * Deterministic, stable-per-install variant assignment for A/B experiments. The same
 * [experimentKey] always resolves to the same variant for this install, for as long as the id in
 * [InstallIdStore] survives (i.e. until the app is uninstalled or its data is cleared) — an install
 * does not get re-bucketed on every launch, which would make any experiment result meaningless.
 *
 * Pairs with `RemoteFlags`: the variant this returns is meant to key a remotely-published value
 * (e.g. `remoteFlags.bool("new_onboarding_${key}_variant_$variant", default = false)`), not to
 * drive behavior on its own — this class only answers "which bucket is this install in", nothing
 * about what a bucket should do differently.
 */
class ExperimentBucketing(
    private val installIdStore: InstallIdStore,
) {

    /** [experimentKey]'s bucket for this install, in `[0, variantCount)`. */
    fun variantFor(experimentKey: String, variantCount: Int): Int {
        require(variantCount > 0) { "variantCount must be positive, was $variantCount" }
        val hash = (installId() + experimentKey).hashCode()
        // Int.mod (not %) always returns a non-negative result, since hashCode() can be negative.
        return hash.mod(variantCount)
    }

    private fun installId(): String {
        installIdStore.readInstallId()?.let { return it }
        val generated = generateInstallId()
        installIdStore.writeInstallId(generated)
        return generated
    }

    private fun generateInstallId(): String =
        List(INSTALL_ID_SEGMENTS) { Random.nextInt().toUInt().toString(radix = 16) }.joinToString("-")

    private companion object {
        const val INSTALL_ID_SEGMENTS = 4
    }
}
