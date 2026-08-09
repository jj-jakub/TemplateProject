package com.jj.templateproject.data.network

import com.jj.templateproject.domain.google.exception.NetworkError
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * OkHttp's exact exception vocabulary, ported unchanged from the Retrofit-era test of the same
 * behaviour: what to expect is not a guess, it is what OkHttp is documented to throw for each
 * failure mode.
 */
class NetworkTransportClassificationAndroidTest {

    @Test
    fun `a socket timeout maps to Timeout`() {
        assertEquals(NetworkError.Timeout, classifyTransportFailure(SocketTimeoutException()))
    }

    @Test
    fun `a call-level timeout (bare InterruptedIOException) maps to Timeout`() {
        // OkHttp's callTimeout throws a plain InterruptedIOException, not a SocketTimeoutException.
        assertEquals(NetworkError.Timeout, classifyTransportFailure(InterruptedIOException("timeout")))
    }

    @Test
    fun `an unknown host maps to Connectivity`() {
        assertEquals(NetworkError.Connectivity, classifyTransportFailure(UnknownHostException()))
    }

    @Test
    fun `a connect failure maps to Connectivity`() {
        assertEquals(NetworkError.Connectivity, classifyTransportFailure(ConnectException()))
    }

    @Test
    fun `a generic IO failure maps to Connectivity`() {
        assertEquals(NetworkError.Connectivity, classifyTransportFailure(IOException("stream closed")))
    }

    @Test
    fun `an exception this platform does not recognise returns null`() {
        assertNull(classifyTransportFailure(IllegalStateException("weird")))
    }
}
