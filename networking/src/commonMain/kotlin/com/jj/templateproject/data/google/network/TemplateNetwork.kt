package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.utils.safeApiCall
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

/**
 * [TemplateNetwork]'s own contract, extracted so [DefaultTemplateRepository] (and its tests) can
 * depend on the interface rather than the concrete class — the same boundary
 * [TemplateRepository][com.jj.templateproject.domain.google.TemplateRepository] already draws one
 * layer up, and what lets a hand-written fake stand in for this layer in tests, the same way
 * `TemplateRepository` has one of its own.
 */
interface TemplateNetworkApi {
    suspend fun getGoogleData(): BaseResult<String, NetworkError>
    suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError>
}

class TemplateNetwork(
    private val templateService: TemplateService,
) : TemplateNetworkApi {

    override suspend fun getGoogleData(): BaseResult<String, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleData() }) { response ->
            response.status.value.toString()
        }

    override suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleStatus() }) { }
}
