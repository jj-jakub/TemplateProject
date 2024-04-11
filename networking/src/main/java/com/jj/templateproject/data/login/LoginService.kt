package com.jj.templateproject.data.login

import com.jj.templateproject.data.login.model.RawLoginRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/login")
    suspend fun login(@Body rawLoginRequest: RawLoginRequest): Response<Unit>
}