package com.jj.templateproject.domain.createaccount

import com.jj.templateproject.domain.UseCase

class CreateAccountUseCase(
    private val createAccountRepository: CreateAccountRepository,
) : UseCase<CreateAccountUseCase.CreateAccountUseCaseParam, Unit> {

    data class CreateAccountUseCaseParam(
        val username: String,
        val password: String,
    )

    override suspend fun invoke(param: CreateAccountUseCaseParam) {
        createAccountRepository.createAccount(
            username = param.username,
            password = param.password,
        )
    }
}