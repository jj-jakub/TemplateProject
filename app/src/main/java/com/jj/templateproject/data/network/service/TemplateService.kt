package com.jj.templateproject.data.network.service

import retrofit2.Response
import retrofit2.http.GET

interface TemplateService {
    @GET("/")
    suspend fun getGoogleData(): Response<Unit>
}