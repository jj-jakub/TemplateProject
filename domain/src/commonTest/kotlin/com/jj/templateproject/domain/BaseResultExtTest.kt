package com.jj.templateproject.domain

import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.Test

private data class TestError(val reason: String) : BaseError

class BaseResultExtTest {

    private val success: BaseResult<Int, TestError> = BaseResult.Success(2)
    private val failure: BaseResult<Int, TestError> = BaseResult.Error(TestError("boom"))

    @Test
    fun `isSuccess and isError reflect the branch`() {
        assertTrue(success.isSuccess)
        assertFalse(success.isError)
        assertTrue(failure.isError)
        assertFalse(failure.isSuccess)
    }

    @Test
    fun `fold collapses both branches`() {
        assertEquals("ok:2", success.fold(onSuccess = { "ok:$it" }, onError = { "err:${it.reason}" }))
        assertEquals("err:boom", failure.fold(onSuccess = { "ok:$it" }, onError = { "err:${it.reason}" }))
    }

    @Test
    fun `map transforms only success`() {
        assertEquals(BaseResult.Success<String, TestError>("4"), success.map { (it * 2).toString() })
        // Spelled out rather than compared against `failure` itself: mapping changes the success
        // type, so the two sides are different types and only the error is expected to survive.
        assertEquals(
            BaseResult.Error<String, TestError>(TestError("boom")),
            failure.map { (it * 2).toString() },
        )
    }

    @Test
    fun `mapError transforms only error`() {
        val mapped = failure.mapError { TestError(it.reason.uppercase()) }
        assertEquals(BaseResult.Error<Int, TestError>(TestError("BOOM")), mapped)
        assertEquals(success, success.mapError { TestError(it.reason.uppercase()) })
    }

    @Test
    fun `flatMap chains success and short-circuits error`() {
        assertEquals(
            BaseResult.Success<Int, TestError>(20),
            success.flatMap { BaseResult.Success(it * 10) },
        )
        assertEquals(failure, failure.flatMap { BaseResult.Success(it * 10) })
        val chainedError: BaseResult<Int, TestError> = BaseResult.Error(TestError("downstream"))
        assertEquals(chainedError, success.flatMap { chainedError })
    }

    @Test
    fun `onSuccess and onError run the matching side effect only`() {
        var seen = ""
        success.onSuccess { seen = "s:$it" }.onError { seen = "e" }
        assertEquals("s:2", seen)

        seen = ""
        failure.onSuccess { seen = "s" }.onError { seen = "e:${it.reason}" }
        assertEquals("e:boom", seen)
    }

    @Test
    fun `getOrNull and errorOrNull pick the present branch`() {
        assertEquals(2, success.getOrNull())
        assertNull(failure.getOrNull())
        assertNull(success.errorOrNull())
        assertEquals(TestError("boom"), failure.errorOrNull())
    }

    @Test
    fun `getOrElse and getOrDefault supply fallbacks for errors`() {
        assertEquals(2, success.getOrElse { -1 })
        assertEquals(-1, failure.getOrElse { -1 })
        assertEquals(2, success.getOrDefault(99))
        assertEquals(99, failure.getOrDefault(99))
    }

    @Test
    fun `recover converts an error into a success and leaves success untouched`() {
        assertEquals(BaseResult.Success<Int, TestError>(0), failure.recover { 0 })
        assertSame(success, success.recover { 0 })
    }
}
