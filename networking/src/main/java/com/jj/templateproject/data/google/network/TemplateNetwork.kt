package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.utils.toResult
import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

class TemplateNetwork(
    private val templateService: TemplateService,
) {

    suspend fun getGoogleData(): BaseResult<String, NetworkError> =
        templateService.getGoogleData().toResult { response -> response.code().toString() }

    suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError> =
        templateService.getGoogleStatus().toResult { }
}
