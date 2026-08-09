package com.jj.templateproject.domain.google.exception

import kotlin.test.assertEquals
import kotlin.test.Test

class NetworkErrorTest {

    @Test
    fun `http falls back to a code-based message when none is provided`() {
        assertEquals("HTTP error 500", NetworkError.Http(500).message)
        assertEquals("HTTP error 500", NetworkError.Http(500, "   ").message)
        assertEquals("Server exploded", NetworkError.Http(500, "Server exploded").message)
    }

    @Test
    fun `connectivity and timeout expose stable messages`() {
        assertEquals("No network connection", NetworkError.Connectivity.message)
        assertEquals("The request timed out", NetworkError.Timeout.message)
    }

    @Test
    fun `serialization and unknown use the detail when present`() {
        assertEquals("Failed to parse the server response", NetworkError.Serialization().message)
        assertEquals("boom", NetworkError.Serialization("boom").message)
        assertEquals("An unexpected error occurred", NetworkError.Unknown().message)
        assertEquals("oops", NetworkError.Unknown("oops").message)
    }
}
