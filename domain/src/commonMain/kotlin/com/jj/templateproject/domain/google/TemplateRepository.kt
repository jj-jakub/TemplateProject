package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult
import com.jj.templateproject.domain.google.exception.NetworkError

interface TemplateRepository {
    suspend fun getGoogleData(): BaseResult<String, NetworkError>
    suspend fun getGoogleStatus(): BaseResult<Unit, NetworkError>
}
