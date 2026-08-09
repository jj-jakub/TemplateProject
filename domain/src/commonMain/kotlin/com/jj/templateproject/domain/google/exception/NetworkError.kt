package com.jj.templateproject.domain.google.exception

import com.jj.templateproject.domain.BaseError

/**
 * Typed networking failures, so screens can react differently to "you're offline" vs. "the
 * server is down" vs. "we couldn't read the response" instead of one opaque code/message.
 *
 * Every variant exposes a non-null, human-readable [message] safe to surface or log.
 */
sealed interface NetworkError : BaseError {

    val message: String

    /** A non-2xx HTTP response was received. */
    data class Http(val code: Int, val httpMessage: String? = null) : NetworkError {
        override val message: String
            get() = httpMessage?.takeIf { it.isNotBlank() } ?: "HTTP error $code"
    }

    /** No connectivity — DNS/connect failure, airplane mode, etc. */
    data object Connectivity : NetworkError {
        override val message: String get() = "No network connection"
    }

    /** The request did not complete in time. */
    data object Timeout : NetworkError {
        override val message: String get() = "The request timed out"
    }

    /** A response was received but its body could not be parsed. */
    data class Serialization(val detail: String? = null) : NetworkError {
        override val message: String get() = detail ?: "Failed to parse the server response"
    }

    /** Any other, unclassified failure. */
    data class Unknown(val detail: String? = null) : NetworkError {
        override val message: String get() = detail ?: "An unexpected error occurred"
    }
}
