package com.jj.templateproject.domain.experiment

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.Test

class ExperimentBucketingTest {

    private class FakeInstallIdStore(private var id: String? = null) : InstallIdStore {
        override fun readInstallId() = id
        override fun writeInstallId(id: String) {
            this.id = id
        }
    }

    @Test
    fun `the same experiment key resolves to the same variant on repeated calls`() {
        val bucketing = ExperimentBucketing(FakeInstallIdStore())

        val first = bucketing.variantFor("onboarding_v2", variantCount = 2)
        val second = bucketing.variantFor("onboarding_v2", variantCount = 2)

        assertEquals(first, second)
    }

    @Test
    fun `a fresh install id is generated once and reused for every later call`() {
        val store = FakeInstallIdStore()
        val bucketing = ExperimentBucketing(store)

        bucketing.variantFor("a", variantCount = 2)
        val idAfterFirstCall = store.readInstallId()
        bucketing.variantFor("b", variantCount = 2)

        assertEquals(idAfterFirstCall, store.readInstallId())
    }

    @Test
    fun `an existing install id is never overwritten`() {
        val store = FakeInstallIdStore(id = "fixed-id")
        val bucketing = ExperimentBucketing(store)

        bucketing.variantFor("a", variantCount = 2)

        assertEquals("fixed-id", store.readInstallId())
    }

    @Test
    fun `the variant always falls within 0 until variantCount`() {
        val bucketing = ExperimentBucketing(FakeInstallIdStore(id = "fixed-id"))

        repeat(20) { index ->
            val variant = bucketing.variantFor("experiment_$index", variantCount = 3)
            assertTrue(variant in 0..2, "variant $variant out of range for experiment_$index")
        }
    }

    @Test
    fun `different install ids can land in different buckets for the same experiment`() {
        val variants = (0 until 20).map { index ->
            ExperimentBucketing(FakeInstallIdStore(id = "install_$index")).variantFor("a", variantCount = 2)
        }

        assertTrue(variants.toSet().size > 1, "expected more than one distinct variant across installs")
    }

    @Test
    fun `a non-positive variant count is rejected`() {
        val bucketing = ExperimentBucketing(FakeInstallIdStore(id = "fixed-id"))

        assertFailsWith<IllegalArgumentException> { bucketing.variantFor("a", variantCount = 0) }
    }
}
