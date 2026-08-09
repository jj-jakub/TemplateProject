package com.jj.templateproject.core.di

import com.jj.templateproject.domain.experiment.ExperimentBucketing
import com.jj.templateproject.domain.experiment.GetExperimentVariantUseCase
import com.jj.templateproject.domain.google.GetGoogleDataUseCase
import com.jj.templateproject.domain.google.GetGoogleStatusUseCase
import com.jj.templateproject.domain.reliability.LaunchStability
import com.jj.templateproject.domain.review.ReviewController
import com.jj.templateproject.domain.theme.GetThemeModeUseCase
import com.jj.templateproject.domain.theme.SetThemeModeUseCase
import org.koin.dsl.module

/**
 * Everything this module binds that needs nothing from the platform.
 *
 * The split is by what a binding *needs*, not by what it is: a use case takes repositories, and
 * [LaunchStability] takes a `LaunchAttemptStore` it resolves through `get()`, so all of them can be
 * constructed identically on every target. The bindings that genuinely differ (a notification
 * surface, a preference file, a share sheet) live in [platformCoreModule], which is why this one
 * needs no `androidContext()` and therefore no Android-only Koin artifact.
 *
 * Both halves are always used together: `modules(coreModule, platformCoreModule())`.
 */
val coreModule = module {
    single { GetGoogleStatusUseCase(templateRepository = get()) }
    single { GetGoogleDataUseCase(templateRepository = get()) }
    single { GetThemeModeUseCase(appPreferencesRepository = get()) }
    single { SetThemeModeUseCase(appPreferencesRepository = get()) }
    // Launch-scoped: the app resolves the mode once at startup, and anything that restores
    // persisted state consults it before doing so.
    single { LaunchStability(store = get()) }
    single { ReviewController(store = get(), reviewPrompter = get()) }
    single { ExperimentBucketing(installIdStore = get()) }
    single { GetExperimentVariantUseCase(experimentBucketing = get(), analyticsLogger = get()) }
}
