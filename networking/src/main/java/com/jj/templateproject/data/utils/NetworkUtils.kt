package com.jj.templateproject.data.utils

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import retrofit2.Response
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps a Retrofit [Response] to a [BaseResult], deriving the success value from [onSuccess].
 *
 * Pass a transform (e.g. `{ it.code().toString() }`) for endpoints that return data, or `{ }`
 * for endpoints where only success/failure matters (a [Unit] result). A non-2xx response becomes
 * [NetworkError.Http].
 */
inline fun <T, R> Response<T>.toResult(onSuccess: (Response<T>) -> R): BaseResult<R, NetworkError> =
    if (isSuccessful) {
        BaseResult.Success(onSuccess(this))
    } else {
        BaseResult.Error(NetworkError.Http(code(), message()))
    }

/**
 * Runs a suspending Retrofit [apiCall] and maps both HTTP responses **and thrown exceptions** to a
 * [BaseResult], so network failures never escape the data layer as crashes.
 *
 * - non-2xx response  -> [NetworkError.Http]
 * - timeout           -> [NetworkError.Timeout]
 * - offline / DNS     -> [NetworkError.Connectivity]
 * - parse failure     -> [NetworkError.Serialization]
 * - anything else     -> [NetworkError.Unknown]
 *
 * [CancellationException] is rethrown so coroutine cancellation keeps working.
 */
suspend fun <T, R> safeApiCall(
    apiCall: suspend () -> Response<T>,
    onSuccess: (Response<T>) -> R,
): BaseResult<R, NetworkError> =
    try {
        apiCall().toResult(onSuccess)
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (timeout: SocketTimeoutException) {
        BaseResult.Error(NetworkError.Timeout)
    } catch (noHost: UnknownHostException) {
        BaseResult.Error(NetworkError.Connectivity)
    } catch (noConnection: ConnectException) {
        BaseResult.Error(NetworkError.Connectivity)
    } catch (io: IOException) {
        BaseResult.Error(NetworkError.Connectivity)
    } catch (serialization: SerializationException) {
        BaseResult.Error(NetworkError.Serialization(serialization.message))
    } catch (unexpected: Exception) {
        BaseResult.Error(NetworkError.Unknown(unexpected.message))
    }
