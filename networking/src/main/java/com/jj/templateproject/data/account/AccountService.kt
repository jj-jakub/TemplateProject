package com.jj.templateproject.data.account

import com.jj.templateproject.data.account.model.RawCreateAccountRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AccountService {
    @POST("/account")
    suspend fun createAccount(@Body rawCreateAccountRequest: RawCreateAccountRequest): Response<Unit>
}