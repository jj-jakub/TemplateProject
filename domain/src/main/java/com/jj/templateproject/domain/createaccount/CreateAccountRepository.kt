package com.jj.templateproject.domain.createaccount

interface CreateAccountRepository {
    suspend fun createAccount(username: String, password: String)
}