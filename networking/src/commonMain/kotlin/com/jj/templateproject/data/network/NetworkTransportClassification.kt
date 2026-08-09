package com.jj.templateproject.data.network

import com.jj.templateproject.domain.google.exception.NetworkError

/**
 * Reads a thrown [Throwable] as a transport-level failure ("no connection"), or null when it is
 * not one this platform recognises.
 *
 * There is no single multiplatform exception type that safely means "the network is unreachable"
 * without also swallowing unrelated failures: OkHttp on Android throws `java.io.IOException` and
 * its subtypes for a DNS/connect failure, and Darwin on iOS surfaces the equivalent as its own
 * platform-native type. Catching `kotlinx.io.IOException` in common code would miss the Android
 * ones entirely, since `java.io.IOException` is a different, unrelated class despite the similar
 * name. So each platform is asked directly, using the exact exception types its own engine throws.
 *
 * Timeouts and serialization failures do NOT go through this: Ktor's own timeout exceptions
 * (`HttpRequestTimeoutException`, `ConnectTimeoutException`, `SocketTimeoutException`) and
 * `kotlinx.serialization.SerializationException` are genuinely multiplatform, so [safeApiCall]
 * classifies those directly in common code and only reaches here for everything else.
 */
expect fun classifyTransportFailure(throwable: Throwable): NetworkError?
