package com.jj.templateproject.domain.crosspromo

/**
 * A slot pointing at another app from the same publisher. Published entirely through
 * [com.jj.templateproject.domain.config.RemoteFlags], not persisted state: turning promotion off
 * (or repointing it at a different app) is a console change, never a release.
 *
 * @param label the row/pill text, e.g. "Try our other app".
 * @param target an already-resolved `https://play.google.com/…` URL (see [CrossPromoTarget]) —
 *   always safe to open as-is; [GetCrossPromoConfigUseCase] is the only place that constructs one,
 *   and it never returns a config whose target failed to resolve.
 */
data class CrossPromoConfig(
    val label: String,
    val target: String,
)
