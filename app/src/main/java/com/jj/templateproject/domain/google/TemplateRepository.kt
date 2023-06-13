package com.jj.templateproject.domain.google

import com.jj.templateproject.data.Result

interface TemplateRepository {
    suspend fun getGoogleData(): Result<String>
    suspend fun getGoogleStatus(): Result<Unit>
}