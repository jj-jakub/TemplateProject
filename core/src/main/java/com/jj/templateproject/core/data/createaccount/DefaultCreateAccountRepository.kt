package com.jj.templateproject.core.data.createaccount

import com.jj.templateproject.data.account.network.AccountNetwork
import com.jj.templateproject.domain.createaccount.CreateAccountRepository

class DefaultCreateAccountRepository(
    private val accountNetwork: AccountNetwork,
) : CreateAccountRepository {
    override suspend fun createAccount(username: String, password: String) {
        accountNetwork.createAccount(username, password)
    }
}