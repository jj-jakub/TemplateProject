package com.jj.templateproject.domain.crosspromo

import com.jj.templateproject.domain.config.RemoteFlags
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.Test

class GetCrossPromoConfigUseCaseTest {

    private class FakeRemoteFlags(
        private val bools: Map<String, Boolean> = emptyMap(),
        private val strings: Map<String, String> = emptyMap(),
    ) : RemoteFlags {
        override fun int(key: String, default: Int) = default
        override fun bool(key: String, default: Boolean) = bools[key] ?: default
        override fun string(key: String, default: String) = strings[key] ?: default
    }

    @Test
    fun `disabled resolves to null`() {
        val useCase = GetCrossPromoConfigUseCase(FakeRemoteFlags(bools = mapOf("cross_promo_enabled" to false)))

        assertNull(useCase())
    }

    @Test
    fun `not configured at all resolves to null`() {
        val useCase = GetCrossPromoConfigUseCase(FakeRemoteFlags())

        assertNull(useCase())
    }

    @Test
    fun `enabled with a blank label resolves to null`() {
        val flags = FakeRemoteFlags(
            bools = mapOf("cross_promo_enabled" to true),
            strings = mapOf("cross_promo_label" to "", "cross_promo_target" to "com.example.other"),
        )

        assertNull(GetCrossPromoConfigUseCase(flags)())
    }

    @Test
    fun `enabled with an unresolvable target resolves to null`() {
        val flags = FakeRemoteFlags(
            bools = mapOf("cross_promo_enabled" to true),
            strings = mapOf("cross_promo_label" to "Try our other app", "cross_promo_target" to "not a package"),
        )

        assertNull(GetCrossPromoConfigUseCase(flags)())
    }

    @Test
    fun `fully configured resolves to a config with the resolved store URL`() {
        val flags = FakeRemoteFlags(
            bools = mapOf("cross_promo_enabled" to true),
            strings = mapOf("cross_promo_label" to "Try our other app", "cross_promo_target" to "com.example.other"),
        )

        assertEquals(
            CrossPromoConfig(
                label = "Try our other app",
                target = "https://play.google.com/store/apps/details?id=com.example.other",
            ),
            GetCrossPromoConfigUseCase(flags)(),
        )
    }
}
