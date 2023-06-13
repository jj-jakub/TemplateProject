package com.jj.templateproject.data.network.network

import com.jj.templateproject.data.Result
import com.jj.templateproject.data.network.service.TemplateService
import com.jj.templateproject.data.network.utils.NetworkException
import com.jj.templateproject.data.network.utils.toResult

class TemplateNetwork(
    private val templateService: TemplateService,
) {

    suspend fun getGoogleData(): Result<String> {
        val result = templateService.getGoogleData()
        return if (result.isSuccessful) {
            Result.Success(result.code().toString())
        } else {
            Result.Error(NetworkException(result.code(), result.message()))
        }
    }

    suspend fun getGoogleStatus(): Result<Unit> = templateService.getGoogleData().toResult()
}