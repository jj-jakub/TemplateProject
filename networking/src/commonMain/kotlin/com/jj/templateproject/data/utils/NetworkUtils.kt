package com.jj.templateproject.data.utils

import com.jj.templateproject.data.network.classifyTransportFailure
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

/**
 * Maps a resolved Ktor [HttpResponse] to a [BaseResult], deriving the success value from
 * [onSuccess].
 *
 * Pass a transform (e.g. `{ it.status.value.toString() }`) for endpoints that return data, or
 * `{ }` for endpoints where only success/failure matters (a [Unit] result). A non-2xx response
 * becomes [NetworkError.Http].
 */
inline fun <R> HttpResponse.toResult(onSuccess: (HttpResponse) -> R): BaseResult<R, NetworkError> =
    if (status.isSuccess()) {
        BaseResult.Success(onSuccess(this))
    } else {
        BaseResult.Error(NetworkError.Http(status.value, status.description))
    }

/**
 * Runs a suspending [apiCall] and maps both HTTP responses **and thrown exceptions** to a
 * [BaseResult], so network failures never escape the data layer as crashes.
 *
 * - non-2xx response  -> [NetworkError.Http]
 * - timeout           -> [NetworkError.Timeout] (Ktor's own timeout exceptions first, then
 *   whatever the platform's own engine throws for a timeout it did not translate — see
 *   [classifyTransportFailure])
 * - offline / DNS     -> [NetworkError.Connectivity]
 * - parse failure     -> [NetworkError.Serialization]
 * - anything else     -> [NetworkError.Unknown]
 *
 * [CancellationException] is rethrown so coroutine cancellation keeps working.
 */
// Intentionally classifies each exception into a typed NetworkError instead of rethrowing it;
// the broad final catch is the deliberate "Unknown" fallback.
@Suppress("SwallowedException", "TooGenericExceptionCaught")
suspend fun <R> safeApiCall(
    apiCall: suspend () -> HttpResponse,
    onSuccess: (HttpResponse) -> R,
): BaseResult<R, NetworkError> =
    try {
        apiCall().toResult(onSuccess)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (timeout: HttpRequestTimeoutException) {
        BaseResult.Error(NetworkError.Timeout)
    } catch (timeout: ConnectTimeoutException) {
        BaseResult.Error(NetworkError.Timeout)
    } catch (timeout: SocketTimeoutException) {
        BaseResult.Error(NetworkError.Timeout)
    } catch (serialization: SerializationException) {
        BaseResult.Error(NetworkError.Serialization(serialization.message))
    } catch (unexpected: Exception) {
        val transportFailure = classifyTransportFailure(unexpected)
        if (transportFailure != null) {
            BaseResult.Error(transportFailure)
        } else {
            BaseResult.Error(NetworkError.Unknown(unexpected.message))
        }
    }
