package com.jj.templateproject.data.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigValue
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Everything here is one rule seen from different angles: only a genuinely published value overrides
 * the caller's in-code default. Remote Config answers every read whether or not a key exists, so
 * without the source check an unpublished key would silently mean 0, false and "".
 */
class FirebaseRemoteFlagsTest {

    private fun configReturning(value: FirebaseRemoteConfigValue): FirebaseRemoteConfig =
        mockk<FirebaseRemoteConfig>().also { every { it.getValue(any()) } returns value }

    private fun published(build: FirebaseRemoteConfigValue.() -> Unit) =
        mockk<FirebaseRemoteConfigValue>().also {
            every { it.source } returns FirebaseRemoteConfig.VALUE_SOURCE_REMOTE
            it.build()
        }

    private fun unpublished() = mockk<FirebaseRemoteConfigValue>().also {
        every { it.source } returns FirebaseRemoteConfig.VALUE_SOURCE_STATIC
        every { it.asLong() } returns 0L
        every { it.asBoolean() } returns false
        every { it.asString() } returns ""
    }

    @Test
    fun `a published number overrides the default`() {
        val flags = FirebaseRemoteFlags(configReturning(published { every { asLong() } returns 90L }))

        assertEquals(90, flags.int("network_timeout_seconds", 30))
    }

    @Test
    fun `a published boolean overrides the default`() {
        val flags = FirebaseRemoteFlags(configReturning(published { every { asBoolean() } returns false }))

        assertEquals(false, flags.bool("feature_enabled", true))
    }

    @Test
    fun `an unpublished key keeps the caller's default instead of reading as zero`() {
        val flags = FirebaseRemoteFlags(configReturning(unpublished()))

        assertEquals(30, flags.int("network_timeout_seconds", 30))
        assertTrue(flags.bool("feature_enabled", true))
        assertEquals("https://example.com", flags.string("base_url", "https://example.com"))
    }

    @Test
    fun `a published value of the wrong type degrades to the default, not to zero`() {
        // A console typo (text where a number belongs) must not become a shipped behaviour change.
        val flags = FirebaseRemoteFlags(
            configReturning(published { every { asLong() } throws IllegalArgumentException("not a number") }),
        )

        assertEquals(30, flags.int("network_timeout_seconds", 30))
    }

    @Test
    fun `a published but empty string is treated as nothing published`() {
        val flags = FirebaseRemoteFlags(configReturning(published { every { asString() } returns "" }))

        assertEquals("fallback", flags.string("message", "fallback"))
    }
}
