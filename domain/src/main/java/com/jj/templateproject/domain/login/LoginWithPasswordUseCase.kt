package com.jj.templateproject.domain.login

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.UseCase
import com.jj.templateproject.domain.login.model.LoginType

class LoginWithPasswordUseCase(
    private val loginRepository: LoginRepository,
) : UseCase<LoginWithPasswordUseCase.LoginWithPasswordParams, BaseResult<Unit>> {

    data class LoginWithPasswordParams(
        val username: String,
        val password: String,
    )

    override suspend fun invoke(param: LoginWithPasswordParams): BaseResult<Unit> =
        loginRepository.login(
             LoginType.LoginWithPassword(
                 username = param.username,
                 password = param.password,
             )
         )
}