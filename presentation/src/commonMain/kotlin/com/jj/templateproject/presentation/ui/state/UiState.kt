package com.jj.templateproject.presentation.ui.state

import com.jj.templateproject.domain.BaseError
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.fold
import com.jj.templateproject.domain.google.exception.NetworkError

/**
 * The canonical content-state for a screen (or a section of one), so every screen models
 * loading/success/error/empty the same type-safe way and can never silently forget an error path.
 *
 * Pair it with `UiStateContent` to render the matching design-system slot.
 */
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
    data object Empty : UiState<Nothing>
}

val UiState<*>.isLoading: Boolean get() = this is UiState.Loading

val UiState<*>.isSuccess: Boolean get() = this is UiState.Success

/** The success value, or `null` for any non-success state. */
fun <T> UiState<T>.dataOrNull(): T? = (this as? UiState.Success)?.data

/** Transforms a success value; other states pass through unchanged. */
inline fun <T, R> UiState<T>.map(transform: (T) -> R): UiState<R> = when (this) {
    is UiState.Success -> UiState.Success(transform(data))
    UiState.Loading -> UiState.Loading
    UiState.Empty -> UiState.Empty
    is UiState.Error -> this
}

/** Bridges a [BaseResult] to a [UiState], deriving the error message from [errorMessage]. */
inline fun <T, E : BaseError> BaseResult<T, E>.toUiState(
    errorMessage: (E) -> String,
): UiState<T> = fold(
    onSuccess = { UiState.Success(it) },
    onError = { UiState.Error(errorMessage(it)) },
)

/** Bridges a networking [BaseResult] to a [UiState] using [NetworkError.message]. */
fun <T> BaseResult<T, NetworkError>.toUiState(): UiState<T> = toUiState { it.message }
