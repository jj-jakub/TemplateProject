package com.jj.templateproject.data.account.network

import com.jj.templateproject.domain.BaseResult

class MockAccountNetwork : AccountNetwork {
    override suspend fun createAccount(username: String, password: String): BaseResult<String> =
        BaseResult.Success("200")
}