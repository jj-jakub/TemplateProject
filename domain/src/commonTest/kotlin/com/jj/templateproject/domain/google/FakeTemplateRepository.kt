package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

/**
 * Hand-written stand-in for [TemplateRepository].
 *
 * A fake rather than a mock because these tests have to run on every target, and the mocking
 * libraries that make a mock cheap are JVM-only. It costs a dozen lines and reads better than the
 * `coEvery`/`coVerify` pair it replaces: what it returns and how often it was called are both plain
 * properties.
 */
class FakeTemplateRepository(
    var dataResult: BaseResult<String, NetworkError> = BaseResult.Success("200"),
    var statusResult: BaseResult<Unit, NetworkError> = BaseResult.Success(Unit),
) : TemplateRepository {

    var dataCallCount = 0
        private set

    var statusCallCount = 0
        private set

    override suspend fun getGoogleData(): BaseResult<String, NetworkError> {
        dataCallCount++
        return dataResult
    }

    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> {
        statusCallCount++
        return statusResult
    }
}
