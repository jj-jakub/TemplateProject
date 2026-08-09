package com.jj.templateproject.data.google

import com.jj.templateproject.data.google.network.TemplateNetworkApi
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

/** Hand-written stand-in for [TemplateNetworkApi], the same reasoning as
 *  [com.jj.templateproject.domain.google.FakeTemplateRepository] in `:domain`. */
class FakeTemplateNetwork(
    var dataResult: BaseResult<String, NetworkError> = BaseResult.Success("200"),
    var statusResult: BaseResult<Unit, NetworkError> = BaseResult.Success(Unit),
) : TemplateNetworkApi {

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
