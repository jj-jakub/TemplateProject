package com.jj.templateproject.domain.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * The property worth pinning is the fallback, not the plumbing: every caller passes its own in-code
 * constant as the default, so a build with nothing published has to behave exactly as it would if
 * remote configuration did not exist at all.
 */
class RemoteFlagsTest {

    @Test
    fun `nothing published means every caller keeps its own default`() {
        assertEquals(30, NoOpRemoteFlags.int("network_timeout_seconds", 30))
        assertTrue(NoOpRemoteFlags.bool("feature_enabled", true))
        assertEquals("https://example.com", NoOpRemoteFlags.string("base_url", "https://example.com"))
    }

    @Test
    fun `the default is returned as given, including values that would look empty`() {
        // A flag whose default is 0, false or "" must still come back unchanged: a no-op layer that
        // "helpfully" substituted something here would be a silent behaviour change.
        assertEquals(0, NoOpRemoteFlags.int("retries", 0))
        assertEquals(false, NoOpRemoteFlags.bool("beta_enabled", false))
        assertEquals("", NoOpRemoteFlags.string("override_message", ""))
    }
}
