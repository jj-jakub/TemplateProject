package com.jj.templateproject.data.account.network

import com.jj.templateproject.data.account.AccountService
import com.jj.templateproject.data.account.model.RawCreateAccountRequest
import com.jj.templateproject.data.utils.NetworkCallResult
import com.jj.templateproject.data.utils.safeCall

class DefaultAccountNetwork(
    private val accountService: AccountService,
) : AccountNetwork {
    override suspend fun createAccount(
        username: String,
        password: String,
    ): NetworkCallResult<Unit> = safeCall {
        accountService.createAccount(
            RawCreateAccountRequest(
                username = username,
                password = password,
            )
        )
    }
}