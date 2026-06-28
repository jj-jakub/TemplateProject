package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.utils.safeApiCall
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

class TemplateNetwork(
    private val templateService: TemplateService,
) {

    suspend fun getGoogleData(): BaseResult<String, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleData() }) { response ->
            response.code().toString()
        }

    suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        safeApiCall(apiCall = { templateService.getGoogleStatus() }) { }
}
