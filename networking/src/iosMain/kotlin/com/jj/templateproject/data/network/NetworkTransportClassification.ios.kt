package com.jj.templateproject.data.network

import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.io.IOException

/**
 * The iOS side of transport classification.
 *
 * Best-effort, and deliberately broader than the Android actual: Ktor's Darwin engine wraps most
 * NSURLSession-level connection failures (DNS, unreachable host, no connectivity) as a
 * `kotlinx.io.IOException` reaching this point, so anything of that type is read as
 * [NetworkError.Connectivity]. Unlike the Android classification, which is pinned by unit tests
 * against the exact exception types OkHttp is documented to throw, this has not been exercised
 * against a real device or simulator under actual network-loss conditions (airplane mode, DNS
 * failure) — that verification is still owed, consistent with every other iOS runtime behaviour in
 * this codebase.
 */
actual fun classifyTransportFailure(throwable: Throwable): NetworkError? = when (throwable) {
    is IOException -> NetworkError.Connectivity
    else -> null
}
