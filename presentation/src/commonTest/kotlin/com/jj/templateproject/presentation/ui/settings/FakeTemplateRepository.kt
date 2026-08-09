package com.jj.templateproject.presentation.ui.settings

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.TemplateRepository
import com.jj.templateproject.domain.google.exception.NetworkError

/** The same fake as `:domain`'s own `FakeTemplateRepository`, duplicated rather than shared: test
 *  sources are not visible across module boundaries in this project's Gradle setup. */
class FakeTemplateRepository(
    var dataResult: BaseResult<String, NetworkError> = BaseResult.Success("200"),
    var statusResult: BaseResult<Unit, NetworkError> = BaseResult.Success(Unit),
) : TemplateRepository {

    var statusCallCount = 0
        private set

    override suspend fun getGoogleData(): BaseResult<String, NetworkError> = dataResult

    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> {
        statusCallCount++
        return statusResult
    }
}
