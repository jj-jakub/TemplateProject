package com.jj.templateproject.domain.experiment

import com.jj.templateproject.domain.analytics.AnalyticsLogger

/**
 * Reads this install's variant for [experimentKey] and logs an exposure event, so an experiment's
 * results can be sliced by which variant a user actually saw. Call this once, right before the
 * variant first affects anything visible — from a background/startup path "assigned" and "exposed"
 * are not the same event, and only the latter belongs in an experiment's analysis.
 */
class GetExperimentVariantUseCase(
    private val experimentBucketing: ExperimentBucketing,
    private val analyticsLogger: AnalyticsLogger,
) {
    operator fun invoke(experimentKey: String, variantCount: Int): Int {
        val variant = experimentBucketing.variantFor(experimentKey, variantCount)
        analyticsLogger.logEvent(
            name = EXPOSURE_EVENT_NAME,
            params = mapOf("experiment" to experimentKey),
            metrics = mapOf("variant" to variant.toLong()),
        )
        return variant
    }

    private companion object {
        const val EXPOSURE_EVENT_NAME = "experiment_exposure"
    }
}
