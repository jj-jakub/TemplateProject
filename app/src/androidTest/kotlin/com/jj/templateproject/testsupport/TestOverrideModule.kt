package com.jj.templateproject.testsupport

import com.jj.templateproject.domain.ad.AdManager
import com.jj.templateproject.domain.google.TemplateRepository
import org.koin.dsl.module

/**
 * Koin module loaded last by [com.jj.templateproject.HermeticTestApplication] to override the real
 * network repository and ad manager with deterministic fakes (override is enabled via
 * `allowOverride(true)`).
 */
val testOverrideModule = module {
    single<TemplateRepository> { FakeTemplateRepository() }
    single<AdManager> { NoOpAdManager() }
}
