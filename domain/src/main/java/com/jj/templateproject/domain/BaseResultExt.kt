package com.jj.templateproject.domain

/**
 * Functional extensions for [BaseResult], so callers transform and consume results without
 * repeating `when (result) { is Success -> …; is Error -> … }` at every call site.
 *
 * All operators are `inline` (zero allocation for the lambdas) and pure Kotlin, so they live in
 * `:domain` and are usable from every layer.
 */

/** `true` if this is [BaseResult.Success]. */
val BaseResult<*, *>.isSuccess: Boolean get() = this is BaseResult.Success

/** `true` if this is [BaseResult.Error]. */
val BaseResult<*, *>.isError: Boolean get() = this is BaseResult.Error

/** Collapses both branches into a single value. */
inline fun <Data, Err : BaseError, R> BaseResult<Data, Err>.fold(
    onSuccess: (Data) -> R,
    onError: (Err) -> R,
): R = when (this) {
    is BaseResult.Success -> onSuccess(data)
    is BaseResult.Error -> onError(error)
}

/** Transforms the success value, leaving an error untouched. */
inline fun <Data, Err : BaseError, R> BaseResult<Data, Err>.map(
    transform: (Data) -> R,
): BaseResult<R, Err> = when (this) {
    is BaseResult.Success -> BaseResult.Success(transform(data))
    is BaseResult.Error -> BaseResult.Error(error)
}

/** Transforms the error value, leaving a success untouched. */
inline fun <Data, Err : BaseError, F : BaseError> BaseResult<Data, Err>.mapError(
    transform: (Err) -> F,
): BaseResult<Data, F> = when (this) {
    is BaseResult.Success -> BaseResult.Success(data)
    is BaseResult.Error -> BaseResult.Error(transform(error))
}

/** Chains another result-producing operation onto a success (monadic bind). */
inline fun <Data, Err : BaseError, R> BaseResult<Data, Err>.flatMap(
    transform: (Data) -> BaseResult<R, Err>,
): BaseResult<R, Err> = when (this) {
    is BaseResult.Success -> transform(data)
    is BaseResult.Error -> BaseResult.Error(error)
}

/** Runs [action] on the success value and returns the original result, for side effects. */
inline fun <Data, Err : BaseError> BaseResult<Data, Err>.onSuccess(
    action: (Data) -> Unit,
): BaseResult<Data, Err> {
    if (this is BaseResult.Success) action(data)
    return this
}

/** Runs [action] on the error and returns the original result, for side effects. */
inline fun <Data, Err : BaseError> BaseResult<Data, Err>.onError(
    action: (Err) -> Unit,
): BaseResult<Data, Err> {
    if (this is BaseResult.Error) action(error)
    return this
}

/** The success value, or `null` for an error. */
fun <Data, Err : BaseError> BaseResult<Data, Err>.getOrNull(): Data? = when (this) {
    is BaseResult.Success -> data
    is BaseResult.Error -> null
}

/** The error, or `null` for a success. */
fun <Data, Err : BaseError> BaseResult<Data, Err>.errorOrNull(): Err? = when (this) {
    is BaseResult.Success -> null
    is BaseResult.Error -> error
}

/** The success value, or the result of [fallback] for an error. */
inline fun <Data, Err : BaseError> BaseResult<Data, Err>.getOrElse(
    fallback: (Err) -> Data,
): Data = when (this) {
    is BaseResult.Success -> data
    is BaseResult.Error -> fallback(error)
}

/** The success value, or [default] for an error. */
fun <Data, Err : BaseError> BaseResult<Data, Err>.getOrDefault(default: Data): Data = when (this) {
    is BaseResult.Success -> data
    is BaseResult.Error -> default
}

/** Turns an error into a success by computing a value from it; a success is returned unchanged. */
inline fun <Data, Err : BaseError> BaseResult<Data, Err>.recover(
    transform: (Err) -> Data,
): BaseResult<Data, Err> = when (this) {
    is BaseResult.Success -> this
    is BaseResult.Error -> BaseResult.Success(transform(error))
}
