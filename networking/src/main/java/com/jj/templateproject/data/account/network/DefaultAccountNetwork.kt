package com.jj.templateproject.data.account.network

import com.jj.templateproject.data.account.AccountService
import com.jj.templateproject.data.account.model.RawCreateAccountRequest
import com.jj.templateproject.data.utils.NetworkException
import com.jj.templateproject.domain.BaseResult

class DefaultAccountNetwork(
    private val accountService: AccountService,
): AccountNetwork {
    override suspend fun createAccount(username: String, password: String): BaseResult<String> {
        val result = accountService.createAccount(
            RawCreateAccountRequest(
                username = username,
                password = password,
            )
        )
        return if (result.isSuccessful) {
            BaseResult.Success(result.code().toString())
        } else {
            BaseResult.Error(NetworkException(result.code(), result.message()))
        }
    }
}