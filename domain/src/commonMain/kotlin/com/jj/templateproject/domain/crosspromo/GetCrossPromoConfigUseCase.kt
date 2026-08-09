package com.jj.templateproject.domain.crosspromo

import com.jj.templateproject.domain.config.RemoteFlags

/**
 * Reads the published cross-promo slot, or `null` when it's off or incompletely configured. The
 * `enabled` flag is the kill switch: flipping it off removes the row from the app with no release,
 * the same lever `RemoteFlags`' own doc comment describes for a gate that already exists in one
 * place.
 */
class GetCrossPromoConfigUseCase(
    private val remoteFlags: RemoteFlags,
) {
    operator fun invoke(): CrossPromoConfig? {
        if (!remoteFlags.bool(KEY_ENABLED, false)) return null
        val label = remoteFlags.string(KEY_LABEL, "")
        if (label.isBlank()) return null
        val target = remoteFlags.string(KEY_TARGET, "")
        val url = CrossPromoTarget.resolveUrl(target) ?: return null
        return CrossPromoConfig(label = label, target = url)
    }

    private companion object {
        const val KEY_ENABLED = "cross_promo_enabled"
        const val KEY_LABEL = "cross_promo_label"
        const val KEY_TARGET = "cross_promo_target"
    }
}
