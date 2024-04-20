package com.jj.templateproject.data.google.network

import com.jj.templateproject.data.google.service.TemplateService
import com.jj.templateproject.data.utils.NetworkCallResult
import com.jj.templateproject.data.utils.safeCall
import retrofit2.Response

class TemplateNetwork(
    private val templateService: TemplateService,
) {

    suspend fun getGoogleData(): NetworkCallResult<String> {
        val result = safeCall {
            templateService.getGoogleData()
        }
        return when (result) {
            is NetworkCallResult.Success -> NetworkCallResult.Success(
                Response.success(
                    result.response.code(),
                    result.response.code().toString(),
                )
            )

            is NetworkCallResult.Failure -> NetworkCallResult.Failure(result.exception)
        }
    }

    suspend fun getGoogleStatus(): NetworkCallResult<Unit> =
        safeCall { templateService.getGoogleData() }
}