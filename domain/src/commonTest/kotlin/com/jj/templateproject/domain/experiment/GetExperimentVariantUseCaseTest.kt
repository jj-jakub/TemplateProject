package com.jj.templateproject.domain.experiment

import com.jj.templateproject.domain.analytics.AnalyticsLogger
import kotlin.test.assertEquals
import kotlin.test.Test

class GetExperimentVariantUseCaseTest {

    private class FakeInstallIdStore(private var id: String? = "fixed-id") : InstallIdStore {
        override fun readInstallId() = id
        override fun writeInstallId(id: String) {
            this.id = id
        }
    }

    private class RecordingLogger : AnalyticsLogger {
        var lastName: String? = null
        var lastParams: Map<String, String> = emptyMap()

        override fun logEvent(name: String, params: Map<String, String>) {
            lastName = name
            lastParams = params
        }
    }

    @Test
    fun `the returned variant matches ExperimentBucketing's own assignment`() {
        val bucketing = ExperimentBucketing(FakeInstallIdStore())
        val logger = RecordingLogger()
        val useCase = GetExperimentVariantUseCase(bucketing, logger)

        val variant = useCase("onboarding_v2", variantCount = 3)

        assertEquals(bucketing.variantFor("onboarding_v2", variantCount = 3), variant)
    }

    @Test
    fun `an exposure event is logged with the experiment key and the resolved variant`() {
        val bucketing = ExperimentBucketing(FakeInstallIdStore())
        val logger = RecordingLogger()
        val useCase = GetExperimentVariantUseCase(bucketing, logger)

        val variant = useCase("onboarding_v2", variantCount = 3)

        assertEquals("experiment_exposure", logger.lastName)
        assertEquals(
            mapOf("experiment" to "onboarding_v2", "variant" to variant.toString()),
            logger.lastParams,
        )
    }
}
