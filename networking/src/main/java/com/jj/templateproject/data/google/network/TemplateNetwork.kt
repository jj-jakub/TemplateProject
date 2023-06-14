package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.utils.NetworkException
import com.jj.templateproject.data.utils.toResult
import com.jj.templateproject.domain.BaseResult

class TemplateNetwork(
    private val templateService: TemplateService,
) {

    suspend fun getGoogleData(): BaseResult<String> {
        val result = templateService.getGoogleData()
        return if (result.isSuccessful) {
            BaseResult.Success(result.code().toString())
        } else {
            BaseResult.Error(NetworkException(result.code(), result.message()))
        }
    }

    suspend fun getGoogleStatus(): BaseResult<Unit> = templateService.getGoogleData().toResult()
}