package com.jj.templateproject.domain.google

import com.jj.templateproject.domain.BaseResult

interface TemplateRepository {
    suspend fun getGoogleData(): BaseResult<String>
    suspend fun getGoogleStatus(): BaseResult<Unit>
}